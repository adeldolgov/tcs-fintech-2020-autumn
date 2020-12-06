package com.adeldolgov.feeder.ui.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.*
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.item.CommentItem
import com.adeldolgov.feeder.util.extension.toRelativeDateString
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import kotlinx.android.synthetic.main.view_social_post_comment.view.*
import kotlin.math.max

class SocialPostCommentViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {
    private val metrics = DisplayMetrics()

    init {
        setWillNotDraw(true)
        LayoutInflater.from(context).inflate(R.layout.view_social_post_comment, this, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var height = 0

        measureChildWithMargins(commentOwnerImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += commentOwnerImage.measuredHeight
        height += commentOwnerImage.marginTop

        measureChildWithMargins(
            commentOwnerText, widthMeasureSpec,
            commentOwnerImage.measuredWidth + commentOwnerImage.marginStart,
            heightMeasureSpec, height
        )
        measureChildWithMargins(
            commentContentText, widthMeasureSpec,
            commentOwnerImage.measuredWidth + commentOwnerImage.marginStart
            , heightMeasureSpec, height
        )
        if (commentContentText.text.isNotEmpty()) {
            height += commentContentText.measuredHeight
            height += commentContentText.marginTop
        }

        measureChildWithMargins(commentContentImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += commentContentImage.measuredHeight
        if (commentContentImage.measuredHeight != 0) height += commentContentImage.marginTop

        measureChildWithMargins(commentTimeText, widthMeasureSpec, 0, heightMeasureSpec, height)

        measureChildWithMargins(commentLikeBtn, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += commentLikeBtn.measuredHeight
        height += commentLikeBtn.marginBottom

        measureChildWithMargins(commentLikeCountText, widthMeasureSpec, 0, heightMeasureSpec, height)

        val desiredWidth = if (widthMeasureSpec == MeasureSpec.UNSPECIFIED) {
            display.getMetrics(metrics)
            when (layoutParams.width) {
                MATCH_PARENT -> {
                    commentContentText.maxWidth = metrics.widthPixels
                    commentContentImage.maxWidth = metrics.widthPixels
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

        commentOwnerImage.layout(
            currentLeft + commentOwnerImage.marginStart,
            currentTop + commentOwnerImage.marginTop,
            currentLeft + commentOwnerImage.measuredWidth + commentOwnerImage.marginStart,
            currentTop + commentOwnerImage.marginTop + commentOwnerImage.measuredHeight
        )

        commentOwnerText.layout(
            currentLeft + commentOwnerImage.right + commentOwnerText.marginStart,
            currentTop + commentOwnerText.marginTop,
            currentLeft + commentOwnerImage.right + commentOwnerText.measuredWidth + commentOwnerText.marginStart,
            currentTop + commentOwnerText.marginTop + commentOwnerText.measuredHeight
        )

        currentTop = commentOwnerText.bottom

        commentContentText.layout(
            currentLeft + commentOwnerImage.right + commentContentText.marginStart,
            currentTop + commentContentText.marginTop,
            currentLeft + commentOwnerImage.right + commentContentText.measuredWidth + commentContentText.marginStart,
            currentTop + commentContentText.marginTop + commentContentText.measuredHeight
        )

        if(commentContentText.text.isNotEmpty()) currentTop = commentContentText.bottom

        commentContentImage.layout(
            currentLeft + commentOwnerImage.right + commentContentImage.marginStart,
            currentTop + commentContentImage.marginTop,
            currentLeft + commentOwnerImage.right + commentContentImage.marginStart + commentContentImage.measuredWidth,
            currentTop + commentContentImage.marginTop + commentContentImage.measuredHeight
        )

        if (commentContentImage.measuredHeight != 0) currentTop = commentContentImage.bottom

        commentTimeText.layout(
            currentLeft + commentTimeText.marginStart,
            currentTop + commentTimeText.marginTop,
            currentLeft + commentTimeText.measuredWidth + commentTimeText.marginStart,
            currentTop + commentTimeText.marginTop + commentTimeText.measuredHeight
        )

        commentLikeCountText.layout(
            measuredWidth - currentLeft - commentLikeCountText.marginEnd - commentLikeCountText.measuredWidth,
            currentTop + commentLikeCountText.marginTop,
            measuredWidth - currentLeft - commentLikeCountText.marginEnd,
            currentTop + commentLikeCountText.marginTop + commentLikeCountText.measuredHeight
        )

        commentLikeBtn.layout(
            commentLikeCountText.left - commentLikeBtn.marginEnd - commentLikeBtn.measuredWidth,
            currentTop + commentLikeBtn.marginTop,
            commentLikeCountText.left - commentLikeBtn.marginEnd,
            currentTop + commentLikeBtn.marginTop + commentLikeBtn.measuredHeight
        )

    }

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams() = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    fun updateCommentDetails(imageLoader: ImageLoader, commentItem: CommentItem) {
        imageLoader.loadRoundedAvatar(commentItem.sourceImage, commentOwnerImage)
        commentItem.attachments?.first()?.photo?.sizes?.last()?.url?.let { url ->
            imageLoader.loadPoster(url, commentContentImage)
            commentContentImage.isClickable = true
        }
        commentContentText.text = commentItem.text
        commentOwnerText.text = commentItem.sourceName
        commentTimeText.text = commentItem.date.toRelativeDateString()
        commentLikeCountText.text = commentItem.likes.toString()
    }

}