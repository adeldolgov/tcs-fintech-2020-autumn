package com.adeldolgov.feeder.data.database.entity

import androidx.room.*
import com.adeldolgov.feeder.data.mapper.AttachmentConverter
import com.adeldolgov.feeder.data.pojo.Attachment
import com.adeldolgov.feeder.util.PostType
import java.util.*

@Entity(
    tableName = "post",
    foreignKeys = [ForeignKey(
        entity = SourceEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("source_id"),
        onUpdate = ForeignKey.CASCADE
    )]
)

class PostEntity(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(name = "source_id", index = true)
    val sourceId: Long,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "attachments")
    @TypeConverters(AttachmentConverter::class)
    val attachment: Array<Attachment>?,

    @ColumnInfo(name = "likes_count")
    val likesCount: Int,

    @ColumnInfo(name = "has_user_like")
    val hasUserLike: Int,

    @ColumnInfo(name = "reposts_count")
    val repostsCount: Int,

    @ColumnInfo(name = "comments_count")
    val commentsCount: Int,

    @ColumnInfo(name = "post_types")
    var postTypes: EnumSet<PostType>,

    @ColumnInfo(name = "canComment")
    val canComment: Boolean
)
