package com.adeldolgov.feeder.data.mapper

import com.adeldolgov.feeder.data.database.entity.PostEntity
import com.adeldolgov.feeder.data.pojo.*
import com.adeldolgov.feeder.ui.item.PostItem
import com.adeldolgov.feeder.util.extension.toBoolean
import kotlin.math.absoluteValue

fun Post.toPostItem(source: Source): PostItem =
    PostItem(
        id = id,
        sourceId = sourceId,
        sourceName = source.name,
        sourceImage = source.photo100,
        date = date,
        hasUserLike = likes.userLike.toBoolean(),
        text = text,
        attachments = attachments,
        likes = likes.count,
        reposts = reposts.count,
        comments = comments.count
    )

fun Post.toPostEntity(): PostEntity =
    PostEntity(
        id = id,
        date = date,
        sourceId = sourceId.absoluteValue,
        attachment = attachments,
        text = text,
        likesCount = likes.count,
        hasUserLike = likes.userLike,
        repostsCount = reposts.count,
        commentsCount = comments.count
    )

fun PostEntity.toPost(): Post =
    Post(
        id = id,
        date = date,
        sourceId = sourceId * -1,
        text = text,
        attachments = attachment,
        likes = Like(count = likesCount, userLike = hasUserLike),
        comments = Comment(count = commentsCount),
        reposts = Repost(count = repostsCount)
    )


