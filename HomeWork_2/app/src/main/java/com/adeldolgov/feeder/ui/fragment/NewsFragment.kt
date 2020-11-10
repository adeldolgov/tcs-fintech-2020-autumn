package com.adeldolgov.feeder.ui.fragment

import android.app.ActivityOptions
import android.content.Context
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.activity.PostDetailsActivity
import com.adeldolgov.feeder.ui.adapter.PostAdapter
import com.adeldolgov.feeder.ui.decorator.DateItemDecoration
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.itemtouchhelper.SwipeItemTouchHelperCallback
import com.adeldolgov.feeder.ui.onscrolllistener.PaginationListener
import com.adeldolgov.feeder.ui.presenter.NewsFeedPresenter
import com.adeldolgov.feeder.ui.view.NewsFeedView
import com.adeldolgov.feeder.util.extension.isShimmering
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_social_list_shimmer.*
import kotlinx.android.synthetic.main.view_social_post.view.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class NewsFragment : MvpAppCompatFragment(R.layout.fragment_news), NewsFeedView {

    companion object {
        private const val FAVORITE_ONLY = "favorite_only"

        fun newInstance(favoriteNews: Boolean): NewsFragment = NewsFragment().apply {
            arguments = bundleOf(FAVORITE_ONLY to favoriteNews)
        }
    }

    private val newsFeedPresenter by moxyPresenter { NewsFeedPresenter(FeederApp.instance.applicationContainer.newsFeedRepository) }
    private val postAdapter = PostAdapter(
        { newsFeedPresenter.changePostLike(it) },
        { newsFeedPresenter.ignorePost(it) },
        { position, postItem -> prepareDetailOptionsAndStartDetailsActivity(position, postItem) })

    private var shouldScrollToStart = false
    private var newsFragmentListener: OnPostLikeListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postRecyclerView.apply {
            adapter = postAdapter
            ItemTouchHelper(SwipeItemTouchHelperCallback(postAdapter)).attachToRecyclerView(this)
            addItemDecoration(DateItemDecoration(postAdapter))
            addOnScrollListener(object : PaginationListener(layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() = newsFeedPresenter.fetchNewPosts()
                override val isLastPage: Boolean = false
                override fun isLoading(): Boolean = newsFeedPresenter.recyclerViewPageLoading
            })
        }

        swipeRefreshLayout.setOnRefreshListener {
            shouldScrollToStart = true
            newsFeedPresenter.getPosts(true)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPostLikeListener) {
            newsFragmentListener = context
        }
    }

    override fun showNewsFeed(posts: List<PostItem>) {
        showLoadingScreen(false)
        updateRecyclerAdapterPostsAndScrollToStart(posts)
    }

    override fun showEmptyState() {
        showLoadingScreen(false)
        showErrorScreenWithContent(getString(R.string.error_header_empty), getString(R.string.error_empty))
    }

    override fun showError(error: String) {
        showLoadingScreen(false)
        showErrorScreenWithContent(getString(R.string.error_header), error)
    }

    override fun showLoading(isFullScreenLoading: Boolean) {
        hideErrorScreen()
        showLoadingScreen(true)
    }

    private fun showLoadingScreen(show: Boolean) {
        postRecyclerView.isVisible = !show
        shimmerLayout.isShimmering = show
        swipeRefreshLayout.isEnabled = !show
    }

    private fun showErrorScreenWithContent(errorHeader: String, error: String){
        postRecyclerView.isVisible = false
        errorLayout.isVisible = true
        errorText.text = error
        errorHeaderText.text = errorHeader
        newsFragmentListener?.onFavoriteVisibility(false)
    }

    private fun hideErrorScreen() {
        errorLayout.isVisible = false
    }

    private fun updateRecyclerAdapterPostsAndScrollToStart(posts: List<PostItem>) {
        val favoritePosts = posts.toMutableList().filter { postItem -> postItem.hasUserLike }
        val isFavorite = requireArguments().getBoolean(FAVORITE_ONLY)
        postAdapter.posts = if (isFavorite) favoritePosts else posts
        if (shouldScrollToStart) {
            postRecyclerView.post {
                postRecyclerView.scrollToPosition(0)
                shouldScrollToStart = false
            }
        }
        newsFragmentListener?.onFavoriteVisibility(favoritePosts.isNotEmpty())
    }

    private fun prepareDetailOptionsAndStartDetailsActivity(position: Int, postItem: PostItem) {
        val viewHolder = postRecyclerView.findViewHolderForAdapterPosition(position)
        if (viewHolder != null) {
            startDetailsActivity(viewHolder, postItem)
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
        requireActivity().startActivity(
            PostDetailsActivity.createIntent(requireContext(), postItem),
            options.toBundle()
        )
    }

    interface OnPostLikeListener {
        fun onFavoriteVisibility(visible: Boolean)
    }
}