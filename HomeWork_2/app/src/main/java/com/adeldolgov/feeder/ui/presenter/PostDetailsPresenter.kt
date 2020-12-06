package com.adeldolgov.feeder.ui.presenter

import com.adeldolgov.feeder.data.repository.PostDetailRepository
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.view.PostDetailView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class PostDetailPresenter @Inject constructor(
    private val postDetailRepository: PostDetailRepository
) : RxPresenter<PostDetailView>() {

    companion object {
        private const val ITEMS_COUNT = 10
    }

    var recyclerViewCommentsPageLoading = false
    var recyclerViewIsLastPage = false

    fun getComments(postItem: PostItem) {
        recyclerViewCommentsPageLoading = true
        postDetailRepository.getCommentsAtStart(postItem.id, postItem.sourceId, ITEMS_COUNT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState?.showCommentsLoading(true) }
            .subscribeBy(
                onSuccess = {
                    if (it.isNotEmpty()) {
                        viewState?.showPostComments(it)
                    } else {
                        recyclerViewIsLastPage = true
                        viewState?.showCommentsEmpty()
                    }
                    recyclerViewCommentsPageLoading = false
                },
                onError = { viewState?.showCommentsError(it) }
            )
            .disposeOnFinish()
    }

    fun fetchComments(postItem: PostItem, startFrom: Int) {
        recyclerViewCommentsPageLoading = true
        postDetailRepository.fetchComments(postItem.id, postItem.sourceId, ITEMS_COUNT, startFrom)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState?.showCommentsLoading(false)  }
            .subscribeBy(
                onSuccess = {
                    if (it.size <= startFrom) recyclerViewIsLastPage = true
                    recyclerViewCommentsPageLoading = false
                    viewState?.showPostComments(it)
                },
                onError = { viewState?.showCommentsError(it) }
            )
            .disposeOnFinish()
    }

    fun changePostLike(postItem: PostItem) {
        if (!postItem.hasUserLike) addLikeAtPost(postItem) else deleteLikeAtPost(postItem)
    }

    fun createComment(postItem: PostItem, message: String) {
        postDetailRepository.createComment(postItem.id, postItem.sourceId, message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState?.showCommentSending()  }
            .subscribeBy(
                onSuccess = { viewState?.showPostComments(it) },
                onError = { viewState?.showCommentSendingError(it) }
            )
            .disposeOnFinish()
    }

    private fun deleteLikeAtPost(postItem: PostItem) {
            postDetailRepository.deleteLikeAtDetailedPost(postItem.id, postItem.sourceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = { viewState?.showPost(it) },
                    onError = { viewState?.showPostError(it) }
                )
                .disposeOnFinish()

    }

    private fun addLikeAtPost(postItem: PostItem) {
        postDetailRepository.addLikeAtDetailedPost(postItem.id, postItem.sourceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = { viewState?.showPost(it) },
                    onError = { viewState?.showPostError(it) }
                )
                .disposeOnFinish()
    }
}