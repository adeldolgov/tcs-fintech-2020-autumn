package com.adeldolgov.feeder.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adeldolgov.feeder.data.database.entity.PostEntity
import io.reactivex.Single

@Dao
interface PostDao {

    @Query("SELECT * FROM post ORDER BY date DESC LIMIT :limit OFFSET :offset")
    fun getPosts(limit: Int, offset: Int): Single<List<PostEntity>>

    @Query("SELECT * FROM post WHERE id = :id")
    fun getPostById(id: Long): Single<PostEntity>

    @Query("UPDATE post SET has_user_like = :hasUserLike, likes_count = :likesCount WHERE id = :id")
    fun updatePostLike(id: Long, hasUserLike: Boolean, likesCount: Int): Int

    @Query("DELETE FROM post WHERE id = :id")
    fun deletePostById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg postEntities: PostEntity)

    @Query("DELETE FROM post")
    fun deleteDataFromTable()
}