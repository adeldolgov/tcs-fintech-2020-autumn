package com.adeldolgov.feeder.ui.presenter

import com.adeldolgov.feeder.data.repository.ProfileRepository
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.view.ProfileView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import java.io.File
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor(
    private val profileRepository: ProfileRepository
) : RxPresenter<ProfileView>() {

    companion object {
        private const val ITEMS_COUNT = 5
    }

    private var postFlow: Flowable<List<PostItem>>? = null
    var recyclerViewPostsPageLoading = false
    var recyclerViewIsLastPage = false

    override fun attachView(view: ProfileView?) {
        super.attachView(view)
        if (postFlow != null) subscribeOnPostChanges()
        else getProfile()
    }

    private fun initPostFlow(): Flowable<List<PostItem>> {
        return profileRepository.getPostsFlow()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate { postFlow = null }
    }

    private fun subscribeOnPostChanges() {
        postFlow ?: run { postFlow = initPostFlow() }
        postFlow?.subscribeBy(
            onNext = {
                if (it.isNotEmpty()) viewState?.showProfilePosts(it)
                else viewState?.showPostsEmpty()
            },
            onError = { viewState?.showPostsError(it) }
        )?.disposeOnFinish()
    }

    fun getProfile() {
        recyclerViewPostsPageLoading = true
        profileRepository.getProfile()
            .zipWith(profileRepository.loadDataAtStart(ITEMS_COUNT))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState?.showProfileLoading() }
            .doAfterSuccess {
                recyclerViewPostsPageLoading = false; recyclerViewIsLastPage = false
            }
            .subscribeBy(
                onSuccess = {
                    viewState?.showProfile(it.first)
                    postFlow ?: subscribeOnPostChanges()
                },
                onError = {
                    viewState?.showProfileError(it)
                }
            )
            .disposeOnFinish()
    }

    fun fetchNewPosts(offset: Int) {
        recyclerViewPostsPageLoading = true
        profileRepository.fetchPosts(ITEMS_COUNT, offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState?.showPostsLoading(false) }
            .doAfterSuccess {
                recyclerViewPostsPageLoading = false
                recyclerViewIsLastPage = it.size <= offset
                if (it.isEmpty()) viewState?.showPostsEmpty()
            }
            .subscribeBy(onError = { viewState?.showPostsError(it) })
            .disposeOnFinish()
    }

    fun createPost(message: String, photo: File?) {
        profileRepository.createPost(message, photo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { viewState?.showPostSending()  }
            .subscribeBy(
                onSuccess = { viewState?.hidePostSending() },
                onError = { viewState?.showPostSendingError(it) }
            )
            .disposeOnFinish()
    }

    fun changePostLike(postItem: PostItem) {
        if (!postItem.hasUserLike) addLikeAtPost(postItem) else deleteLikeAtPost(postItem)
    }

    private fun deleteLikeAtPost(postItem: PostItem) {
        profileRepository.deleteLikeAtPost(postItem.id, postItem.sourceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { viewState?.showPostSendingError(it) }
            )
            .disposeOnFinish()

    }

    private fun addLikeAtPost(postItem: PostItem) {
        profileRepository.addLikeAtPost(postItem.id, postItem.sourceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { viewState?.showPostSendingError(it) }
            )
            .disposeOnFinish()
    }

    fun deletePost(postItem: PostItem) {
        profileRepository.deletePost(postItem.id, postItem.sourceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { viewState?.showPostSendingError(it) }
            )
            .disposeOnFinish()

    }

}