package com.adeldolgov.feeder.ui.view

import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.ui.item.ProfileItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SingleStateStrategy
import moxy.viewstate.strategy.StateStrategyType


interface ProfileView : MvpView {

    @StateStrategyType(value = SingleStateStrategy::class)
    fun showProfile(profile: ProfileItem)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showProfileLoading()

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showProfileError(error: Throwable)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showProfilePosts(posts: List<PostItem>)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showPostsEmpty()

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showPostsError(error: Throwable)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showPostsLoading(isFullScreenLoading: Boolean)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showPostSending()

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showPostSendingError(error: Throwable)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun hidePostSending()

}