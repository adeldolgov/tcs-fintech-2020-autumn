package com.adeldolgov.feeder.data.server

import com.adeldolgov.feeder.data.pojo.*
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface VkService {

    @GET("newsfeed.get?filters=post")
    fun getNewsFeedPosts(
        @Query("count") count: Int,
        @Query("start_from") start_from: Int
    ): Single<ApiResponse<NewsFeed>>

    @FormUrlEncoded
    @POST("likes.add?type=post")
    fun addLikeAtPost(
        @Field("item_id") postId: Long,
        @Field("owner_id") ownerId: Long
    ): Single<ApiResponse<Count>>

    @FormUrlEncoded
    @POST("likes.delete?type=post")
    fun deleteLikeAtPost(
        @Field("item_id") id: Long,
        @Field("owner_id") ownerId: Long
    ): Single<ApiResponse<Count>>

    @FormUrlEncoded
    @POST("newsfeed.ignoreItem?type=wall")
    fun ignorePost(
        @Field("item_id") id: Long,
        @Field("owner_id") ownerId: Long
    ): Single<ApiResponse<Int>>

    @FormUrlEncoded
    @POST("wall.delete")
    fun deletePost(
        @Field("post_id") id: Long,
        @Field("owner_id") ownerId: Long
    ): Single<ApiResponse<Int>>

    @GET("wall.getComments?extended=true&need_likes=true")
    fun getComments(
        @Query("post_id") postId: Long, @Query("owner_id") ownerId: Long,
        @Query("count") count: Int, @Query("offset") offset: Int
    ): Single<ApiResponse<PostComments>>

    @FormUrlEncoded
    @POST("wall.createComment")
    fun createComment(
        @Field("post_id") postId: Long, @Field("owner_id") ownerId: Long,
        @Field("message") message: String
    ): Single<ApiResponse<CreatedComment>>

    @GET("wall.getComment?extended=true")
    fun getComment(
        @Query("comment_id") commentId: Long,
        @Query("owner_id") ownerId: Long
    ): Single<ApiResponse<PostComments>>

    @GET("users.get?fields=first_name, last_name, photo_400_orig, photo_100, about, " +
            "bdate, city, country, career, education, followers_count, domain, last_seen&name_case=Nom")
    fun getUser(): Single<ApiResponse<List<Profile>>>

    @GET("wall.get?extended=true&filter=owner")
    fun getPostsFromWall(
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): Single<ApiResponse<Wall>>

    @GET("groups.getById")
    fun getGroupById(@Query("group_id") groupId: Long): Single<ApiResponse<List<Source>>>

    @GET("database.getCountriesById")
    fun getCountryById(@Query("country_ids") countryId: Int): Single<ApiResponse<List<Location>>>

    @GET("database.getCitiesById")
    fun getCityById(@Query("city_ids") cityId: Int): Single<ApiResponse<List<Location>>>

    @FormUrlEncoded
    @POST("wall.post")
    fun createPostAtWall(@FieldMap fields: HashMap<String, Any>): Single<ApiResponse<CreatedPost>>

    @GET("photos.getWallUploadServer")
    fun getWallUploadServer(): Single<ApiResponse<UploadServer>>

    @Multipart
    @POST
    fun uploadPhotoToUrl(@Url uploadUrl: String, @Part photo: MultipartBody.Part): Single<UploadedPhoto>

    @FormUrlEncoded
    @POST("photos.saveWallPhoto")
    fun saveWallPhoto(
        @Field("photo") photo: String,
        @Field("server") server: Int,
        @Field("hash") hash: String
    ): Single<ApiResponse<List<Photo>>>
}