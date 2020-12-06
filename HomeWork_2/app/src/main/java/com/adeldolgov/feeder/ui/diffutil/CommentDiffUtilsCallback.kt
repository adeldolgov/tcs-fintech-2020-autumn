package com.adeldolgov.feeder.ui.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.adeldolgov.feeder.ui.item.CommentItem

class CommentDiffUtilsCallback : DiffUtil.ItemCallback<CommentItem>() {
    override fun areItemsTheSame(oldItem: CommentItem, newItem: CommentItem):Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean = oldItem == newItem

    /*override fun getChangePayload(oldItem: CommentItem, newItem: CommentItem): Any? {
        return oldItem.hasUserLike != newItem.hasUserLike
    }*/
}