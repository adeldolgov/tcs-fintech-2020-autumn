package com.adeldolgov.feeder.data.repository

import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.database.entity.CommentEntity
import com.adeldolgov.feeder.data.database.entity.SourceEntity
import com.adeldolgov.feeder.data.mapper.*
import com.adeldolgov.feeder.data.pojo.Comment
import com.adeldolgov.feeder.data.pojo.Source
import com.adeldolgov.feeder.data.server.VkService
import com.adeldolgov.feeder.ui.item.CommentItem
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.util.error.NoNetworkException
import com.adeldolgov.feeder.util.networkavailability.NetworkAvailability
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import javax.inject.Inject

class PostDetailRepository @Inject constructor(
    override val vkService: VkService,
    override val appDatabase: AppDatabase,
    private val networkAvailability: NetworkAvailability
): BasePostActionRepository() {

    fun getCommentsAtStart(postId: Long, ownerId: Long, count: Int): Single<List<CommentItem>> {
        return if (networkAvailability.isNetworkAvailable())
            loadActualDataFromNetworkAndUpdateLocal(postId, ownerId, count, 0)
        else
            loadLocalDataFromDatabase(postId)
    }

    fun fetchComments(postId: Long, ownerId: Long, count: Int, startFrom: Int): Single<List<CommentItem>> {
        return Single.just(networkAvailability.isNetworkAvailable())
            .map {
                if (it) loadActualDataFromNetworkAndUpdateLocal(postId, ownerId, count, startFrom).blockingGet()
                else throw NoNetworkException("No connection with host.")
            }
    }

    private fun loadActualDataFromNetworkAndUpdateLocal(postId: Long, ownerId: Long, count: Int, startFrom: Int): Single<List<CommentItem>> {
        return vkService
            .getComments(postId, ownerId, count, startFrom)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response.copy(items = filterDeletedAndEmptyComments(it.response.items))
            }
            .doOnSuccess { updateDataInDatabase(it.groups.plus(it.profiles.map { profile ->  profile.toSource() }), it.items) }
            .map { loadLocalDataFromDatabase(postId).blockingGet() }
    }

    private fun loadLocalDataFromDatabase(postId: Long): Single<List<CommentItem>> {
        return getSourcesFromDatabase()
            .zipWith(getCommentsFromDatabase(postId)) { sources: List<SourceEntity>, comments: List<CommentEntity> ->
                mapCommentEntityToCommentItem(sources, comments)
            }
    }

    fun createComment(postId: Long, ownerId: Long, message: String): Single<List<CommentItem>> {
        return vkService
            .createComment(postId, ownerId, message)
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response.commentId
            }
            .map { vkService.getComment(it, ownerId).blockingGet() }
            .map {
                if (it.error != null) throw Exception(it.error.errorMsg)
                else it.response
            }
            .doOnSuccess { updateDataInDatabase(it.groups.plus(it.profiles.map { profile ->  profile.toSource() }), it.items) }
            .map { loadLocalDataFromDatabase(postId).blockingGet() }
    }

    private fun filterDeletedAndEmptyComments(comments: List<Comment>): List<Comment> {
        return comments.filter { !it.deleted && (it.text.isNotEmpty() || it.attachments?.first()?.photo?.sizes != null) }
    }

    private fun mapCommentEntityToCommentItem(sources: List<SourceEntity>, comments: List<CommentEntity>): List<CommentItem> {
        return comments.mapNotNull { comment ->
            sources.find { source -> (source.id == comment.fromId) }?.let {
                comment.toCommentItem(it)
            }
        }
    }

    fun addLikeAtDetailedPost(postId: Long, ownerId: Long): Single<PostItem> {
        return super.addLikeAtPost(postId, ownerId)
            .map { getPostFromDatabase(postId, ownerId).blockingGet() }
    }

    fun deleteLikeAtDetailedPost(postId: Long, ownerId: Long): Single<PostItem> {
        return super.deleteLikeAtPost(postId, ownerId)
            .map { getPostFromDatabase(postId, ownerId).blockingGet() }
    }

    private fun getPostFromDatabase(postId: Long, ownerId: Long): Single<PostItem> {
        return appDatabase.postDao()
            .getPostById(postId)
            .zipWith(appDatabase.sourceDao().getSourceById(ownerId)) { postEntity, sourceEntity ->
                postEntity.toPostItem(sourceEntity)
            }
    }

    private fun updateDataInDatabase(sources: List<Source>, comments: List<Comment>) {
        appDatabase.runInTransaction{
            insertSourcesToDatabase(sources)
            insertCommentsToDatabase(comments)
        }
    }

    private fun insertCommentsToDatabase(comments: List<Comment>) {
        appDatabase.commentDao().insertAll(*comments.map {
            it.toCommentEntity()
        }.toTypedArray())
    }

    private fun insertSourcesToDatabase(sources: List<Source>) {
        appDatabase.sourceDao().insertAll(*sources.map {
            it.toSourceEntity()
        }.toTypedArray())
    }

    private fun getCommentsFromDatabase(postId: Long): Single<List<CommentEntity>> {
        return appDatabase.commentDao()
            .getCommentsForPost(postId)
    }

    private fun getSourcesFromDatabase(): Single<List<SourceEntity>> {
        return appDatabase.sourceDao()
            .getSources()
    }

}