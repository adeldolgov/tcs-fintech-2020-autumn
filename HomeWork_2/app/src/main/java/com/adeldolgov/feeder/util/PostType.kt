package com.adeldolgov.feeder.util

import java.util.*

enum class PostType {
    NEWS_FEED_POST, WALL_POST
}

typealias PostTypes = EnumSet<PostType>

fun List<PostType>.toPostTypes(): PostTypes {
    val temp = PostTypes.noneOf(PostType::class.java)
    Collections.addAll(temp, *this.toTypedArray())
    return temp
}

fun Set<PostType>.toPostTypes(): PostTypes {
    val temp = PostTypes.noneOf(PostType::class.java)
    Collections.addAll(temp, *this.toTypedArray())
    return temp
}