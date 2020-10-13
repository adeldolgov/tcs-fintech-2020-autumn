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
import androidx.core.view.marginBottom
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.adeldolgov.homework_2.R
import kotlinx.android.synthetic.main.view_social_post.view.*
import kotlin.math.max

class SocialPostViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {
    private val metrics = DisplayMetrics()

    init {
        setWillNotDraw(true)
        LayoutInflater.from(context).inflate(R.layout.view_social_post, this, true)
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

        measureChildWithMargins(postLikeBtn, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += postLikeBtn.measuredHeight
        height += postLikeBtn.marginTop
        height += postLikeBtn.marginBottom

        measureChildWithMargins(postLikeCountText, widthMeasureSpec, 0, heightMeasureSpec, height)

        measureChildWithMargins(postCommentBtn, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(postCommentCountText, widthMeasureSpec, 0, heightMeasureSpec, height)

        measureChildWithMargins(postShareBtn, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(postShareCountText, widthMeasureSpec, 0, heightMeasureSpec, height)

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

        currentTop = postContentImage.bottom

        postLikeBtn.layout(
            currentLeft + postLikeBtn.marginStart,
            currentTop + postLikeBtn.marginTop,
            currentLeft + postLikeBtn.measuredWidth + postLikeBtn.marginStart,
            currentTop + postLikeBtn.marginTop + postLikeBtn.measuredHeight
        )
        postLikeCountText.layout(
            postLikeBtn.right + postLikeCountText.marginStart,
            postLikeBtn.top + (postLikeBtn.bottom - postLikeBtn.top) / 2 - postLikeCountText.measuredHeight / 2,
            postLikeBtn.right + postLikeCountText.measuredWidth + postLikeCountText.marginStart,
            postLikeBtn.top + (postLikeBtn.bottom - postLikeBtn.top) / 2 + postLikeCountText.measuredHeight / 2
        )

        postCommentBtn.layout(
            postLikeCountText.right + postCommentBtn.marginStart,
            currentTop + postCommentBtn.marginTop,
            postLikeCountText.right + postCommentBtn.measuredWidth + postCommentBtn.marginStart,
            currentTop + postCommentBtn.marginTop + postCommentBtn.measuredHeight
        )
        postCommentCountText.layout(
            postCommentBtn.right + postCommentCountText.marginStart,
            postCommentBtn.top + (postCommentBtn.bottom - postCommentBtn.top) / 2 - postCommentCountText.measuredHeight / 2,
            postCommentBtn.right + postCommentCountText.measuredWidth + postCommentCountText.marginStart,
            postCommentBtn.top + (postCommentBtn.bottom - postCommentBtn.top) / 2 + postCommentCountText.measuredHeight / 2
        )

        postShareBtn.layout(
            postCommentCountText.right + postShareBtn.marginStart,
            currentTop + postShareBtn.marginTop,
            postCommentCountText.right + postShareBtn.measuredWidth + postShareBtn.marginStart,
            currentTop + postShareBtn.marginTop + postShareBtn.measuredHeight
        )
        postShareCountText.layout(
            postShareBtn.right + postShareCountText.marginStart,
            postShareBtn.top + (postShareBtn.bottom - postShareBtn.top) / 2 - postShareCountText.measuredHeight / 2,
            postShareBtn.right + postShareCountText.measuredWidth + postShareCountText.marginStart,
            postShareBtn.top + (postShareBtn.bottom - postShareBtn.top) / 2 + postShareCountText.measuredHeight / 2
        )

    }


    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)

}