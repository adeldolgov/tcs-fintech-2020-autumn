package com.adeldolgov.feeder.data.mapper

import androidx.room.TypeConverter
import com.adeldolgov.feeder.ui.item.CareerItem
import com.google.gson.Gson

class CareerConverter {
    @TypeConverter
    fun fromCareer(career: Array<CareerItem>?): String? {
        return Gson().toJson(career, Array<CareerItem>::class.java)
    }

    @TypeConverter
    fun toCareer(career: String?): Array<CareerItem>? {
        return Gson().fromJson(career, Array<CareerItem>::class.java)
    }
}