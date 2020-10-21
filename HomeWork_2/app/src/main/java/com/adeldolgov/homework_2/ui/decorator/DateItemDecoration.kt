package com.adeldolgov.homework_2.ui.decorator

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.homework_2.R
import com.adeldolgov.homework_2.util.dp
import com.adeldolgov.homework_2.util.sp

class DateItemDecoration(private val dateItemInterface: DateItemInterface) : RecyclerView.ItemDecoration() {

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var roundRectanglePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textBounds: Rect = Rect()

    companion object {
        private const val PADDING_TOP_DATE = 40
        private const val PADDING_DEFAULT = 4
        private const val DATE_TEXT_SIZE = 16
        private const val ROUND_RECTANGLE_RADIUS = 20f
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        parent.children.forEach { child ->
            val currentPos = parent.getChildAdapterPosition(child)
            if (currentPos == -1) return

            if (dateItemInterface.shouldWriteDate(currentPos)) {
                val currentRelativeDateStr = dateItemInterface.getDateAtPosition(currentPos)

                textPaint.color = Color.WHITE
                textPaint.textSize = (DATE_TEXT_SIZE.sp.toFloat())
                textPaint.getTextBounds(currentRelativeDateStr, 0, currentRelativeDateStr.length, textBounds)

                roundRectanglePaint.color = ContextCompat.getColor(parent.context, R.color.colorAccent)

                canvas.drawRoundRect(
                    (parent.measuredWidth / 2 - textBounds.width() / 2 - 12.dp).toFloat(),
                    child.top.toFloat() - 32.dp,
                    (parent.measuredWidth / 2 + textBounds.width() / 2 + 12.dp).toFloat(),
                    child.top.toFloat() - 8.dp,
                    ROUND_RECTANGLE_RADIUS,
                    ROUND_RECTANGLE_RADIUS,
                    roundRectanglePaint)

                canvas.drawText(
                    currentRelativeDateStr,
                    (parent.measuredWidth / 2 - textBounds.width() / 2).toFloat(),
                    child.top.toFloat() - 14.dp,
                    textPaint)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val currentPos = parent.getChildAdapterPosition(view)
        if (currentPos == -1) return

        if (dateItemInterface.shouldWriteDate(currentPos)) {
            outRect.top = PADDING_TOP_DATE.dp
        } else {
            outRect.top = PADDING_DEFAULT.dp
        }
        if (currentPos == (parent.adapter?.itemCount ?: 1) - 1) outRect.bottom = PADDING_DEFAULT.dp
    }

    interface DateItemInterface {
        fun shouldWriteDate(position: Int): Boolean
        fun getDateAtPosition(position: Int): String
    }
}