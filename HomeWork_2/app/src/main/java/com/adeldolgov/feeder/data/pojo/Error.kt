package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class Error(
    @SerializedName("error_code")
    val errorCode: Int,

    @SerializedName("error_msg")
    val errorMsg: String
)