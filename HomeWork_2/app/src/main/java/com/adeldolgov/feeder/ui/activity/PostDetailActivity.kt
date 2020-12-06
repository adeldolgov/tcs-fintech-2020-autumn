package com.adeldolgov.feeder.ui.activity

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.adapter.CommentAdapter
import com.adeldolgov.feeder.ui.adapter.LoadStateAdapter
import com.adeldolgov.feeder.ui.adapter.PostDetailAdapter
import com.adeldolgov.feeder.ui.item.CommentItem
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.onscrolllistener.PaginationListener
import com.adeldolgov.feeder.ui.view.PostDetailView
import com.adeldolgov.feeder.util.extension.feederApp
import com.adeldolgov.feeder.util.extension.parseErrorToLocalizedText
import com.adeldolgov.feeder.util.extension.toast
import kotlinx.android.synthetic.main.activity_post_details.*
import kotlinx.android.synthetic.main.view_error.*
import moxy.ktx.moxyPresenter


class PostDetailActivity : BaseMvpActivity(R.layout.activity_post_details), PostDetailView {

    companion object {
        private const val POST_EXTRA = "post_extra"

        fun createIntent(context: Context, postItem: PostItem): Intent {
            return Intent(context, PostDetailActivity::class.java).apply {
                putExtra(POST_EXTRA, postItem)
            }
        }
    }

    private val postDetailPresenter by moxyPresenter {
        feederApp().postDetailComponent!!.providePostDetailPresenter()
    }

    private val postDetailAdapter = PostDetailAdapter(
        onLikeClickListener = { postDetailPresenter.changePostLike(it) },
        onImageClickListener = { position, imageUrl -> startImageDetailsActivity(position, imageUrl) },
        imageLoader = FeederApp.instance.appComponent.provideImageLoader()
    )

    private val commentAdapter = CommentAdapter(
        onImageClickListener = { position, imageUrl -> startImageDetailsActivity(position, imageUrl) },
        imageLoader = FeederApp.instance.appComponent.provideImageLoader()
    )

    private lateinit var loadStateAdapter: LoadStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportPostponeEnterTransition()
        intent.getParcelableExtra<PostItem>(POST_EXTRA)?.let {

            loadStateAdapter = LoadStateAdapter { postDetailPresenter.fetchComments(it, commentAdapter.itemCount) }

            postDetailRecycler.apply {
                adapter = ConcatAdapter(postDetailAdapter, commentAdapter, loadStateAdapter)
                postDetailPresenter.getComments(it)

                addOnScrollListener(object : PaginationListener(layoutManager as LinearLayoutManager) {
                    override fun loadMoreItems() =
                        postDetailPresenter.fetchComments(it, commentAdapter.itemCount)
                    override fun isLastPage(): Boolean =
                        postDetailPresenter.recyclerViewIsLastPage
                    override fun isLoading(): Boolean =
                        postDetailPresenter.recyclerViewCommentsPageLoading
                })
            }
            showPost(it)
            sendBtn.setOnClickListener { _ ->
                postDetailPresenter.createComment(it, commentEditText.text.toString())
            }
            if (savedInstanceState == null) sendBtn.isEnabled = false
            commentEditText.addTextChangedListener { sendBtn.isEnabled = it?.isNotBlank() ?: false }

        }

        postDetailRecycler.viewTreeObserver
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    postDetailRecycler.viewTreeObserver.removeOnPreDrawListener(this)
                    supportStartPostponedEnterTransition()
                    return false
                }
            })

    }

    override fun showPost(postItem: PostItem) {
        postDetailAdapter.posts = listOf(postItem)
        bottomCommentCard.isVisible = postItem.canComment
    }

    override fun showPostError(error: Throwable) {
        showErrorScreenWithContent(getString(R.string.error_header), parseErrorToLocalizedText(error))
    }

    override fun showPostComments(comments: List<CommentItem>) {
        commentAdapter.comments = comments
        loadStateAdapter.removeState()
        switchCommentProgress(false)
    }

    override fun showCommentsEmpty() {
        loadStateAdapter.showEmptyState(getString(R.string.comments_empty))
    }

    override fun showCommentsError(error: Throwable) {
        loadStateAdapter.showErrorState(parseErrorToLocalizedText(error))
    }

    override fun showCommentsLoading(isFullScreenLoading: Boolean) {
        loadStateAdapter.showLoadingState()
    }

    override fun showCommentSending() {
        switchCommentProgress(true)
    }

    override fun showCommentSendingError(error: Throwable) {
        toast(parseErrorToLocalizedText(error))
        switchCommentProgress(false)
    }

    private fun switchCommentProgress(isSending: Boolean) {
        sendingProgress.isVisible = isSending
        sendBtn.isGone = isSending
        commentEditText.isEnabled = !isSending
        if (!isSending) commentEditText.text.clear()
    }

    private fun showErrorScreenWithContent(errorHeader: String, error: String){
        postDetailRecycler.isVisible = false
        errorLayout.isVisible = true
        errorText.text = error
        errorHeaderText.text = errorHeader
    }

    private fun startImageDetailsActivity(viewHolderPosition: Int, url: String) {
        val viewHolder = postDetailRecycler.findViewHolderForAdapterPosition(viewHolderPosition)
        if (viewHolder != null) {
            val selectedView: View = viewHolder.itemView.findViewById(R.id.postContentImage)
                ?: viewHolder.itemView.findViewById(R.id.commentContentImage)
            val options = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(selectedView, selectedView.transitionName)
            )
            startActivity(
                ImageDetailsActivity.createIntent(this, url, selectedView.transitionName),
                options.toBundle()
            )
        }
    }

}