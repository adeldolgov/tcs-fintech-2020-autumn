package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class NewsFeed(
    @SerializedName("items")
    val items: List<Post>,
    @SerializedName("profiles")
    val profiles: List<Source>,
    @SerializedName("groups")
    val groups: List<Source>,
    @SerializedName("next_from")
    val nextFrom: String
)