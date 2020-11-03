package com.adeldolgov.feeder.ui.viewgroup

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.util.extension.toRelativeDateString
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import kotlinx.android.synthetic.main.view_social_post.view.*
import kotlin.math.max

class SocialDetailPostViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {
    private val metrics = DisplayMetrics()

    init {
        setWillNotDraw(true)
        LayoutInflater.from(context).inflate(R.layout.view_social_post_details, this, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var height = 0

        measureChildWithMargins(postOwnerImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += postOwnerImage.measuredHeight
        height += postOwnerImage.marginTop

        measureChildWithMargins(postOwnerText, widthMeasureSpec,
            postOwnerImage.measuredWidth + postOwnerImage.marginStart,
            heightMeasureSpec, height)
        measureChildWithMargins(postTimeText, widthMeasureSpec, 0, heightMeasureSpec, height)

        measureChildWithMargins(postContentText, widthMeasureSpec, 0, heightMeasureSpec, height)
        if (postContentText.text.isNotEmpty()) {
            height += postContentText.measuredHeight
            height += postContentText.marginTop
        }

        measureChildWithMargins(postContentImage, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += postContentImage.measuredHeight
        if (postContentImage.measuredHeight != 0) height += postContentImage.marginTop

        measureChildWithMargins(postLikeBtn, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += postLikeBtn.measuredHeight
        height += postLikeBtn.marginTop
        height += postLikeBtn.marginBottom

        measureChildWithMargins(postLikeCountText, widthMeasureSpec, 0, heightMeasureSpec, height)
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

        postContentImage.layout(
            measuredWidth / 2 - postContentImage.measuredWidth / 2,
            currentTop + postContentImage.marginTop,
            measuredWidth / 2 + postContentImage.measuredWidth / 2,
            currentTop + postContentImage.marginTop + postContentImage.measuredHeight
        )

        if(postContentImage.measuredHeight != 0) currentTop = postContentImage.bottom

        postContentText.layout(
            currentLeft + postContentText.marginStart,
            currentTop + postContentText.marginTop,
            currentLeft + postContentText.measuredWidth + postContentText.marginStart,
            currentTop + postContentText.marginTop + postContentText.measuredHeight
        )

        if (postContentText.text.isNotEmpty()) currentTop = postContentText.bottom

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

        postShareBtn.layout(
            postLikeCountText.right + postShareBtn.marginStart,
            currentTop + postShareBtn.marginTop,
            postLikeCountText.right + postShareBtn.measuredWidth + postShareBtn.marginStart,
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

    fun setPostDetails(
        postItem: PostItem, imageLoader: ImageLoader,
        onImageClickListener: (String) -> Unit,
        onShareClickListener: (Bitmap?) -> Unit
    ) {
        postItem.attachments?.first()?.photo?.sizes?.last()?.url?.let { url ->
            imageLoader.loadPoster(url, postContentImage)
            postContentImage.setOnClickListener {
                onImageClickListener(url)
            }
        }

        postShareBtn.setOnClickListener {
            val bitmap = (postContentImage.drawable as BitmapDrawable?)?.bitmap
            onShareClickListener(bitmap)
        }

        imageLoader.loadRoundedAvatar(postItem.sourceImage, postOwnerImage)

        postContentText.text = postItem.text
        postOwnerText.text = postItem.sourceName

        if (postItem.hasUserLike) {
            postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite))
        } else {
            postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_outline))
        }
        postTimeText.text = postItem.date.toRelativeDateString()
        postLikeCountText.text = postItem.likes.toString()
        postShareCountText.text = postItem.reposts.toString()
    }

}