package com.adeldolgov.feeder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.adeldolgov.feeder.data.database.dao.CommentDao
import com.adeldolgov.feeder.data.database.dao.PostDao
import com.adeldolgov.feeder.data.database.dao.ProfileDao
import com.adeldolgov.feeder.data.database.dao.SourceDao
import com.adeldolgov.feeder.data.database.entity.CommentEntity
import com.adeldolgov.feeder.data.database.entity.PostEntity
import com.adeldolgov.feeder.data.database.entity.ProfileEntity
import com.adeldolgov.feeder.data.database.entity.SourceEntity
import com.adeldolgov.feeder.data.mapper.AttachmentConverter
import com.adeldolgov.feeder.data.mapper.CareerConverter
import com.adeldolgov.feeder.data.mapper.PostTypeConverter

@Database(
    entities = [
        PostEntity::class,
        SourceEntity::class,
        CommentEntity::class,
        ProfileEntity::class
    ], version = 3,
    exportSchema = false
)
@TypeConverters(AttachmentConverter::class, CareerConverter::class, PostTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
    abstract fun profileDao(): ProfileDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
}