package com.adeldolgov.homework_2.data.pojo

import com.adeldolgov.homework_2.data.item.PostItem

class Post(
    val id: Long,
    val date: Long,
    val isFavorite: Boolean,
    val sourceId: Long,
    val text: String,
    val attachments: Array<Attachment>?,
    val likes: Count,
    val reposts: Count,
    val comments: Count
)

fun Post.toPostItem(source: Source) = PostItem(
    id = id,
    sourceName = source.name,
    sourceImage = source.photo100,
    date = date,
    isFavorite = isFavorite,
    text = text,
    attachments =  attachments,
    likes = likes.count,
    reposts = reposts.count,
    comments = comments.count
)