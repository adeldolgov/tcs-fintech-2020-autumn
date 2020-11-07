package com.adeldolgov.feeder.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.decorator.DateItemDecoration
import com.adeldolgov.feeder.ui.diffutil.PostDiffUtilsCallback
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.itemtouchhelper.SwipeItemTouchHelperCallback
import com.adeldolgov.feeder.util.extension.*
import com.adeldolgov.feeder.util.imageloader.GlideImageLoader
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import kotlinx.android.synthetic.main.view_social_post.view.*
import java.util.*

private const val POST_TEXT_ONLY = 0
private const val POST_IMAGE = 1

class PostAdapter(
    val swipeLeftListener: (PostItem) -> Unit,
    val swipeRightListener: (PostItem) -> Unit,
    val onClickListener: (Int, PostItem) -> Unit
) : RecyclerView.Adapter<PostAdapter.BaseViewHolder>(),
    SwipeItemTouchHelperCallback.SwipeItemTouchHelperAdapter, DateItemDecoration.DateItemInterface {

    private val imageLoader: ImageLoader = GlideImageLoader()
    private val diffUtil = AsyncListDiffer(this, PostDiffUtilsCallback())

    var posts: List<PostItem> = emptyList()
        set(value) {
            diffUtil.submitList(value)
            field = value
        }
        get() = diffUtil.currentList

    override fun getItemViewType(position: Int): Int {
        val photosSize = posts[position].attachments?.first()?.photo?.sizes?.size
        return photosSize?.let { POST_IMAGE }?: run { POST_TEXT_ONLY }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            POST_IMAGE -> createWithImageVH(inflater, parent)
            else -> createWithTextVH(inflater, parent)
        }
    }

    private fun createWithTextVH(inflater: LayoutInflater, parent: ViewGroup): PostViewHolder {
        return PostViewHolder(
            inflater.inflate(R.layout.item_post, parent, false),
            clickLikeListener = { swipeLeftListener(posts[it]) },
            clickListener = { onClickListener(it, posts[it]) },
            imageLoader = imageLoader
        )
    }

    private fun createWithImageVH(inflater: LayoutInflater, parent: ViewGroup): PostWithImageViewHolder {
        return PostWithImageViewHolder(
            inflater.inflate(R.layout.item_post, parent, false),
            clickLikeListener = { swipeLeftListener(posts[it]) },
            clickListener = { onClickListener(it, posts[it]) },
            imageLoader = imageLoader
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    override fun getItemId(position: Int): Long {
        return posts[position].id
    }

    override fun onItemSwipeRight(position: Int) {
        swipeRightListener(posts[position])
    }

    override fun onItemSwipeLeft(position: Int) {
        swipeLeftListener(posts[position])
        notifyItemChanged(position)
    }

    override fun shouldWriteDate(position: Int): Boolean {
        if (position == 0) return true

        val currentDate = Date(posts[position].date * DateUtils.SECOND_IN_MILLIS)
        val prevDate = Date(posts[position - 1].date * DateUtils.SECOND_IN_MILLIS)
        return !currentDate.compareToExcludeTime(prevDate)
    }

    override fun getDateAtPosition(position: Int): String {
        return posts[position].date.toRelativeDateStringDay()
    }

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(post: PostItem)
    }

    class PostWithImageViewHolder(view: View,
        clickLikeListener: (Int) -> Unit,
        clickListener: (Int) -> Unit,
        val imageLoader: ImageLoader
    ) : BaseViewHolder(view) {
        init {
            itemView.postLikeBtn.setOnClickListener { clickLikeListener(adapterPosition) }
            itemView.setOnClickListener { clickListener(adapterPosition) }
        }

        override fun bind(post: PostItem) {
            with(itemView) {
                imageLoader.loadPoster(
                    post.attachments?.first()?.photo?.sizes?.last()?.url!!,
                    postContentImage
                )
                imageLoader.loadRoundedAvatar(post.sourceImage, postOwnerImage)

                postContentText.text = post.text
                postOwnerText.text = post.sourceName

                if (post.hasUserLike) {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite))
                } else {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_outline))
                }
                postTimeText.text = post.date.toRelativeDateString()
                postLikeCountText.text = post.likes.toString()
                postShareCountText.text = post.reposts.toString()
                postCommentCountText.text = post.comments.toString()
                postShareBtn.setOnClickListener {
                    (postContentImage.drawable as BitmapDrawable?)?.bitmap?.let {
                        context.sharePhotoFile(post.text, post.sourceName, context.saveImageToCache(it))
                    }
                }
            }
        }
    }

    class PostViewHolder(
        view: View,
        clickLikeListener: (Int) -> Unit,
        clickListener: (Int) -> Unit,
        val imageLoader: ImageLoader
    ) : BaseViewHolder(view) {
        init {
            itemView.postLikeBtn.setOnClickListener { clickLikeListener(adapterPosition) }
            itemView.setOnClickListener { clickListener(adapterPosition) }
        }

        override fun bind(post: PostItem) {
            with(itemView) {
                imageLoader.loadRoundedAvatar(post.sourceImage, postOwnerImage)

                postContentText.text = post.text
                postOwnerText.text = post.sourceName

                if (post.hasUserLike) {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite))
                } else {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_outline))
                }
                postTimeText.text = post.date.toRelativeDateString()
                postLikeCountText.text = post.likes.toString()
                postShareCountText.text = post.reposts.toString()
                postCommentCountText.text = post.comments.toString()
                postShareBtn.setOnClickListener {
                    context.shareTextMessage(post.text, post.sourceName)
                }
            }
        }
    }

}