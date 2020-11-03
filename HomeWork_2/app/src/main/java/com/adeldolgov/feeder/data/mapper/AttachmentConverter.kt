package com.adeldolgov.feeder.data.mapper

import androidx.room.TypeConverter
import com.adeldolgov.feeder.data.pojo.Attachment
import com.google.gson.Gson

class AttachmentConverter {
    @TypeConverter
    fun fromAttachments(attachments: Array<Attachment>?): String? {
        return Gson().toJson(attachments, Array<Attachment>::class.java)
    }

    @TypeConverter
    fun toAttachments(attachments: String?): Array<Attachment>? {
        return Gson().fromJson(attachments, Array<Attachment>::class.java)
    }
}