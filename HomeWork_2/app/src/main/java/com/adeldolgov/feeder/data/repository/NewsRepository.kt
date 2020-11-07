package com.adeldolgov.feeder.data.repository

import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.data.database.AppDatabaseImpl
import com.adeldolgov.feeder.data.mapper.toPost
import com.adeldolgov.feeder.data.mapper.toPostEntity
import com.adeldolgov.feeder.data.mapper.toSource
import com.adeldolgov.feeder.data.mapper.toSourceEntity
import com.adeldolgov.feeder.data.pojo.Count
import com.adeldolgov.feeder.data.pojo.NewsFeed
import com.adeldolgov.feeder.data.pojo.Post
import com.adeldolgov.feeder.data.pojo.Source
import com.adeldolgov.feeder.data.server.VKServiceImpl
import com.adeldolgov.feeder.util.timeoutpolicy.PreferencesCacheTimeout
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException

class NewsRepository {

    private val vkService = VKServiceImpl().vkService
    private val appDataBase = AppDatabaseImpl().db
    private val cacheTimeoutPolicy = PreferencesCacheTimeout()

    fun getNewsFeedAtStart(ignoreCacheTimeout: Boolean, count: Int): Single<NewsFeed> {
        return if (FeederApp.isNetworkAvailable && (!cacheTimeoutPolicy.isValid() || ignoreCacheTimeout))
            loadActualDataFromNetworkAndUpdateLocal(count, 0).doOnSubscribe { deleteDataInDatabase() }
        else
            loadLocalDataFromDatabase()
    }

    fun fetchNewsFeed(count: Int, startFrom: Int): Single<NewsFeed> {
        return Single.just(FeederApp.isNetworkAvailable)
            .map {
                if (it) loadActualDataFromNetworkAndUpdateLocal(count, startFrom).blockingGet()
                else throw UnknownHostException()
            }
    }

    fun addLikeAtPost(postId: Long, ownerId: Long): Single<Count> {
        return vkService
            .addLikeAtPost(postId, ownerId)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
            .doOnSuccess { appDataBase.postDao().updatePostLike(postId, true, it.likes) }

    }

    fun deleteLikeAtPost(postId: Long, ownerId: Long): Single<Count> {
        return vkService
            .deleteLikeAtPost(postId, ownerId)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
            .doOnSuccess { appDataBase.postDao().updatePostLike(postId, false, it.likes) }
    }

    fun ignorePost(postId: Long, ownerId: Long): Single<Int> {
        return vkService
            .ignorePost(postId, ownerId)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
            .doOnSuccess { appDataBase.postDao().deletePostById(postId) }
    }

    private fun loadActualDataFromNetworkAndUpdateLocal(count: Int, startFrom: Int): Single<NewsFeed> {
        return vkService
            .getNewsFeedPosts(count, startFrom)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
            .doOnSuccess { updateDataInDatabase(it.groups, it.items) }
    }

    private fun loadLocalDataFromDatabase(): Single<NewsFeed> {
        return getSourcesFromDatabase()
            .zipWith(getPostsFromDatabase()) { sources: List<Source>, posts: List<Post> ->
                NewsFeed(posts, emptyList(), sources, "")
            }
    }

    private fun updateDataInDatabase(sources: List<Source>, posts: List<Post>) {
        appDataBase.runInTransaction{
            insertSourcesToDatabase(sources)
            insertPostsToDatabase(posts)
        }
        cacheTimeoutPolicy.setLatestTime(System.currentTimeMillis())
    }

    private fun deleteDataInDatabase() {
        appDataBase.runInTransaction{
            appDataBase.postDao().deleteDataFromTable()
            appDataBase.sourceDao().deleteDataFromTable()
        }
    }

    private fun insertSourcesToDatabase(sources: List<Source>) {
        appDataBase.sourceDao().insertAll(*sources.map {
            it.toSourceEntity()
        }.toTypedArray())
    }

    private fun insertPostsToDatabase(posts: List<Post>) {
        appDataBase.postDao().insertAll(*posts.map {
            it.toPostEntity()
        }.toTypedArray())
    }

    private fun getPostsFromDatabase(): Single<List<Post>> {
        return appDataBase.postDao()
            .getPosts()
            .subscribeOn(Schedulers.io())
            .map { it.map { postEntity -> postEntity.toPost() } }
    }

    private fun getSourcesFromDatabase(): Single<List<Source>> {
        return appDataBase.sourceDao()
            .getSources()
            .subscribeOn(Schedulers.io())
            .map { it.map { sourceEntity -> sourceEntity.toSource() } }
    }

}