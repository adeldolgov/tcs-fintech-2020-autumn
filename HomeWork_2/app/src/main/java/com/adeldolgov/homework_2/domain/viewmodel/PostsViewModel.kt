package com.adeldolgov.homework_2.domain.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adeldolgov.homework_2.data.item.PostItem
import com.adeldolgov.homework_2.data.pojo.toPostItem
import com.adeldolgov.homework_2.data.repository.GroupRepository
import com.adeldolgov.homework_2.data.repository.PostRepository

class PostsViewModel: ViewModel()  {
    val postList = MutableLiveData<List<PostItem>>()
    val postRepo = PostRepository()
    val groupRepo = GroupRepository()

    init {
        getPosts()
    }

    fun getPosts() {
        postList.postValue(
            postRepo.getPosts().mapNotNull { post ->
                groupRepo.getGroupBySourceId(post.sourceId)?.let {
                    post.toPostItem(it)
                }
            }
        )
    }

    fun changePostLike(oldItem: PostItem) {
        val newItem = oldItem.copy(isFavorite = !oldItem.isFavorite, likes = if (oldItem.isFavorite) oldItem.likes - 1 else oldItem.likes + 1)
        postList.value = postList.value?.toMutableList().apply {
            if (postList.value?.indexOf(oldItem) != -1) {
                postList.value?.indexOf(oldItem)?.let { this?.set(it, newItem) }
            }
        }
    }

    fun removePostAt(postItem: PostItem) {
        postList.value = postList.value?.toMutableList()?.apply {
            remove(postItem)
        }
    }

}