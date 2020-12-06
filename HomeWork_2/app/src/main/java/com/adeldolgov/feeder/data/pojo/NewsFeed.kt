package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class NewsFeed(
    items: List<NewsFeedPost>,

    profiles: List<Profile>,

    groups: List<Source>,

    @SerializedName("next_from")
    val nextFrom: String

) : BaseItemsHolder<NewsFeedPost>(items, profiles, groups)