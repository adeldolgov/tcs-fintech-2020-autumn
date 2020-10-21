package com.adeldolgov.homework_2.ui.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.adeldolgov.homework_2.data.item.PostItem

class PostDiffUtilsCallback : DiffUtil.ItemCallback<PostItem>() {
    override fun areItemsTheSame(oldItem: PostItem, newItem: PostItem):Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PostItem, newItem: PostItem): Boolean = oldItem == newItem

    /*override fun getChangePayload(oldItem: PostItem, newItem: PostItem): Any? {
        return oldItem.isFavorite != newItem.isFavorite
    }*/
}