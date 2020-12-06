package com.adeldolgov.feeder.data.repository

import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.pojo.BaseItemsHolder
import com.adeldolgov.feeder.data.pojo.BasePost
import com.adeldolgov.feeder.data.pojo.NewsFeedPost
import com.adeldolgov.feeder.data.pojo.Source
import com.adeldolgov.feeder.data.server.VkService
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.util.PostTypes
import com.adeldolgov.feeder.util.networkavailability.NetworkAvailability
import com.adeldolgov.feeder.util.timeoutpolicy.CacheTimeoutPolicy
import io.reactivex.Single
import javax.inject.Inject

class NewsFeedRepository @Inject constructor(
    override val vkService: VkService,
    override val appDatabase: AppDatabase,
    override val postTypes: PostTypes,
    override val networkAvailability: NetworkAvailability,
    private val cacheTimeoutPolicy: CacheTimeoutPolicy
): BasePostRepository<NewsFeedPost>() {

    override fun loadPostsAndGroupsFromNetwork(count: Int, offset: Int): Single<BaseItemsHolder<NewsFeedPost>> {
        return vkService.getNewsFeedPosts(count, offset)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response
            }
    }

    fun updatePostsIfNeeds(ignoreCacheTimeout: Boolean, count: Int): Single<List<PostItem>> {
        return if (networkAvailability.isNetworkAvailable() && (!cacheTimeoutPolicy.isValid() || ignoreCacheTimeout))
            loadDataAtStart(count)
        else
            loadPostsFromDatabase()
    }

    override fun updateDataInDatabase(posts: List<BasePost>, sources: List<Source>) {
        super.updateDataInDatabase(posts, sources)
        cacheTimeoutPolicy.setLatestTime(System.currentTimeMillis())
    }

}