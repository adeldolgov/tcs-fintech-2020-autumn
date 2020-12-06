package com.adeldolgov.feeder.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    @SerializedName("id")
    val id: Long,

    @SerializedName("owner_id")
    val ownerId: Long,

    @SerializedName("sizes")
    val sizes: Array<PhotoSize>
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Photo

        if (id != other.id) return false
        if (ownerId != other.ownerId) return false
        if (!sizes.contentEquals(other.sizes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + sizes.contentHashCode()
        return result
    }
}