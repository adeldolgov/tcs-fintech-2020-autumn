package com.adeldolgov.feeder.data.repository

import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.mapper.toCareerItem
import com.adeldolgov.feeder.data.mapper.toProfileEntity
import com.adeldolgov.feeder.data.mapper.toProfileItem
import com.adeldolgov.feeder.data.mapper.toSource
import com.adeldolgov.feeder.data.pojo.*
import com.adeldolgov.feeder.data.server.VkService
import com.adeldolgov.feeder.ui.item.CareerItem
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.item.ProfileItem
import com.adeldolgov.feeder.util.PostTypes
import com.adeldolgov.feeder.util.error.EmptyDataException
import com.adeldolgov.feeder.util.networkavailability.NetworkAvailability
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class ProfileRepository @Inject constructor(
    override val vkService: VkService,
    override val appDatabase: AppDatabase,
    override val postTypes: PostTypes,
    override val networkAvailability: NetworkAvailability
): BasePostRepository<WallPost>() {

    override fun loadPostsAndGroupsFromNetwork(count: Int, offset: Int): Single<BaseItemsHolder<WallPost>> {
        return vkService.getPostsFromWall(count, offset)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response
            }
    }

    fun getProfile(): Single<ProfileItem> {
        return if (networkAvailability.isNetworkAvailable())
            loadActualProfileFromNetworkAndUpdateLocal().doOnSubscribe { deleteProfileInDatabase() }
        else
            loadProfileFromDatabase()
    }

    private fun loadActualProfileFromNetworkAndUpdateLocal(): Single<ProfileItem> {
        return vkService
            .getUser()
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response.first()
            }
            .map { Pair(it, loadProfileCareerInformation(it).blockingGet()) }
            .doOnSuccess { insertProfileToDatabase(it.first, it.second.toTypedArray()) }
            .map { loadProfileFromDatabase().blockingGet() }
    }

    private fun loadProfileFromDatabase(): Single<ProfileItem> {
        return appDatabase.profileDao()
            .getProfiles()
            .map {
                if (it.isEmpty()) throw EmptyDataException("Profile is empty.")
                it.first().toProfileItem()
            }
    }

    private fun loadProfileCareerInformation(profile: Profile): Single<List<CareerItem>> {
        return Observable
            .fromIterable(profile.career.toList())
            .map {
                it.toCareerItem(
                    it.groupId?.let { groupId -> loadGroupById(groupId).blockingGet() },
                    it.countryId?.let { countryId -> loadCountryById(countryId).blockingGet() },
                    it.cityId?.let { cityId -> loadCityById(cityId).blockingGet() }
                )
            }
            .toList()
    }

    private fun loadGroupById(groupId: Long): Single<Source> {
        return vkService.getGroupById(groupId)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response.first()
            }
    }

    private fun loadCountryById(countryId: Int): Single<Location> {
        return vkService.getCountryById(countryId)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response.first()
            }
    }

    private fun loadCityById(cityId: Int): Single<Location> {
        return vkService.getCityById(cityId)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response.first()
            }
    }

    fun createPost(message: String, photo: File?): Single<List<PostItem>> {
        return Single.just(photo != null)
            .map {
                val uploadedPhoto = if (it) uploadWallPhoto(photo!!).blockingGet().first() else null
                fillPostFields(message, uploadedPhoto)
            }
            .map {  vkService.createPostAtWall(it).blockingGet() }
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response.postId
            }
            .map { vkService.getPostsFromWall(1, 0).blockingGet() }
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
            .doOnSuccess { updateDataInDatabase(it.items, it.groups.plus(it.profiles.map { profile ->  profile.toSource() })) }
            .map { loadPostsFromDatabase().blockingGet() }
    }

    private fun uploadWallPhoto(photo: File): Single<List<Photo>> {
        return vkService.getWallUploadServer()
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
            .map { uploadPhotoToServer(it.uploadUrl, photo).blockingGet() }
            .map { saveWallPhoto(it).blockingGet() }
    }

    private fun uploadPhotoToServer(uploadUrl: String, photo: File): Single<UploadedPhoto> {
        return Single
            .fromCallable {
                val photoRequest = RequestBody.create(MediaType.parse("image/*"), photo)
                MultipartBody.Part.createFormData("photo", photo.getName(), photoRequest)
            }
            .map { vkService.uploadPhotoToUrl(uploadUrl, it).blockingGet() }
    }

    private fun saveWallPhoto(uploadedPhoto: UploadedPhoto): Single<List<Photo>> {
        return vkService.saveWallPhoto(uploadedPhoto.photo, uploadedPhoto.server, uploadedPhoto.hash)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
    }

    private fun fillPostFields(message: String, photo: Photo?): HashMap<String, Any> {
        return HashMap<String, Any>().apply {
            put("message", message)
            photo?.let { put("attachments", "photo${photo.ownerId}_${photo.id}") }
        }
    }

    private fun insertProfileToDatabase(profile: Profile, career: Array<CareerItem>) {
        appDatabase.profileDao().insertAll(profile.toProfileEntity(career))
    }

    private fun deleteProfileInDatabase() {
        appDatabase.profileDao().deleteDataFromTable()
    }

}