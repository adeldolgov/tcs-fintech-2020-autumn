package com.adeldolgov.feeder.data.item

import android.os.Parcelable
import com.adeldolgov.feeder.data.pojo.Attachment
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostItem (
    val id: Long,
    val sourceId: Long,
    val sourceName: String,
    val sourceImage: String,
    val date: Long,
    var hasUserLike: Boolean,
    val text: String,
    val attachments: Array<Attachment>?,
    var likes: Int,
    var reposts: Int,
    var comments: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostItem

        if (id != other.id) return false
        if (sourceId != other.sourceId) return false
        if (sourceName != other.sourceName) return false
        if (sourceImage != other.sourceImage) return false
        if (date != other.date) return false
        if (hasUserLike != other.hasUserLike) return false
        if (text != other.text) return false
        if (attachments != null) {
            if (other.attachments == null) return false
            if (!attachments.contentEquals(other.attachments)) return false
        } else if (other.attachments != null) return false
        if (likes != other.likes) return false
        if (reposts != other.reposts) return false
        if (comments != other.comments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sourceId.hashCode()
        result = 31 * result + sourceName.hashCode()
        result = 31 * result + sourceImage.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + hasUserLike.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + (attachments?.contentHashCode() ?: 0)
        result = 31 * result + likes
        result = 31 * result + reposts
        result = 31 * result + comments
        return result
    }

}