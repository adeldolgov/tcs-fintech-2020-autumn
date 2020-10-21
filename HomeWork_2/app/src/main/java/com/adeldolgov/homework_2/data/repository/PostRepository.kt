package com.adeldolgov.homework_2.data.repository

import com.adeldolgov.homework_2.api.DataGenerator
import com.adeldolgov.homework_2.data.pojo.Post
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

class PostRepository {
    fun getPosts(): Single<List<Post>> {
        return Single.fromCallable {
            when {
                Random.nextInt(10) == 0 -> {
                    throw RuntimeException("Вам выпала редкая легендарная ошибка.")
                }
                Random.nextInt(5) == 0 -> {
                    DataGenerator().getPosts().blockingGet().toMutableList().apply { clear() }
                }
                else -> {
                    DataGenerator().getPosts().blockingGet() //чтобы сгенерировать ошибку
                }
            }
        }.subscribeOn(Schedulers.io())
    }
}