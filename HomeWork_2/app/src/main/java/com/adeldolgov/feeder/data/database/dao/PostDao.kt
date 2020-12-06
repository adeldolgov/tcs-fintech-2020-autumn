package com.adeldolgov.feeder.data.database.dao

import android.util.Log
import androidx.room.*
import com.adeldolgov.feeder.data.database.entity.PostEntity
import com.adeldolgov.feeder.util.PostTypes
import com.adeldolgov.feeder.util.toPostTypes
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PostDao {

    @Query("SELECT * FROM post WHERE post_types LIKE '%' || :postTypes || '%' ORDER BY date DESC")
    fun getPosts(postTypes: PostTypes): Single<List<PostEntity>>

    @Query("SELECT * FROM post WHERE post_types LIKE '%' || :postTypes || '%' ORDER BY date DESC")
    fun getPostsFlowable(postTypes: PostTypes): Flowable<List<PostEntity>>

    @Query("SELECT * FROM post WHERE id = :id")
    fun getPostById(id: Long): Single<PostEntity>

    @Query("SELECT * FROM post WHERE source_id = :id")
    fun getPostsByOwnerId(id: Long): Single<List<PostEntity>>

    @Query("UPDATE post SET has_user_like = :hasUserLike, likes_count = :likesCount WHERE id = :id")
    fun updatePostLike(id: Long, hasUserLike: Boolean, likesCount: Int): Int

    @Query("SELECT * FROM post WHERE id = :id")
    fun checkIfPostExists(id: Long): PostEntity?

    @Query("DELETE FROM post WHERE id = :id")
    fun deletePostById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg postEntities: PostEntity)

    @Update
    fun updateAll(vararg postEntities: PostEntity)

    @Transaction
    fun insertAllWithPostTypeAddition(vararg postEntities: PostEntity) {
        postEntities.forEach { postEntity ->
            checkIfPostExists(postEntity.id)?.let {
                postEntity.postTypes = postEntity.postTypes.plus(it.postTypes).toPostTypes()
            }
        }
        insertAll(*postEntities)
    }

    @Transaction
    fun deleteDataFromTableByTypesOrRemoveTypes(postTypes: PostTypes) {
        Log.d(PostDao::class.java.name, postTypes.joinToString { it.name })
        getPosts(postTypes).blockingGet().forEach {
            it.postTypes = it.postTypes.minus(postTypes).toPostTypes()
            if (it.postTypes.isEmpty()) deletePostById(it.id)
            else updateAll(it)
        }
    }
}