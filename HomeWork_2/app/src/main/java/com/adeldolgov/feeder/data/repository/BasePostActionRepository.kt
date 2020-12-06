package com.adeldolgov.feeder.data.repository

import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.pojo.Count
import com.adeldolgov.feeder.data.server.VkService
import io.reactivex.Single

abstract class BasePostActionRepository {
    protected abstract val appDatabase: AppDatabase
    protected abstract val vkService: VkService

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

    fun deletePost(postId: Long, ownerId: Long): Single<Int> {
        return vkService
            .deletePost(postId, ownerId)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg) else it.response
            }
            .doOnSuccess { appDatabase.postDao().deletePostById(postId) }
    }
}