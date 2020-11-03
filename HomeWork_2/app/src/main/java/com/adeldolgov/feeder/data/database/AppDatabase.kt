package com.adeldolgov.feeder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.adeldolgov.feeder.data.database.dao.PostDao
import com.adeldolgov.feeder.data.database.dao.SourceDao
import com.adeldolgov.feeder.data.database.entity.PostEntity
import com.adeldolgov.feeder.data.database.entity.SourceEntity
import com.adeldolgov.feeder.data.mapper.AttachmentConverter

@Database(
    entities = [
        PostEntity::class,
        SourceEntity::class
    ], version = 1
)
@TypeConverters(AttachmentConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
    abstract fun postDao(): PostDao
}