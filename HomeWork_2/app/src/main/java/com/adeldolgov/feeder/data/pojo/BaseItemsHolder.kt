package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

open class BaseItemsHolder<T : BasePost>(
    @SerializedName("items")
    val items: List<T>,

    @SerializedName("profiles")
    val profiles: List<Profile>,

    @SerializedName("groups")
    val groups: List<Source>
)