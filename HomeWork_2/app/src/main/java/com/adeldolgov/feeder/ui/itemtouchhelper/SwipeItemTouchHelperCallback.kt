package com.adeldolgov.feeder.ui.itemtouchhelper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.util.extension.dp
import kotlin.math.abs
import kotlin.math.min


class SwipeItemTouchHelperCallback(private val adapter: SwipeItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    companion object {
        private const val swipeRightIcon = R.drawable.ic_favorite
        private const val swipeLeftIcon = R.drawable.ic_delete
        private const val iconHeightPercent = 0.7f
        private const val itemWidthPercent = 0.8f

    }
    private val iconMargin = 16.dp
    private val swipeRightBackground = ColorDrawable(Color.parseColor("#ffebee"))
    private val swipeLeftBackground = ColorDrawable(Color.parseColor("#ffcc0000"))


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = 0
        val swipeFlags = START or END

        return if (viewHolder.bindingAdapter is SwipeItemTouchHelperAdapter) makeMovementFlags(dragFlags, swipeFlags)
        else 0
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            START -> adapter.onItemSwipeRight(viewHolder.bindingAdapterPosition)
            END -> adapter.onItemSwipeLeft(viewHolder.bindingAdapterPosition)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView

        val background = if (dX > 0) swipeRightBackground else swipeLeftBackground
        val iconId = if (dX > 0) swipeRightIcon else swipeLeftIcon

        background.setBounds(
            if (dX > 0) itemView.left else itemView.right + dX.toInt(), itemView.top,
            if (dX > 0) itemView.left + dX.toInt() else itemView.right, itemView.bottom
        )
        background.alpha = min((((abs(dX)) / (itemView.width * itemWidthPercent)) * 255).toInt(), 255)
        background.draw(c)

        ContextCompat.getDrawable(recyclerView.context, iconId)?.let { icon ->
            val iconSize = icon.intrinsicHeight + ((abs(dX) / itemView.width) / 2) * itemView.height * iconHeightPercent
            val iconWidth = icon.intrinsicWidth + ((abs(dX) / itemView.width) / 2) * itemView.height * iconHeightPercent
            val halfIcon: Int = iconSize.toInt() / 2
            val top = itemView.top + ((itemView.bottom - itemView.top) / 2 - halfIcon)
            val left = if (dX > 0) itemView.left + iconMargin else itemView.right - iconMargin - halfIcon * 2
            val right = if (dX > 0) left + iconWidth.toInt() else itemView.right - iconMargin
            icon.setBounds(left, top, right, top + iconSize.toInt())
            icon.draw(c)
        }
    }

    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = true

    interface SwipeItemTouchHelperAdapter {
        fun onItemSwipeRight(position: Int)
        fun onItemSwipeLeft(position: Int)
    }

}