package com.adeldolgov.feeder.data.mapper

import com.adeldolgov.feeder.data.database.entity.PostEntity
import com.adeldolgov.feeder.data.pojo.WallPost
import com.adeldolgov.feeder.util.PostType
import com.adeldolgov.feeder.util.PostTypes
import com.adeldolgov.feeder.util.extension.toBoolean


fun WallPost.toPostEntity(): PostEntity {
    return PostEntity(
        id = id,
        date = date,
        sourceId = ownerId,
        attachment = attachments,
        text = text,
        likesCount = likes.count,
        hasUserLike = likes.userLike,
        repostsCount = reposts.count,
        commentsCount = comments.count,
        postTypes = PostTypes.of(PostType.WALL_POST),
        canComment = comments.canComment.toBoolean()
    )
}
