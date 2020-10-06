package com.adeldolgov.homework_2.data.repository

import com.adeldolgov.homework_2.api.DataGenerator
import com.adeldolgov.homework_2.data.pojo.Post

object PostRepository {
    fun getPosts(): Array<Post> {
        return DataGenerator().getPosts()
    }
}