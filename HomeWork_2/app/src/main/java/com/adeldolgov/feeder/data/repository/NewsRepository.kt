package com.adeldolgov.feeder.data.repository

import com.adeldolgov.feeder.api.VKService
import com.adeldolgov.feeder.data.pojo.Count
import com.adeldolgov.feeder.data.pojo.NewsFeed
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class NewsRepository {
    fun getNewsFeedPosts(): Single<NewsFeed> {
        return VKService.vkService
            .getNewsFeedPosts()
            .subscribeOn(Schedulers.io())
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
    }

    fun addLikeAtPost(postId: Long, ownerId: Long): Single<Count> {
        return VKService.vkService
            .addLikeAtPost(postId, ownerId)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
    }

    fun deleteLikeAtPost(postId: Long, ownerId: Long): Single<Count> {
        return VKService.vkService
            .deleteLikeAtPost(postId, ownerId)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
    }

    fun ignorePost(postId: Long, ownerId: Long): Single<Int> {
        return VKService.vkService
            .ignorePost(postId, ownerId)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
    }

}