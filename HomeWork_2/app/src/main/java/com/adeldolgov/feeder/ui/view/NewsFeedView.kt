package com.adeldolgov.feeder.ui.view

import com.adeldolgov.feeder.ui.item.PostItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType


interface NewsFeedView : MvpView {

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showNewsFeed(posts: List<PostItem>)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showEmptyState()

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showError(error: Throwable)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showPostsFetchingError(error: Throwable)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showLoading(isFullScreenLoading: Boolean)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun hideLoading()

}