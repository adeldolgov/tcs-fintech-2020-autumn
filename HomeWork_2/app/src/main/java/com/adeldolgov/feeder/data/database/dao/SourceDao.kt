package com.adeldolgov.feeder.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adeldolgov.feeder.data.database.entity.SourceEntity
import io.reactivex.Single

@Dao
interface SourceDao {

    @Query("SELECT * FROM source")
    fun getSources(): Single<List<SourceEntity>>

    @Query("SELECT * FROM source WHERE id = :id")
    fun getSourceById(id: Long): Single<SourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg sourceEntities: SourceEntity)

    @Query("DELETE FROM source")
    fun deleteDataFromTable()
}