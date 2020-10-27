package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class ApiResponse<T>(
    @SerializedName("response")
    val response: T,
    val error: Error?
)