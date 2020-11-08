package com.adeldolgov.feeder.data.pojo

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
