package com.adeldolgov.feeder.ui.item

class ProfileItem(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val birthdayDate: String,
    val photo: String,
    val domain: String,
    val country: String?,
    val city: String?,
    val about: String,
    val lastSeen: Long,
    val followersCount: Int,
    val universityName: String,
    val facultyName: String,
    val graduation: Int,
    val career: List<CareerItem>
)