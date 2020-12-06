package com.adeldolgov.feeder.ui.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.feeder.util.extension.dp

class PaddingItemDecoration(private val paddingItemInterface: PaddingItemInterface) :
    RecyclerView.ItemDecoration() {

    companion object {
        private const val PADDING_DEFAULT = 12
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val viewHolder = parent.getChildViewHolder(view)
        val relativePos = getRelativePosition(viewHolder)
        if (relativePos == -1) return

        if (paddingItemInterface.shouldMakePadding(relativePos)) {
            outRect.top = PADDING_DEFAULT.dp
        } else {
            outRect.top = 0
        }

        if (relativePos == (parent.adapter?.itemCount ?: 1) - 1) outRect.bottom = PADDING_DEFAULT.dp
    }

    private fun getRelativePosition(viewHolder: RecyclerView.ViewHolder): Int {
        return if (viewHolder.bindingAdapter is PaddingItemInterface) {
            viewHolder.bindingAdapterPosition
        } else {
            -1
        }
    }

    interface PaddingItemInterface {
        fun shouldMakePadding(position: Int): Boolean
    }
}