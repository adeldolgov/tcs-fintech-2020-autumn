package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

data class PostComments(
    @SerializedName("items")
    val items: List<Comment>,

    @SerializedName("profiles")
    val profiles: List<Profile>,

    @SerializedName("groups")
    val groups: List<Source>,

    @SerializedName("count")
    val count: Int
)