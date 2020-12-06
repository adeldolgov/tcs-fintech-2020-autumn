package com.adeldolgov.feeder.ui.view

import com.adeldolgov.feeder.ui.item.CommentItem
import com.adeldolgov.feeder.ui.item.PostItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType


interface PostDetailView : MvpView {

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showPost(postItem: PostItem)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showPostError(error: Throwable)

    @StateStrategyType(value = AddToEndSingleStrategy::class)
    fun showPostComments(comments: List<CommentItem>)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showCommentsEmpty()

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showCommentsError(error: Throwable)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showCommentsLoading(isFullScreenLoading: Boolean)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showCommentSending()

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showCommentSendingError(error: Throwable)

}