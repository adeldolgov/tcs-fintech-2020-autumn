package com.adeldolgov.feeder.ui.itemtouchhelper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.RecyclerView

class SwipeItemTouchHelperCallback(private val adapter: SwipeItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = 0
        val swipeFlags = START or END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            START -> adapter.onItemSwipeRight(viewHolder.adapterPosition)
            END -> adapter.onItemSwipeLeft(viewHolder.adapterPosition)
        }
    }

    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = true

    interface SwipeItemTouchHelperAdapter {
        fun onItemSwipeRight(position: Int)
        fun onItemSwipeLeft(position: Int)
    }

}