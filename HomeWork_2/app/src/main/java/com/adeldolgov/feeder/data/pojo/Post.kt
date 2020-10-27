package com.adeldolgov.feeder.data.pojo

import com.adeldolgov.feeder.data.item.PostItem
import com.adeldolgov.feeder.util.toBoolean
import com.google.gson.annotations.SerializedName

class Post(
    @SerializedName("post_id")
    val id: Long,
    @SerializedName("date")
    val date: Long,
    @SerializedName("source_id")
    val sourceId: Long,
    @SerializedName("text")
    val text: String,
    @SerializedName("attachments")
    val attachments: Array<Attachment>?,
    @SerializedName("likes")
    val likes: Like,
    @SerializedName("reposts")
    val reposts: Repost,
    @SerializedName("comments")
    val comments: Comment
)

fun Post.toPostItem(source: Source): PostItem = PostItem(
    id = id,
    sourceId = sourceId,
    sourceName = source.name,
    sourceImage = source.photo100,
    date = date,
    hasUserLike = likes.userLike.toBoolean(),
    text = text,
    attachments =  attachments,
    likes = likes.count,
    reposts = reposts.count,
    comments = comments.count
)