package com.adeldolgov.feeder.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.adeldolgov.feeder.data.pojo.Career
import com.adeldolgov.feeder.ui.item.CareerItem

@Entity(tableName = "profile")
class ProfileEntity(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "birthday_date")
    val birthdayDate: String,

    @ColumnInfo(name = "domain")
    val domain: String,

    @ColumnInfo(name = "country")
    val country: String?,

    @ColumnInfo(name = "city")
    val city: String?,

    @ColumnInfo(name = "photo_400_orig")
    val photo400: String,

    @ColumnInfo(name = "about")
    val about: String,

    @ColumnInfo(name = "last_seen")
    val lastSeen: Long,

    @ColumnInfo(name = "followers_count")
    val followersCount: Int,

    @ColumnInfo(name = "university_name")
    val universityName: String,

    @ColumnInfo(name = "faculty_name")
    val facultyName: String,

    @ColumnInfo(name = "graduation")
    val graduation: Int,

    @TypeConverters(Career::class)
    @ColumnInfo(name = "career")
    val career: Array<CareerItem>
)