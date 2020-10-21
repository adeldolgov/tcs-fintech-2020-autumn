package com.adeldolgov.homework_2.data.repository

import com.adeldolgov.homework_2.api.DataGenerator
import com.adeldolgov.homework_2.data.pojo.Post

class PostRepository {
    fun getPosts(): List<Post> {
        return DataGenerator().getPosts()
    }
}