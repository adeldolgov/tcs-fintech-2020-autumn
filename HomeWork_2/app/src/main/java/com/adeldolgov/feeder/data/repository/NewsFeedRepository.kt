package com.adeldolgov.feeder.data.repository

import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.mapper.toPost
import com.adeldolgov.feeder.data.mapper.toPostEntity
import com.adeldolgov.feeder.data.mapper.toSource
import com.adeldolgov.feeder.data.mapper.toSourceEntity
import com.adeldolgov.feeder.data.pojo.Count
import com.adeldolgov.feeder.data.pojo.NewsFeed
import com.adeldolgov.feeder.data.pojo.Post
import com.adeldolgov.feeder.data.pojo.Source
import com.adeldolgov.feeder.data.server.VKService
import com.adeldolgov.feeder.util.timeoutpolicy.CacheTimeoutPolicy
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import java.net.UnknownHostException

class NewsFeedRepository(
    private val vkService: VKService,
    private val appDatabase: AppDatabase,
    private val cacheTimeoutPolicy: CacheTimeoutPolicy
) {


    fun getNewsFeedAtStart(ignoreCacheTimeout: Boolean, count: Int): Single<NewsFeed> {
        return if (FeederApp.instance.isNetworkAvailable && (!cacheTimeoutPolicy.isValid() || ignoreCacheTimeout))
            loadActualDataFromNetworkAndUpdateLocal(count, 0).doOnSubscribe { deleteDataInDatabase() }
        else
            loadLocalDataFromDatabase()
    }

    fun fetchNewsFeed(count: Int, startFrom: Int): Single<NewsFeed> {
        return Single.just(FeederApp.instance.isNetworkAvailable)
            .map {
                if (it) loadActualDataFromNetworkAndUpdateLocal(count, startFrom).blockingGet()
                else throw UnknownHostException()
            }
    }

    fun addLikeAtPost(postId: Long, ownerId: Long): Single<Count> {
        return vkService
            .addLikeAtPost(postId, ownerId)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response
            }
            .doOnSuccess { appDatabase.postDao().updatePostLike(postId, true, it.likes) }

    }

    fun deleteLikeAtPost(postId: Long, ownerId: Long): Single<Count> {
        return vkService
            .deleteLikeAtPost(postId, ownerId)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response
            }
            .doOnSuccess { appDatabase.postDao().updatePostLike(postId, false, it.likes) }
    }

    fun ignorePost(postId: Long, ownerId: Long): Single<Int> {
        return vkService
            .ignorePost(postId, ownerId)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response
            }
            .doOnSuccess { appDatabase.postDao().deletePostById(postId) }
    }

    private fun loadActualDataFromNetworkAndUpdateLocal(count: Int, startFrom: Int): Single<NewsFeed> {
        return vkService
            .getNewsFeedPosts(count, startFrom)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response
            }
            .map { filterPostsFromGroup(it) }
            .doOnSuccess { updateDataInDatabase(it.groups, it.items) }
    }

    //source_id > 0 - user profile, source_id < 0 - group
    private fun filterPostsFromGroup(it: NewsFeed) =
        it.copy(items = it.items.filter { post -> post.sourceId < 0 })

    private fun loadLocalDataFromDatabase(): Single<NewsFeed> {
        return getSourcesFromDatabase()
            .zipWith(getPostsFromDatabase()) { sources: List<Source>, posts: List<Post> ->
                NewsFeed(posts, emptyList(), sources, "")
            }
    }

    private fun updateDataInDatabase(sources: List<Source>, posts: List<Post>) {
        appDatabase.runInTransaction{
            insertSourcesToDatabase(sources)
            insertPostsToDatabase(posts)
            cacheTimeoutPolicy.setLatestTime(System.currentTimeMillis())
        }
    }

    private fun deleteDataInDatabase() {
        appDatabase.runInTransaction{
            appDatabase.postDao().deleteDataFromTable()
            appDatabase.sourceDao().deleteDataFromTable()
        }
    }

    private fun insertSourcesToDatabase(sources: List<Source>) {
        appDatabase.sourceDao().insertAll(*sources.map {
            it.toSourceEntity()
        }.toTypedArray())
    }

    private fun insertPostsToDatabase(posts: List<Post>) {
        appDatabase.postDao().insertAll(*posts.map {
            it.toPostEntity()
        }.toTypedArray())
    }

    private fun getPostsFromDatabase(): Single<List<Post>> {
        return appDatabase.postDao()
            .getPosts()
            .map { it.map { postEntity -> postEntity.toPost() } }
    }

    private fun getSourcesFromDatabase(): Single<List<Source>> {
        return appDatabase.sourceDao()
            .getSources()
            .map { it.map { sourceEntity -> sourceEntity.toSource() } }
    }

}