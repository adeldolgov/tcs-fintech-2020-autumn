package com.adeldolgov.feeder.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.diffutil.PostDiffUtilsCallback
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.util.extension.debounceClick
import com.adeldolgov.feeder.util.extension.saveImageToCache
import com.adeldolgov.feeder.util.extension.sharePhotoFile
import com.adeldolgov.feeder.util.extension.shareTextMessage
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import kotlinx.android.synthetic.main.item_post_detail.view.*
import kotlinx.android.synthetic.main.view_social_post.view.*

private const val POST_DETAILS_ONLY = 0

class PostDetailAdapter(
    val onLikeClickListener: (PostItem) -> Unit,
    val onImageClickListener: (Int, String) -> Unit,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<PostDetailAdapter.BaseViewHolder>() {

    private val diffUtil = AsyncListDiffer(this, PostDiffUtilsCallback())

    var posts: List<PostItem> = emptyList()
        set(value) {
            diffUtil.submitList(value)
            field = value
        }
        get() = diffUtil.currentList

    override fun getItemViewType(position: Int): Int {
        return POST_DETAILS_ONLY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return createDetailPostVH(inflater, parent)
    }

    private fun createDetailPostVH(inflater: LayoutInflater, parent: ViewGroup): PostDetailViewHolder {
        return PostDetailViewHolder(
            inflater.inflate(R.layout.item_post_detail, parent, false),
            clickLikeListener = { onLikeClickListener(posts[it]) },
            clickImageListener = {
                posts[it].attachments?.first()?.photo?.sizes?.last()?.url?.let { url ->
                    onImageClickListener(it, url)
                }
            },
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

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(post: PostItem)
    }

    class PostDetailViewHolder(view: View,
                               clickLikeListener: (Int) -> Unit,
                               clickImageListener: (Int) -> Unit,
                               private val imageLoader: ImageLoader
    ) : BaseViewHolder(view) {
        init {
            itemView.postLikeBtn.debounceClick { clickLikeListener(bindingAdapterPosition) }
            itemView.postContentImage.debounceClick { clickImageListener(bindingAdapterPosition) }
        }

        override fun bind(post: PostItem) {
            with(itemView) {
                itemView.detailPostViewGroup.updatePostDetails(postItem = post, imageLoader = imageLoader)
                postShareBtn.debounceClick {
                    if (post.attachments?.first()?.photo?.sizes?.size == null) {
                        context.shareTextMessage(post.text, post.sourceName)
                    } else {
                        (postContentImage.drawable as BitmapDrawable?)?.bitmap?.let {
                            context.sharePhotoFile(post.text, post.sourceName, context.saveImageToCache(it))
                        }
                    }
                }
            }
        }
    }

}