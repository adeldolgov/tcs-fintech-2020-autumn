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

class NewsRepository {

    private val vkService = VKServiceImpl().vkService
    private val appDataBase = AppDatabaseImpl().db
    private val cacheTimeoutPolicy = PreferencesCacheTimeout()

    fun getNewsFeedPosts(ignoreCacheTimeout: Boolean, count: Int, startFrom: Int): Single<NewsFeed> {
        return if (FeederApp.isNetworkAvailable && (!cacheTimeoutPolicy.isValid() || ignoreCacheTimeout))
            loadActualDataFromNetworkAndUpdateLocal(count, startFrom)
        else
            loadLocalDataFromDatabase(count, startFrom)
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
            .doOnSuccess { updateNonActualData(it.groups, it.items) }
    }

    private fun loadLocalDataFromDatabase(count: Int, startFrom: Int): Single<NewsFeed> {
        return getSourcesFromDatabase()
            .zipWith(getPostsFromDatabase(count, startFrom)) { sources: List<Source>, posts: List<Post> ->
                NewsFeed(posts, emptyList(), sources, "")
            }
    }

    private fun updateNonActualData(sources: List<Source>, posts: List<Post>) {
        appDataBase.sourceDao().deleteDataFromTable()
        appDataBase.postDao().deleteDataFromTable()
        insertSourcesToDatabase(sources)
        insertPostsToDatabase(posts)
        cacheTimeoutPolicy.setLatestTime(System.currentTimeMillis())
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

    private fun getPostsFromDatabase(limit: Int, offset: Int): Single<List<Post>> {
        return appDataBase.postDao()
            .getPosts(limit, offset)
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