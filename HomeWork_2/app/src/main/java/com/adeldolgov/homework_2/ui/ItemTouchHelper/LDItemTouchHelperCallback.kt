package com.adeldolgov.homework_2.ui.ItemTouchHelper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.RecyclerView

class LDItemTouchHelperCallback(private val adapter: LDItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = 0
        val swipeFlags = START or END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            START -> adapter.onItemHide(viewHolder.adapterPosition)
            END -> adapter.onItemLike(viewHolder.adapterPosition)
        }
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    interface LDItemTouchHelperAdapter {
        fun onItemHide(position: Int)
        fun onItemLike(position: Int)
    }

}