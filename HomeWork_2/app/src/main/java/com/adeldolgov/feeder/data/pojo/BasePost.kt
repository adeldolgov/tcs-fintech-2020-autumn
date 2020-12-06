package com.adeldolgov.feeder.data.pojo

import com.adeldolgov.feeder.data.database.entity.PostEntity
import com.google.gson.annotations.SerializedName

abstract class BasePost(
    @SerializedName("date")
    val date: Long,

    @SerializedName("text")
    val text: String,

    @SerializedName("attachments")
    val attachments: Array<Attachment>?,

    @SerializedName("likes")
    val likes: Like,

    @SerializedName("reposts")
    val reposts: Repost,

    @SerializedName("comments")
    val comments: Comments
) : MapToPostEntityInterface

internal interface MapToPostEntityInterface {
    fun mapToPostEntity(): PostEntity
}