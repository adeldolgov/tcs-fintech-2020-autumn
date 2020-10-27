package com.adeldolgov.feeder.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    @SerializedName("sizes")
    val sizes: Array<PhotoSize>
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Photo

        if (!sizes.contentEquals(other.sizes)) return false

        return true
    }

    override fun hashCode(): Int {
        return sizes.contentHashCode()
    }
}