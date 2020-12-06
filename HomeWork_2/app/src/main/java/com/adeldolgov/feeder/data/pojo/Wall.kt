package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class Wall(

    items: List<WallPost>,

    profiles: List<Profile>,

    groups: List<Source>,

    @SerializedName("count")
    val count: Int
) : BaseItemsHolder<WallPost>(items, profiles, groups)