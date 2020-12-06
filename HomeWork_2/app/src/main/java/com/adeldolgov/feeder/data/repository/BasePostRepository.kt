package com.adeldolgov.feeder.data.repository

import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.database.entity.PostEntity
import com.adeldolgov.feeder.data.database.entity.SourceEntity
import com.adeldolgov.feeder.data.mapper.toPostItem
import com.adeldolgov.feeder.data.mapper.toSource
import com.adeldolgov.feeder.data.mapper.toSourceEntity
import com.adeldolgov.feeder.data.pojo.BaseItemsHolder
import com.adeldolgov.feeder.data.pojo.BasePost
import com.adeldolgov.feeder.data.pojo.Source
import com.adeldolgov.feeder.data.server.VkService
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.util.PostTypes
import com.adeldolgov.feeder.util.error.NoNetworkException
import com.adeldolgov.feeder.util.networkavailability.NetworkAvailability
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith

abstract class BasePostRepository<T: BasePost> : BasePostActionRepository() {
    protected abstract val postTypes: PostTypes
    abstract override val appDatabase: AppDatabase
    abstract override val vkService: VkService
    protected abstract val networkAvailability: NetworkAvailability
    protected abstract fun loadPostsAndGroupsFromNetwork(count: Int, offset: Int): Single<BaseItemsHolder<T>>

    fun getPostsFlow(): Flowable<List<PostItem>> {
        return appDatabase.postDao().getPostsFlowable(postTypes)
            .map { mapPostsToPostItem(appDatabase.sourceDao().getSources().blockingGet(), it) }
    }

    fun getPosts(): Single<List<PostItem>> {
        return loadPostsFromDatabase()
    }

    fun fetchPosts(count: Int, offset: Int): Single<List<PostItem>> {
        return Single.just(networkAvailability.isNetworkAvailable())
            .map {
                if (it) loadActualDataFromNetworkAndUpdateLocal(count, offset).blockingGet()
                else throw NoNetworkException("No connection with host.")
            }
    }

    open fun loadDataAtStart(count: Int): Single<List<PostItem>> {
        return if (networkAvailability.isNetworkAvailable())
            loadActualDataFromNetworkAndUpdateLocal(count, 0)
        else
            loadPostsFromDatabase()
    }

    private fun loadActualDataFromNetworkAndUpdateLocal(count: Int, offset: Int): Single<List<PostItem>> {
        return loadPostsAndGroupsFromNetwork(count, offset)
            .doOnSuccess {
                val posts = it.items
                val sources = it.groups.plus(it.profiles.map { profile -> profile.toSource() })

                if (offset == 0) updateDataInDatabaseAndDeleteOld(posts, sources)
                else updateDataInDatabase(posts, sources)
            }
            .map { loadPostsFromDatabase().blockingGet() }
    }

    protected open fun updateDataInDatabase(posts: List<BasePost>, sources: List<Source>) {
        appDatabase.runInTransaction {
            appDatabase.sourceDao()
                .insertAll(*sources.map { it.toSourceEntity() }.toTypedArray())
            appDatabase.postDao()
                .insertAllWithPostTypeAddition(*posts.map { it.mapToPostEntity() }.toTypedArray())
        }
    }

    protected open fun updateDataInDatabaseAndDeleteOld(posts: List<BasePost>, sources: List<Source>) {
        appDatabase.runInTransaction{
            deleteDataInDatabase()
            updateDataInDatabase(posts, sources)
        }
    }

    private fun deleteDataInDatabase() {
        appDatabase.runInTransaction{
            appDatabase.postDao().deleteDataFromTableByTypesOrRemoveTypes(postTypes)
            appDatabase.sourceDao().deleteDataFromTableWhichHasNoReferences()
        }
    }

    private fun mapPostsToPostItem(groups: List<SourceEntity>, posts: List<PostEntity>): List<PostItem> {
        return posts.mapNotNull { post ->
            groups.find { source -> (source.id == post.sourceId) }?.let {
                post.toPostItem(it).takeIf { postItem -> postItem.id != 0L } //Заметил, как VK API багануло и вместо поста прислало мне тэг.
            }.takeIf { post.text.isNotEmpty() || post.attachment?.first()?.photo?.sizes?.size != null }
        }
    }

    protected fun loadPostsFromDatabase(): Single<List<PostItem>> {
        return appDatabase.postDao().getPosts(postTypes)
            .zipWith(appDatabase.sourceDao().getSources()) { posts: List<PostEntity>, sources: List<SourceEntity> ->
                mapPostsToPostItem(sources, posts)
            }
    }

}