package com.adeldolgov.homework_2.ui.fragment

import android.app.ActivityOptions
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import com.adeldolgov.homework_2.util.Status
import com.adeldolgov.homework_2.util.isShimmering
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_social_list_shimmer.*
import kotlinx.android.synthetic.main.view_social_post.view.*

class NewsFragment : Fragment() {

    companion object {
        private const val FAVORITE_ONLY = "favorite_only"

        fun newInstance(favoriteNews: Boolean): NewsFragment = NewsFragment().apply {
            arguments = bundleOf(FAVORITE_ONLY to favoriteNews)
        }
    }

    private val postViewModel: PostsViewModel by activityViewModels()
    private val postAdapter = PostAdapter(
        { postViewModel.changePostLike(it) },
        { postViewModel.removePostAt(it) },
        { position, postItem -> prepareDetailOptions(position, postItem) })
    private var shouldScrollToStart = false
    private var newsFragmentListener: OnPostLikeListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postViewModel.postList.observe(viewLifecycleOwner, Observer {
            Log.d(NewsFragment::class.java.name, it.status.name)
            when (it.status) {
                Status.SUCCESS -> {
                    processNewsUpdate(it.data!!)
                    changeShimmerRecycler(false)
                }
                Status.EMPTY -> showErrorLayout(getString(R.string.error_header_empty), getString(R.string.error_empty))
                Status.LOADING -> changeShimmerRecycler(true)
                Status.ERROR -> showErrorLayout(getString(R.string.error_header), it.message.toString())
            }
        })

        postRecyclerView.apply {
            adapter = postAdapter
            ItemTouchHelper(SwipeItemTouchHelperCallback(postAdapter)).attachToRecyclerView(this)
            addItemDecoration(DateItemDecoration(postAdapter))
        }

        swipeRefreshLayout.setOnRefreshListener {
            postViewModel.getPosts()
            swipeRefreshLayout.isRefreshing = false
            shouldScrollToStart = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPostLikeListener) {
            newsFragmentListener = context
        }
    }

    private fun changeShimmerRecycler(show: Boolean) {
        shimmerLayout.isShimmering = show
        postRecyclerView.isVisible = !show
        swipeRefreshLayout.isEnabled = !show
        errorLayout.isVisible = false
    }

    private fun processNewsUpdate(list: List<PostItem>) {
        val favoritePostList = list.toMutableList().filter { postItem -> postItem.isFavorite }
        val isFavorite = requireArguments().getBoolean(FAVORITE_ONLY)
        postAdapter.list = if (isFavorite) favoritePostList else list
        if (shouldScrollToStart) {
            postRecyclerView.post {
                postRecyclerView.scrollToPosition(0)
                shouldScrollToStart = false
            }
        }
        newsFragmentListener?.onFavoriteVisibility(favoritePostList.isNotEmpty())
    }

    private fun showErrorLayout(errorHeader: String, error: String){
        shimmerLayout.isShimmering = false
        errorLayout.isVisible = true
        swipeRefreshLayout.isEnabled = true
        errorText.text = error
        errorHeaderText.text = errorHeader
        newsFragmentListener?.onFavoriteVisibility(false)
    }

    /* задание из обязательной части
    private fun showErrorDialog(error: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_header)
            .setMessage(error)
            .setPositiveButton(android.R.string.yes) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    } */

    private fun prepareDetailOptions(position: Int, postItem: PostItem) {
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