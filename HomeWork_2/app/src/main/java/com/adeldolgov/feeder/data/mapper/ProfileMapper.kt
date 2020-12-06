package com.adeldolgov.feeder.data.mapper

import com.adeldolgov.feeder.data.database.entity.ProfileEntity
import com.adeldolgov.feeder.data.pojo.Profile
import com.adeldolgov.feeder.ui.item.CareerItem
import com.adeldolgov.feeder.ui.item.ProfileItem

fun Profile.toProfileEntity(career: Array<CareerItem>): ProfileEntity =
    ProfileEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        birthdayDate = birthdayDate,
        domain = domain,
        country = country?.title,
        city = city?.title,
        photo400 = photo400,
        about = about,
        lastSeen = lastSeen.time,
        followersCount = followersCount,
        universityName = universityName,
        facultyName = facultyName,
        graduation = graduation,
        career = career
    )

fun ProfileEntity.toProfileItem(): ProfileItem =
    ProfileItem(
        id = id,
        firstName = firstName,
        lastName = lastName,
        birthdayDate = birthdayDate,
        domain = domain,
        country = country,
        city = city,
        photo = photo400,
        about = about,
        lastSeen = lastSeen,
        followersCount = followersCount,
        universityName = universityName,
        facultyName = facultyName,
        graduation = graduation,
        career = career.toList()
    )