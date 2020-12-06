package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class LastSeen(
    @SerializedName("platform")
    val platform: Int,

    @SerializedName("time")
    val time: Long
)