package com.adeldolgov.feeder.data.mapper

import com.adeldolgov.feeder.data.database.entity.PostEntity
import com.adeldolgov.feeder.data.database.entity.SourceEntity
import com.adeldolgov.feeder.data.pojo.NewsFeedPost
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.util.PostType
import com.adeldolgov.feeder.util.PostTypes
import com.adeldolgov.feeder.util.extension.toBoolean


fun NewsFeedPost.toPostEntity(): PostEntity =
    PostEntity(
        id = id,
        date = date,
        sourceId = sourceId,
        attachment = attachments,
        text = text,
        likesCount = likes.count,
        hasUserLike = likes.userLike,
        repostsCount = reposts.count,
        commentsCount = comments.count,
        postTypes = PostTypes.of(PostType.NEWS_FEED_POST),
        canComment = comments.canComment.toBoolean()
    )

fun PostEntity.toPostItem(source: SourceEntity): PostItem =
    PostItem(
        id = id,
        date = date,
        sourceId = sourceId,
        sourceName = source.name,
        sourceImage = source.photo100,
        text = text,
        attachments = attachment,
        hasUserLike = hasUserLike.toBoolean(),
        likes = likesCount,
        comments = commentsCount,
        reposts = repostsCount,
        canComment = canComment
    )


