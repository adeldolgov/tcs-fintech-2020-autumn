package com.adeldolgov.feeder.ui.presenter

import com.adeldolgov.feeder.data.mapper.toPostItem
import com.adeldolgov.feeder.data.pojo.Post
import com.adeldolgov.feeder.data.pojo.Source
import com.adeldolgov.feeder.data.repository.NewsFeedRepository
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.view.NewsFeedView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import kotlin.math.absoluteValue

@InjectViewState
class NewsFeedPresenter(
    private val newsFeedFeedRepository: NewsFeedRepository
) : RxPresenter<NewsFeedView>() {

    companion object {
        private const val ITEMS_COUNT = 25
        private const val NULL_ERROR = "Got error at API request, but error is null"
    }

    init {
        getPosts(false)
    }

    private var posts = listOf<PostItem>()
    var recyclerViewPageLoading = false

    fun getPosts(ignoreTimeout: Boolean) {
        viewState?.showLoading(true)
        newsFeedFeedRepository.getNewsFeedAtStart(ignoreTimeout, ITEMS_COUNT)
            .subscribeOn(Schedulers.io())
            .map {
                mapPostsToPostItem(it.items, it.groups)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    posts = it
                    if (it.isNotEmpty()) viewState?.showNewsFeed(posts) else viewState?.showEmptyState()
                },
                onError = { viewState?.showError(it.message ?: NULL_ERROR) }
            )
            .disposeOnFinish()
    }

    fun fetchNewPosts() {
        recyclerViewPageLoading = true
        val startFrom = posts.size.plus(1)
        newsFeedFeedRepository.fetchNewsFeed(ITEMS_COUNT, startFrom)
            .subscribeOn(Schedulers.io())
            .map { mapPostsToPostItem(it.items, it.groups.toMutableList()) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    if (it.isNotEmpty()) {
                        posts = posts.toMutableList().apply { addAll(it) }
                        viewState?.showNewsFeed(posts)
                    }
                    recyclerViewPageLoading = false
                },
                onError = { viewState?.showError(it.message ?: NULL_ERROR) }
            )
            .disposeOnFinish()
    }

    fun changePostLike(postItem: PostItem) {
        if (!postItem.hasUserLike) addLikeAtPost(postItem) else deleteLikeAtPost(postItem)
    }

    fun ignorePost(postItem: PostItem) {
        newsFeedFeedRepository.ignorePost(postItem.id, postItem.sourceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { removePost(postItem) },
                onError = { viewState?.showError(it.message ?: NULL_ERROR) }
            )
            .disposeOnFinish()
    }

    private fun mapPostsToPostItem(posts: List<Post>, groups: List<Source>): List<PostItem> {
        return posts.mapNotNull { post ->
            groups.find { source -> (source.id == post.sourceId.absoluteValue) }?.let {
                post.toPostItem(it)
            }.takeIf { post.text.isNotEmpty() || post.attachments?.first()?.photo?.sizes?.size != null }
        }
    }

    private fun replaceOldItem(newItem: PostItem, oldItem: PostItem) {
        val oldPostItemIndex = posts.indexOf(oldItem)
        if (oldPostItemIndex != -1) {
            posts = posts.toMutableList().apply {
                set(oldPostItemIndex, newItem)
            }
            viewState?.showNewsFeed(posts)
        }
    }

    private fun deleteLikeAtPost(postItem: PostItem) {
            newsFeedFeedRepository.deleteLikeAtPost(postItem.id, postItem.sourceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        replaceOldItem(postItem.copy(hasUserLike = false, likes = it.likes), postItem)
                    },
                    onError = { viewState?.showError(it.message ?: NULL_ERROR) }
                )
                .disposeOnFinish()

    }

    private fun addLikeAtPost(postItem: PostItem) {
            newsFeedFeedRepository.addLikeAtPost(postItem.id, postItem.sourceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        replaceOldItem(postItem.copy(hasUserLike = true, likes = it.likes), postItem)
                    },
                    onError = { viewState?.showError(it.message ?: NULL_ERROR) }
                )
                .disposeOnFinish()
    }

    private fun removePost(postItem: PostItem) {
        posts = posts.toMutableList().apply {
            remove(postItem)
        }
        viewState?.showNewsFeed(posts)
    }
}