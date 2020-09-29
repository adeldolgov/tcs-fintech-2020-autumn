package com.adeldolgov.homework_2.ui.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.max

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {

    init {
        setWillNotDraw(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0
        var currentRowHeight = 0
        var currentRowWidth = 0

        children.forEach { child ->
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, height)
            currentRowWidth += child.measuredWidth

            if (currentRowWidth > desiredWidth) {
                currentRowWidth = child.measuredWidth
                currentRowHeight = child.measuredHeight
                height += child.measuredHeight
            } else {
                val tempCurrentRowHeight = max(currentRowHeight, child.measuredHeight)
                if (tempCurrentRowHeight > currentRowHeight) {
                    height += tempCurrentRowHeight - currentRowHeight
                    currentRowHeight = tempCurrentRowHeight
                }
            }

        }

        setMeasuredDimension(desiredWidth, View.resolveSize(height, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentLeft = l + paddingLeft
        var currentTop = t + paddingTop
        var maxRowHeight = 0

        children.forEach { child ->
            var currentRight = currentLeft + child.measuredWidth
            if (child.measuredHeight > maxRowHeight && currentRight <= measuredWidth) maxRowHeight = child.measuredHeight
            if (currentRight > measuredWidth) {
                currentLeft = l + paddingLeft
                currentTop += maxRowHeight
                currentRight = currentLeft + child.measuredWidth
                maxRowHeight = child.measuredHeight
            }
            child.layout(currentLeft, currentTop, currentRight, currentTop + child.measuredHeight)
            currentLeft += child.measuredWidth
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
}