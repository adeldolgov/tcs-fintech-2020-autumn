package com.adeldolgov.homework_2.domain.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adeldolgov.homework_2.data.item.PostItem
import com.adeldolgov.homework_2.data.pojo.Post
import com.adeldolgov.homework_2.data.pojo.Source
import com.adeldolgov.homework_2.data.pojo.toPostItem
import com.adeldolgov.homework_2.data.repository.GroupRepository
import com.adeldolgov.homework_2.data.repository.PostRepository
import com.adeldolgov.homework_2.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers

class PostsViewModel: ViewModel()  {
    val postList = MutableLiveData<Resource<List<PostItem>>>()
    private val postRepo = PostRepository()
    private val groupRepo = GroupRepository()
    private val compositeDisposable = CompositeDisposable()

    init {
        getPosts()
    }

    fun getPosts() {
        compositeDisposable.add(
            postRepo.getPosts()
                .doOnSubscribe {
                    postList.postValue(Resource.loading(data = null))
                }
                .zipWith(groupRepo.getGroups()) { posts: List<Post>, groups: List<Source> ->
                    posts.mapNotNull { post ->
                        groups.find { source -> source.id == post.sourceId }?.let {
                            post.toPostItem(it)
                        }
                    }
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        postList.value =
                            if (it.isNotEmpty()) Resource.success(data = it) else Resource.empty()
                    },
                    onError = {
                        postList.value = Resource.error(data = null, message = it.message.toString())
                    }
                )
        )
    }

    fun changePostLike(oldItem: PostItem) {
        val newItem = oldItem.copy(
            isFavorite = !oldItem.isFavorite,
            likes = if (oldItem.isFavorite) oldItem.likes - 1 else oldItem.likes + 1
        )
        postList.value?.data?.let { list ->
            postList.value = Resource.success(data = list.toMutableList().apply {
                if (list.indexOf(oldItem) != -1) {
                    list.indexOf(oldItem).let { this.set(it, newItem) }
                }
            })
        }
    }

    fun removePostAt(postItem: PostItem) {
        postList.value?.data?.let {
            postList.value = Resource.success(data = it.toMutableList().apply {
                remove(postItem)
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }


}