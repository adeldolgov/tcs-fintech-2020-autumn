package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class Source(
    @SerializedName("id")
    val id: Long,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("photo_100")
    val photo100: String
)