package com.adeldolgov.feeder.ui.presenter

import com.adeldolgov.feeder.data.repository.NewsFeedRepository
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.view.NewsFeedView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class NewsFeedPresenter @Inject constructor(
    private val newsFeedRepository: NewsFeedRepository
) : RxPresenter<NewsFeedView>() {

    companion object {
        private const val ITEMS_COUNT = 25
    }

    var recyclerViewPageLoading = false
    private var postFlow: Flowable<List<PostItem>>? = null

    override fun attachView(view: NewsFeedView?) {
        super.attachView(view)
        if (postFlow != null) subscribeOnPostChanges()
        else getPosts(false)
    }

    private fun initPostFlow(): Flowable<List<PostItem>> {
        return newsFeedRepository.getPostsFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate { postFlow = null }
    }

    private fun subscribeOnPostChanges() {
        postFlow ?: run { postFlow = initPostFlow() }
        postFlow?.subscribeBy(
            onNext = {
                if (it.isNotEmpty()) viewState?.showNewsFeed(it)
                else viewState.showEmptyState()
            },
            onError = { viewState?.showError(it) }
        )?.disposeOnFinish()
    }

    fun getPosts(ignoreTimeout: Boolean) {
        recyclerViewPageLoading = true
        newsFeedRepository.updatePostsIfNeeds(ignoreTimeout, ITEMS_COUNT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState?.showLoading(true) }
            .doAfterSuccess { recyclerViewPageLoading = false }
            .subscribeBy(
                onSuccess = {
                    postFlow?.let { viewState?.hideLoading() } ?: subscribeOnPostChanges()
                },
                onError = { viewState?.showError(it) }
            )
            .disposeOnFinish()
    }

    fun fetchNewPosts(startFrom: Int) {
        recyclerViewPageLoading = true
        newsFeedRepository.fetchPosts(ITEMS_COUNT, startFrom)
            .subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState?.showLoading(false) }
            .doAfterSuccess { recyclerViewPageLoading = false }
            .subscribeBy(
                onSuccess = { viewState?.hideLoading() },
                onError = { viewState?.showPostsFetchingError(it) }
            )
            .disposeOnFinish()
    }

    fun changePostLike(postItem: PostItem) {
        if (!postItem.hasUserLike) addLikeAtPost(postItem) else deleteLikeAtPost(postItem)
    }

    fun ignorePost(postItem: PostItem) {
        newsFeedRepository.ignorePost(postItem.id, postItem.sourceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = { viewState?.showError(it) })
            .disposeOnFinish()
    }

    private fun deleteLikeAtPost(postItem: PostItem) {
        newsFeedRepository.deleteLikeAtPost(postItem.id, postItem.sourceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = { viewState?.showError(it) })
            .disposeOnFinish()

    }

    private fun addLikeAtPost(postItem: PostItem) {
        newsFeedRepository.addLikeAtPost(postItem.id, postItem.sourceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = { viewState?.showError(it) })
            .disposeOnFinish()
    }
}