package com.adeldolgov.feeder.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.adeldolgov.feeder.data.pojo.Attachment


@Entity(
    tableName = "comment",
    foreignKeys = [
        ForeignKey(
            entity = SourceEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("from_id"),
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("post_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)

class CommentEntity(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "post_id", index = true)
    val postId: Long,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(name = "from_id", index = true)
    val fromId: Long,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "attachments")
    val attachments: Array<Attachment>?,

    @ColumnInfo(name = "likes_count")
    val likesCount: Int
)
