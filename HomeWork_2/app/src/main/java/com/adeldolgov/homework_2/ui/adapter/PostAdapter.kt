package com.adeldolgov.homework_2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.homework_2.R
import com.adeldolgov.homework_2.data.item.PostItem
import com.adeldolgov.homework_2.ui.decorator.DateItemDecoration
import com.adeldolgov.homework_2.ui.diffutil.PostDiffUtilsCallback
import com.adeldolgov.homework_2.ui.itemtouchhelper.SwipeItemTouchHelperCallback
import com.adeldolgov.homework_2.util.compareToExcludeTime
import com.adeldolgov.homework_2.util.imageloader.GlideImageLoader
import com.adeldolgov.homework_2.util.imageloader.ImageLoader
import com.adeldolgov.homework_2.util.toRelativeDateString
import com.adeldolgov.homework_2.util.toRelativeDateStringDay
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

    private val diffUtil = AsyncListDiffer(this, PostDiffUtilsCallback())
    private val imageLoader: ImageLoader = GlideImageLoader()

    var list: List<PostItem> = emptyList()
        set(value) {
            diffUtil.submitList(value)
            field = value
        }
        get() = diffUtil.currentList

    override fun getItemViewType(position: Int): Int {
        return if(list[position].attachments?.get(0)?.photo?.sizes?.size != null) POST_IMAGE else POST_TEXT_ONLY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            POST_IMAGE -> {
                PostWithImageViewHolder(inflater.inflate(R.layout.item_post, parent, false),
                    clickLikeListener = {
                        swipeLeftListener(list[it])
                    },
                    clickListener = {
                        onClickListener(it, list[it])
                    },
                    imageLoader = imageLoader)
            }
            else -> {
                PostViewHolder(inflater.inflate(R.layout.item_post, parent, false),
                    clickLikeListener = {
                        swipeLeftListener(list[it])
                    },
                    clickListener = {
                        onClickListener(it, list[it])
                    },
                    imageLoader = imageLoader)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when(holder) {
            is PostViewHolder -> holder.bind(list[position])
            is PostWithImageViewHolder -> holder.bind(list[position])
            else -> holder.bind(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun onItemSwipeRight(position: Int) {
        swipeRightListener(list[position])
    }

    override fun onItemSwipeLeft(position: Int) {
        swipeLeftListener(list[position])
    }

    override fun shouldWriteDate(position: Int): Boolean {
        if (position == 0) return true

        val currentDate = Date(list[position].date * 1000)
        val prevDate = Date(list[position - 1].date * 1000)
        return !currentDate.compareToExcludeTime(prevDate)
    }

    override fun getDateAtPosition(position: Int): String {
        return list[position].date.toRelativeDateStringDay()
    }

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(post: PostItem)
    }

    class PostWithImageViewHolder(
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
                imageLoader.loadPoster(post.attachments?.get(0)?.photo?.sizes?.get(0)!!, postContentImage)
                imageLoader.loadRoundedAvatar(post.sourceImage, postOwnerImage)

                postContentText.text = post.text
                postOwnerText.text = post.sourceName

                if (post.isFavorite) {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite))
                } else {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_outline))
                }
                postTimeText.text = post.date.toRelativeDateString()
                postLikeCountText.text = post.likes.toString()
                postShareCountText.text = post.reposts.toString()
                postCommentCountText.text = post.comments.toString()
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

                if (post.isFavorite) {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite))
                } else {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_outline))
                }
                postTimeText.text = post.date.toRelativeDateString()
                postLikeCountText.text = post.likes.toString()
                postShareCountText.text = post.reposts.toString()
                postCommentCountText.text = post.comments.toString()
            }
        }
    }

}