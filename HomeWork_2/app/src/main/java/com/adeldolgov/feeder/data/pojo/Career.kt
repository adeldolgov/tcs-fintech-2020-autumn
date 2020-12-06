package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class Career(
    @SerializedName("country_id")
    val countryId: Int?,

    @SerializedName("city_id")
    val cityId: Int?,

    @SerializedName("company")
    val company: String?,

    @SerializedName("from")
    val from: Int?,

    @SerializedName("until")
    val until: Int?,

    @SerializedName("group_id")
    val groupId: Long?,

    @SerializedName("position")
    val position: String?
)