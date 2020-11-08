package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

data class NewsFeed(
    @SerializedName("items")
    val items: List<Post>,

    @SerializedName("profiles")
    val profiles: List<Profile>,

    @SerializedName("groups")
    val groups: List<Source>,

    @SerializedName("next_from")
    val nextFrom: String
)