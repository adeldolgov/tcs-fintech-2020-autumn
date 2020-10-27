package com.adeldolgov.feeder.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Attachment(
    @SerializedName("photo")
    val photo: Photo?
) : Parcelable