package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class Comments(
    @SerializedName("count")
    val count: Int,
    @SerializedName("can_post")
    val canComment: Int
)