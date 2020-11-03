package com.adeldolgov.feeder.domain.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adeldolgov.feeder.data.mapper.toPostItem
import com.adeldolgov.feeder.data.pojo.Post
import com.adeldolgov.feeder.data.pojo.Source
import com.adeldolgov.feeder.data.repository.NewsRepository
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlin.math.absoluteValue

class PostsViewModel: ViewModel()  {

    companion object {
        private const val ITEMS_COUNT = 50
        private const val START_FROM = 0
    }

    val postList = MutableLiveData<Resource<List<PostItem>>>()
    private val postRepo = NewsRepository()
    private val compositeDisposable = CompositeDisposable()

    init {
        getPosts(false)
    }

    fun getPosts(ignoreTimeout: Boolean) {
        compositeDisposable.add(
            postRepo.getNewsFeedPosts(ignoreTimeout, ITEMS_COUNT, START_FROM)
                .doOnSubscribe { postList.postValue(Resource.Loading(data = null)) }
                .map {
                    mapPostsToPostItem(it.items, it.groups.toMutableList())
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        postList.value = if (it.isNotEmpty()) Resource.Success(data = it) else Resource.Empty()
                    },
                    onError = {
                        postError(it)
                    }
                )
        )
    }

    fun changePostLike(postItem: PostItem) {
        if (!postItem.hasUserLike) addLikeAtPost(postItem) else deleteLikeAtPost(postItem)
    }

    fun ignorePost(postItem: PostItem) {
        compositeDisposable.addAll(postRepo.ignorePost(postItem.id, postItem.sourceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { removePost(postItem) },
                onError = { postError(it) }
            )
        )
    }

    private fun postError(it: Throwable) {
        postList.value = Resource.Failure(data = null, message = it.message?:"Got error at API request, but error is null")
    }

    private fun mapPostsToPostItem(posts: List<Post>, groups: List<Source>): List<PostItem> {
        return posts.mapNotNull { post ->
            groups.find { source -> (source.id == post.sourceId.absoluteValue) }?.let {
                post.toPostItem(it)
            }.takeIf { post.text.isNotEmpty() || post.attachments?.first()?.photo?.sizes?.size != null }
        }
    }

    private fun replaceOldItem(newItem: PostItem, oldItem: PostItem) {
        postList.value?.data?.let { oldList ->
            postList.value = Resource.Success(data = oldList.toMutableList().apply {
                val oldPostItemIndex = oldList.indexOf(oldItem)
                if (oldPostItemIndex != -1) {
                    set(oldPostItemIndex, newItem)
                }
            })
        }
    }

    private fun deleteLikeAtPost(postItem: PostItem) {
        compositeDisposable.addAll(
            postRepo.deleteLikeAtPost(postItem.id, postItem.sourceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        replaceOldItem(postItem.copy(hasUserLike = false, likes = it.likes), postItem)
                    },
                    onError = {
                        postError(it)
                    }
                )
        )
    }

    private fun addLikeAtPost(postItem: PostItem) {
        compositeDisposable.addAll(
            postRepo.addLikeAtPost(postItem.id, postItem.sourceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        replaceOldItem(postItem.copy(hasUserLike = true, likes = it.likes), postItem)
                    },
                    onError = {
                        postError(it)
                    }
                )
        )
    }

    private fun removePost(postItem: PostItem) {
        postList.value?.data?.let { oldList ->
            postList.value = Resource.Success(data = oldList.toMutableList().apply {
                remove(postItem)
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}