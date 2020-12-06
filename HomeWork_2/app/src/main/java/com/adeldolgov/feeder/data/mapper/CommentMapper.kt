package com.adeldolgov.feeder.data.mapper

import com.adeldolgov.feeder.data.database.entity.CommentEntity
import com.adeldolgov.feeder.data.database.entity.SourceEntity
import com.adeldolgov.feeder.data.pojo.Comment
import com.adeldolgov.feeder.data.pojo.Source
import com.adeldolgov.feeder.ui.item.CommentItem
import kotlin.math.absoluteValue

fun Comment.toCommentItem(source: Source): CommentItem =
    CommentItem(
        id = id,
        postId = postId,
        sourceId = fromId,
        sourceName = source.name,
        sourceImage = source.photo100,
        date = date,
        text = text,
        attachments = attachments,
        likes = likes.count
    )

fun Comment.toCommentEntity(): CommentEntity =
    CommentEntity(
        id = id,
        date = date,
        postId = postId,
        fromId = fromId.absoluteValue,
        attachments = attachments,
        text = text,
        likesCount = likes.count
    )

fun CommentEntity.toCommentItem(source: SourceEntity): CommentItem =
    CommentItem(
        id = id,
        date = date,
        postId = postId,
        sourceId = fromId,
        sourceImage = source.photo100,
        sourceName = source.name,
        attachments = attachments,
        text = text,
        likes = likesCount
    )
