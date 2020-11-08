package com.adeldolgov.feeder.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoSize(
    @SerializedName("height")
    val height: Int,

    @SerializedName("width")
    val width: Int,

    @SerializedName("type")
    val type: Char,

    @SerializedName("url")
    val url: String
): Parcelable