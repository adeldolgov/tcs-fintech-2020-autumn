package com.adeldolgov.feeder.ui.fragment

import android.app.ActivityOptions
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.ui.activity.PostDetailActivity
import com.adeldolgov.feeder.ui.adapter.LoadStateAdapter
import com.adeldolgov.feeder.ui.adapter.PostAdapter
import com.adeldolgov.feeder.ui.adapter.ProfileAdapter
import com.adeldolgov.feeder.ui.decorator.DateItemDecoration
import com.adeldolgov.feeder.ui.decorator.PaddingItemDecoration
import com.adeldolgov.feeder.ui.dialogfragment.NewPostBottomSheetDialogFragment
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.item.ProfileItem
import com.adeldolgov.feeder.ui.itemtouchhelper.SwipeItemTouchHelperCallback
import com.adeldolgov.feeder.ui.onscrolllistener.PaginationListener
import com.adeldolgov.feeder.ui.view.ProfileView
import com.adeldolgov.feeder.util.ProfileViewType
import com.adeldolgov.feeder.util.extension.*
import kotlinx.android.synthetic.main.fragment_news.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_profile_list_shimmer.*
import kotlinx.android.synthetic.main.view_social_post.view.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File

class ProfileFragment : MvpAppCompatFragment(R.layout.fragment_profile), ProfileView, NewPostBottomSheetDialogFragment.OnPostCreateListener {

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

    private val profilePresenter by moxyPresenter {
        requireContext().feederApp().profileComponent!!.provideProfilePresenter()
    }

    private val profileAdapter = ProfileAdapter(FeederApp.instance.appComponent.provideImageLoader())
    private val postAdapter = PostAdapter(
        swipeLeftListener = { profilePresenter.changePostLike(it) },
        swipeRightListener = { profilePresenter.deletePost(it) },
        onClickListener = { position, postItem -> prepareDetailOptionsAndStartDetailsActivity(position, postItem) }
    )
    private val loadStateAdapter = LoadStateAdapter { profilePresenter.fetchNewPosts(postAdapter.itemCount) }
    private var shouldScrollToStart = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileRecyclerView.apply {
            adapter = ConcatAdapter(profileAdapter, postAdapter, loadStateAdapter)
            addItemDecoration(PaddingItemDecoration(profileAdapter))
            addItemDecoration(DateItemDecoration(postAdapter))
            ItemTouchHelper(SwipeItemTouchHelperCallback(postAdapter)).attachToRecyclerView(this)

            addOnScrollListener(object : PaginationListener(layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() =
                    profilePresenter.fetchNewPosts(postAdapter.itemCount)
                override fun isLastPage(): Boolean =
                    profilePresenter.recyclerViewIsLastPage
                override fun isLoading(): Boolean =
                    profilePresenter.recyclerViewPostsPageLoading
            })
        }

        swipeRefreshLayout.setOnRefreshListener {
            shouldScrollToStart = true
            profilePresenter.getProfile()
            swipeRefreshLayout.isRefreshing = false
        }

        addPostBtn.debounceClick { showPostCreationBottomSheet() }
    }

    private fun showPostCreationBottomSheet() {
        val sheet = NewPostBottomSheetDialogFragment()
        sheet.show(childFragmentManager, NewPostBottomSheetDialogFragment::class.java.name)
    }

    override fun showProfile(profile: ProfileItem) {
        hideErrorScreen()
        showLoadingScreen(false)
        setupProfileItem(profile)
        if (shouldScrollToStart) {
            profileRecyclerView.post {
                profileRecyclerView.scrollToPosition(0)
                shouldScrollToStart = false
            }
        }
    }

    override fun showProfileLoading() {
        hideErrorScreen()
        showLoadingScreen(true)
    }

    override fun showProfileError(error: Throwable) {
        showLoadingScreen(false)
        showErrorScreenWithContent(getString(R.string.error_header), requireContext().parseErrorToLocalizedText(error))
    }

    override fun showPostsLoading(isFullScreenLoading: Boolean) {
        loadStateAdapter.showLoadingState()
    }

    override fun showProfilePosts(posts: List<PostItem>) {
        loadStateAdapter.removeState()
        postAdapter.posts = posts
    }

    override fun showPostsEmpty() {
        loadStateAdapter.showEmptyState(getString(R.string.no_posts))
    }

    override fun showPostsError(error: Throwable) {
        loadStateAdapter.showErrorState(requireContext().parseErrorToLocalizedText(error))
    }

    override fun showPostSending() {
        findNewPostBottomSheetDialog()?.apply { showProgressSending(true) }
    }

    override fun showPostSendingError(error: Throwable) {
        findNewPostBottomSheetDialog()?.apply { showProgressSending(false) }
        requireContext().toast(requireContext().parseErrorToLocalizedText(error))
    }

    override fun hidePostSending() {
        findNewPostBottomSheetDialog()?.dismiss()
    }

    override fun onCreatePost(text: String, photoFile: File?) {
        profilePresenter.createPost(text, photoFile)
    }

    private fun findNewPostBottomSheetDialog(): NewPostBottomSheetDialogFragment? {
        return childFragmentManager.findFragmentByTag(NewPostBottomSheetDialogFragment::class.java.name) as NewPostBottomSheetDialogFragment?
    }

    private fun setupProfileItem(profile: ProfileItem) {
        val profileItems = mutableListOf<ProfileViewType>()

        profileItems.add(ProfileViewType.Profile(profile))
        if (profile.city != null || profile.country != null) {
            profileItems.add(ProfileViewType.FieldHeader(getString(R.string.location)))
            profileItems.add(
                ProfileViewType.FieldItem(profile.city ?: getString(R.string.error_no_field),
                    profile.country ?: getString(R.string.error_no_field))
            )
        }
        profileItems.add(ProfileViewType.FieldHeader(getString(R.string.age)))
        val years = profile.birthdayDate.currentDateYears()
        profileItems.add(ProfileViewType.FieldItem(resources.getQuantityString(R.plurals.years, years, years), profile.birthdayDate))
        if (profile.universityName.isNotEmpty()) {
            profileItems.add(ProfileViewType.FieldHeader(getString(R.string.education)))
            profileItems.add(ProfileViewType.FieldItem(profile.facultyName, profile.universityName))
        }
        if (profile.career.isNotEmpty()) {
            profileItems.add(ProfileViewType.FieldHeader(getString(R.string.job)))
            profile.career.forEach {
                profileItems.add(
                    ProfileViewType.TimeFieldWithImageItem(
                        it.from, it.until, it.position ?: it.company, it.company, it.groupPhoto
                    )
                )
            }
        }

        profileAdapter.profileFields = profileItems
    }

    private fun showLoadingScreen(show: Boolean) {
        profileRecyclerView.isVisible = !show
        shimmerLayout.isShimmering = show
        swipeRefreshLayout.isEnabled = !show
        addPostBtn.isVisible = !show
    }

    private fun showErrorScreenWithContent(errorHeader: String, error: String){
        profileRecyclerView.isVisible = false
        addPostBtn.isVisible = false
        errorLayout.isVisible = true
        errorText.text = error
        errorHeaderText.text = errorHeader
    }

    private fun hideErrorScreen() {
        errorLayout.isVisible = false
    }

    private fun prepareDetailOptionsAndStartDetailsActivity(position: Int, postItem: PostItem) {
        val viewHolder = profileRecyclerView.findViewHolderForAdapterPosition(position)
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
            PostDetailActivity.createIntent(requireContext(), postItem),
            options.toBundle()
        )
    }
}