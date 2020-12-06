package com.adeldolgov.feeder.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adeldolgov.feeder.data.database.entity.CommentEntity
import io.reactivex.Single

@Dao
interface CommentDao {

    @Query("SELECT * FROM comment WHERE post_id = :postId ORDER BY date ASC")
    fun getCommentsForPost(postId: Long): Single<List<CommentEntity>>

    @Query("SELECT * FROM comment WHERE id = :id")
    fun getCommentById(id: Long): Single<CommentEntity>

    @Query("DELETE FROM comment WHERE id = :id")
    fun deleteCommentById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg commentEntities: CommentEntity)

    @Query("DELETE FROM comment WHERE post_id = :postId")
    fun deleteDataFromTable(postId: Long)
}