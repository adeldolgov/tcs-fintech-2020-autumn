package com.adeldolgov.feeder.ui.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.adeldolgov.feeder.data.item.PostItem

class PostDiffUtilsCallback(
    private val oldItems: List<PostItem>,
    private val newItems: List<PostItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldItems[oldPos].id == newItems[newPos].id
    }

    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldItems[oldPos] == newItems[newPos]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].hasUserLike != newItems[newItemPosition].hasUserLike
    }

}