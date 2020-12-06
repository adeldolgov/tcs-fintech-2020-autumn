package com.adeldolgov.feeder.ui.item

import android.os.Parcelable
import com.adeldolgov.feeder.data.pojo.Attachment
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentItem (
    val id: Long,
    val postId: Long,
    val sourceId: Long,
    val sourceName: String,
    val sourceImage: String,
    val date: Long,
    val text: String,
    val attachments: Array<Attachment>?,
    var likes: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommentItem

        if (id != other.id) return false
        if (postId != other.postId) return false
        if (sourceId != other.sourceId) return false
        if (sourceName != other.sourceName) return false
        if (sourceImage != other.sourceImage) return false
        if (date != other.date) return false
        if (text != other.text) return false
        if (attachments != null) {
            if (other.attachments == null) return false
            if (!attachments.contentEquals(other.attachments)) return false
        } else if (other.attachments != null) return false
        if (likes != other.likes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + postId.hashCode()
        result = 31 * result + sourceId.hashCode()
        result = 31 * result + sourceName.hashCode()
        result = 31 * result + sourceImage.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + (attachments?.contentHashCode() ?: 0)
        result = 31 * result + likes
        return result
    }
}