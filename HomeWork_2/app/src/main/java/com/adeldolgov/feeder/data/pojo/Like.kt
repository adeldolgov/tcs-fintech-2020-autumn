package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class Like(
    @SerializedName("count")
    val count: Int,
    @SerializedName("user_likes")
    val userLike: Int
)