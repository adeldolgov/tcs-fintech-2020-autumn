package com.adeldolgov.homework_2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.homework_2.R
import com.adeldolgov.homework_2.data.item.PostItem
import com.adeldolgov.homework_2.ui.ItemTouchHelper.LDItemTouchHelperCallback
import com.adeldolgov.homework_2.ui.decorator.DateItemDecoration
import com.adeldolgov.homework_2.ui.diffutil.PostDiffUtilsCallback
import com.adeldolgov.homework_2.util.compareToExcludeTime
import com.adeldolgov.homework_2.util.toRelativeDateString
import com.adeldolgov.homework_2.util.toRelativeDateStringDay
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.view_social_post.view.*
import java.util.*

private const val POST_TEXT_ONLY = 0
private const val POST_IMAGE = 1

class PostAdapter(val clickListener: (Int) -> Unit) : RecyclerView.Adapter<PostAdapter.BaseViewHolder>(),
    LDItemTouchHelperCallback.LDItemTouchHelperAdapter, DateItemDecoration.DateItemInterface {

    private val diffUtil = AsyncListDiffer(this, PostDiffUtilsCallback())

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
            POST_TEXT_ONLY -> PostViewHolder(inflater.inflate(R.layout.item_post, parent, false)) { clickListener(it) }
            POST_IMAGE -> PostWithImageViewHolder(inflater.inflate(R.layout.item_post, parent, false)) { clickListener(it) }
            else -> BaseViewHolder(inflater.inflate(R.layout.item_post, parent, false)) { clickListener(it) }
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

    override fun onItemHide(position: Int) {
        list = list.toMutableList().apply {
            removeAt(position)
        }
    }

    override fun onItemLike(position: Int) {
        /*list[position].isFavorite = !list[position].isFavorite
        notifyItemChanged(position)
        без diffUtils, напрямую
        */
        val oldItem = list[position]
        val newItem = oldItem.copy(isFavorite = !oldItem.isFavorite, likes = if (oldItem.isFavorite) oldItem.likes - 1 else oldItem.likes + 1)
        list = list.toMutableList().apply {
            this[position] = newItem
        }
        notifyItemChanged(position) //вручную вернуть элемент списка из свайпа., т.к в DiffUtilsCallback овверайднут getChangePayload
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


    open class BaseViewHolder(view: View, clickLambda: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
        init {
            itemView.postLikeBtn.setOnClickListener { clickLambda(adapterPosition) }
        }
        open fun bind(post: PostItem) {
            with(itemView) {
                Glide.with(this)
                    .load(post.sourceImage)
                    .circleCrop()
                    .into(postOwnerImage)
                if (post.isFavorite) {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite))
                } else {
                    postLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_outline))
                }
                postOwnerText.text = post.sourceName
                postTimeText.text = post.date.toRelativeDateString()
                postLikeCountText.text = post.likes.toString()
                postShareCountText.text = post.reposts.toString()
                postCommentCountText.text = post.comments.toString()
            }
        }
    }

    class PostWithImageViewHolder(view: View, clickLambda: (Int) -> Unit) : BaseViewHolder(view, clickLambda) {
        override fun bind(post: PostItem) {
            super.bind(post)
            with(itemView) {
                Glide.with(this)
                    .load(post.attachments?.get(0)?.photo?.sizes?.get(0))
                    .fitCenter()
                    .into(postContentImage)
                postContentText.text = post.text
            }
        }
    }

    class PostViewHolder(view: View, clickLambda: (Int) -> Unit) : BaseViewHolder(view, clickLambda) {
        override fun bind(post: PostItem) {
            super.bind(post)
            with(itemView) {
                postContentText.text = post.text
            }
        }
    }

}