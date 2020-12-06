package com.adeldolgov.feeder.data.mapper

import androidx.room.TypeConverter
import com.adeldolgov.feeder.util.PostType
import com.adeldolgov.feeder.util.PostTypes
import com.adeldolgov.feeder.util.toPostTypes
import java.util.*

class PostTypeConverter {

    companion object {
        private const val SEPARATOR = ","
    }

    @TypeConverter
    fun toPostType(value: String): PostTypes {
        return value.split(SEPARATOR).map { enumValueOf<PostType>(it) }.toPostTypes()
    }

    @TypeConverter
    fun fromPostType(value: EnumSet<PostType>): String =
        value.joinToString(separator = SEPARATOR) { it.name }

}