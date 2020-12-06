package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class Location(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String
)