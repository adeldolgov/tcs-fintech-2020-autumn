package com.adeldolgov.feeder.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.diffutil.CommentDiffUtilsCallback
import com.adeldolgov.feeder.ui.item.CommentItem
import com.adeldolgov.feeder.util.extension.debounceClick
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.view_social_post_comment.view.*

private const val COMMENT_TEXT_ONLY = 0
private const val COMMENT_IMAGE = 1

class CommentAdapter(
    val onImageClickListener: (Int, String) -> Unit,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<CommentAdapter.BaseViewHolder>() {

    private val diffUtil = AsyncListDiffer(this, CommentDiffUtilsCallback())

    var comments: List<CommentItem> = emptyList()
        set(value) {
            diffUtil.submitList(value)
            field = value
        }
        get() = diffUtil.currentList

    override fun getItemViewType(position: Int): Int {
        val photosSize = comments[position].attachments?.first()?.photo?.sizes?.size
        return photosSize?.let { COMMENT_IMAGE } ?: run { COMMENT_TEXT_ONLY }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            COMMENT_IMAGE -> createWithImageVH(inflater, parent)
            else -> createWithTextVH(inflater, parent)
        }
    }

    private fun createWithTextVH(inflater: LayoutInflater, parent: ViewGroup): CommentViewHolder {
        return CommentViewHolder(
            inflater.inflate(R.layout.item_comment, parent, false),
            imageLoader = imageLoader
        )
    }

    private fun createWithImageVH(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): CommentWithImageViewHolder {
        return CommentWithImageViewHolder(
            inflater.inflate(R.layout.item_comment, parent, false),
            clickImageListener = { absPos, bindPos ->
                onImageClickListener(absPos, comments[bindPos].attachments?.first()?.photo?.sizes?.last()?.url!!)
            },
            imageLoader = imageLoader
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    override fun getItemId(position: Int): Long {
        return comments[position].id
    }

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(comment: CommentItem)
    }

    class CommentWithImageViewHolder(
        view: View,
        clickImageListener: (Int, Int) -> Unit,
        private val imageLoader: ImageLoader
    ) : BaseViewHolder(view) {
        init {
            itemView.commentContentImage.debounceClick { clickImageListener(absoluteAdapterPosition, bindingAdapterPosition) }
        }

        override fun bind(comment: CommentItem) {
            with(itemView) {
                postCommentViewGroup.updateCommentDetails(imageLoader, comment)
            }
        }
    }

    class CommentViewHolder(view: View, private val imageLoader: ImageLoader) : BaseViewHolder(view) {
        override fun bind(comment: CommentItem) {
            with(itemView) {
                postCommentViewGroup.updateCommentDetails(imageLoader, comment)
            }
        }
    }

}