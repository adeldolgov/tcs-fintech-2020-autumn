package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class UploadServer(
    
    @SerializedName("album_id")
    val albumId: Int,

    @SerializedName("upload_url")
    val uploadUrl: String,

    @SerializedName("user_id")
    val userId: Long
)