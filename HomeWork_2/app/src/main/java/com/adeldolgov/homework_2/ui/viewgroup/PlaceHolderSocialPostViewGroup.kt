package com.adeldolgov.homework_2.ui.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.children
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.adeldolgov.homework_2.R
import kotlinx.android.synthetic.main.view_social_post.view.*
import kotlin.math.max

class PlaceHolderSocialPostViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {
    private val metrics = DisplayMetrics()

    init {
        setWillNotDraw(true)
        LayoutInflater.from(context).inflate(R.layout.view_social_post_placeholder, this, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var height = 0

        measureChildWithMargins(postOwnerImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += postOwnerImage.measuredHeight
        height += postOwnerImage.marginTop

        measureChildWithMargins(postOwnerText, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(postTimeText, widthMeasureSpec, 0, heightMeasureSpec, height)

        measureChildWithMargins(postContentText, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += postContentText.measuredHeight
        height += postContentText.marginTop

        measureChildWithMargins(postContentImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += postContentImage.measuredHeight
        height += postContentImage.marginTop

        val desiredWidth = if (widthMeasureSpec == MeasureSpec.UNSPECIFIED) {
            display.getMetrics(metrics)
            when (layoutParams.width) {
                MATCH_PARENT -> {
                    postContentText.maxWidth = metrics.widthPixels
                    postContentImage.maxWidth = metrics.widthPixels
                    metrics.widthPixels
                }
                WRAP_CONTENT -> {
                    var maxRowWidth = 0
                    children.forEach { child ->
                        maxRowWidth = max(maxRowWidth, child.measuredWidth)
                    }
                    maxRowWidth
                }
                else -> layoutParams.width
            }
        } else {
            MeasureSpec.getSize(widthMeasureSpec)
        }

        setMeasuredDimension(desiredWidth, View.resolveSize(height, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val currentLeft = paddingLeft
        var currentTop = paddingTop

        postOwnerImage.layout(
            currentLeft + postOwnerImage.marginStart,
            currentTop + postOwnerImage.marginTop,
            currentLeft + postOwnerImage.measuredWidth + postOwnerImage.marginStart,
            currentTop + postOwnerImage.marginTop + postOwnerImage.measuredHeight)

        postOwnerText.layout(
            currentLeft + postOwnerImage.right + postOwnerText.marginStart,
            currentTop + postOwnerText.marginTop,
            currentLeft + postOwnerImage.right + postOwnerText.measuredWidth + postOwnerText.marginStart,
            currentTop + postOwnerText.marginTop + postOwnerText.measuredHeight)

        postTimeText.layout(
            currentLeft + postOwnerImage.right + postTimeText.marginStart,
            currentTop + postOwnerText.bottom + postTimeText.marginTop,
            currentLeft + postOwnerImage.right + postTimeText.measuredWidth + postTimeText.marginStart,
            currentTop + postOwnerText.bottom + postTimeText.marginTop + postTimeText.measuredHeight)

        currentTop = postOwnerImage.bottom

        postContentText.layout(
            currentLeft + postContentText.marginStart,
            currentTop + postContentText.marginTop,
            currentLeft + postContentText.measuredWidth + postContentText.marginStart,
            currentTop + postContentText.marginTop + postContentText.measuredHeight
        )

        currentTop = postContentText.bottom

        postContentImage.layout(
            measuredWidth / 2 - postContentImage.measuredWidth / 2,
            currentTop + postContentImage.marginTop,
            measuredWidth / 2 + postContentImage.measuredWidth / 2,
            currentTop + postContentImage.marginTop + postContentImage.measuredHeight
        )
    }


    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)

}