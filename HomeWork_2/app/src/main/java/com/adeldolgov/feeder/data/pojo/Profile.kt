package com.adeldolgov.feeder.data.pojo

import com.google.gson.annotations.SerializedName

class Profile(
    @SerializedName("id")
    val id: Long,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("bdate")
    val birthdayDate: String,

    @SerializedName("photo_100")
    val photo100: String,

    @SerializedName("domain")
    val domain: String,

    @SerializedName("country")
    val country: Location?,

    @SerializedName("city")
    val city: Location?,

    @SerializedName("photo_400_orig")
    val photo400: String,

    @SerializedName("about")
    val about: String,

    @SerializedName("last_seen")
    val lastSeen: LastSeen,

    @SerializedName("followers_count")
    val followersCount: Int,

    @SerializedName("university")
    val university: Int,

    @SerializedName("university_name")
    val universityName: String,

    @SerializedName("faculty")
    val faculty: Int,

    @SerializedName("faculty_name")
    val facultyName: String,

    @SerializedName("graduation")
    val graduation: Int,

    @SerializedName("career")
    val career: Array<Career>
)