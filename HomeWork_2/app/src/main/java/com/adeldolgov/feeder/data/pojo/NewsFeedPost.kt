package com.adeldolgov.feeder.data.pojo

import com.adeldolgov.feeder.data.database.entity.PostEntity
import com.adeldolgov.feeder.data.mapper.toPostEntity
import com.google.gson.annotations.SerializedName

class NewsFeedPost(
    @SerializedName("post_id")
    val id: Long,

    @SerializedName("source_id")
    val sourceId: Long,

    date: Long,

    text: String,

    attachments: Array<Attachment>?,

    likes: Like,

    reposts: Repost,

    comments: Comments

) : BasePost(date, text, attachments, likes, reposts, comments) {

    override fun mapToPostEntity(): PostEntity {
        return this.toPostEntity()
    }

}
