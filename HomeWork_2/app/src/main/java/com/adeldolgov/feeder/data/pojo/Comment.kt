package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class Comment(
    @SerializedName("id")
    val id: Long,

    @SerializedName("date")
    val date: Long,

    @SerializedName("post_id")
    val postId: Long,

    @SerializedName("from_id")
    val fromId: Long,

    @SerializedName("text")
    val text: String,

    @SerializedName("attachments")
    val attachments: Array<Attachment>?,

    @SerializedName("likes")
    val likes: Like,

    @SerializedName("deleted")
    val deleted: Boolean

)
