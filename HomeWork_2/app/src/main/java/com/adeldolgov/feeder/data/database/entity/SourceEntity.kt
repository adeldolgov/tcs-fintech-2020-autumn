package com.adeldolgov.feeder.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "source")
class SourceEntity(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "photo_100")
    val photo100: String
)