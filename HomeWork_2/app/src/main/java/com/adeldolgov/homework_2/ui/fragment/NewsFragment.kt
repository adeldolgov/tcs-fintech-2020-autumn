package com.adeldolgov.homework_2.ui.fragment

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.homework_2.R
import com.adeldolgov.homework_2.data.item.PostItem
import com.adeldolgov.homework_2.domain.viewmodel.PostsViewModel
import com.adeldolgov.homework_2.ui.activity.PostDetailsActivity
import com.adeldolgov.homework_2.ui.adapter.PostAdapter
import com.adeldolgov.homework_2.ui.decorator.DateItemDecoration
import com.adeldolgov.homework_2.ui.itemtouchhelper.SwipeItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.view_social_post.view.*

class NewsFragment : Fragment() {

    companion object {
        fun newInstance() = NewsFragment()
        const val TAG = "NewsFragment"
    }

    private val postViewModel: PostsViewModel by activityViewModels()
    private val postAdapter = PostAdapter(swipeLeftListener = {
        postViewModel.changePostLike(it)
    }, swipeRightListener = {
        postViewModel.removePostAt(it)
    }, onClickListener = { position, postItem ->
        val viewHolder = postRecyclerView.findViewHolderForAdapterPosition(position)
        if (viewHolder != null) {
            startDetailsActivity(viewHolder, postItem)
        }
    })
    private var newsFragmentListener: OnPostLikeListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postViewModel.postList.observe(viewLifecycleOwner, Observer {
            postAdapter.list = it
        })
        postViewModel.postList.observe(viewLifecycleOwner, Observer {
            val favoritePostList = it.toMutableList().filter { postItem -> postItem.isFavorite }
            newsFragmentListener?.onFavoriteVisibility(favoritePostList.isNotEmpty())
        })

        postRecyclerView.apply {
            adapter = postAdapter
            ItemTouchHelper(SwipeItemTouchHelperCallback(postAdapter)).attachToRecyclerView(this)
            addItemDecoration(DateItemDecoration(postAdapter))
        }

        swipeRefreshLayout.setOnRefreshListener {
            postViewModel.getPosts()
            swipeRefreshLayout.isRefreshing = false
            postRecyclerView.post {
                postRecyclerView.scrollToPosition(0)
            }
        }
    }

    private fun startDetailsActivity(viewHolder: RecyclerView.ViewHolder, postItem: PostItem) {
        val options = ActivityOptions.makeSceneTransitionAnimation(
            activity,
            Pair.create(
                viewHolder.itemView.postContentImage,
                viewHolder.itemView.postContentImage.transitionName
            ),
            Pair.create(
                viewHolder.itemView.postContentText,
                viewHolder.itemView.postContentText.transitionName
            ),
            Pair.create(
                viewHolder.itemView.postOwnerText,
                viewHolder.itemView.postOwnerText.transitionName
            ),
            Pair.create(
                viewHolder.itemView.postOwnerImage,
                viewHolder.itemView.postOwnerImage.transitionName
            ),
            Pair.create(
                viewHolder.itemView.postTimeText,
                viewHolder.itemView.postTimeText.transitionName
            )
        )
        activity?.startActivity(
            Intent(context, PostDetailsActivity::class.java)
                .putExtra(PostDetailsActivity.POST_EXTRA, postItem),
            options.toBundle()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPostLikeListener) {
            newsFragmentListener = context
        }
    }

    interface OnPostLikeListener {
        fun onFavoriteVisibility(visible: Boolean)
    }
}