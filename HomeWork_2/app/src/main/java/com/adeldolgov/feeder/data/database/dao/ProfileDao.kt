package com.adeldolgov.feeder.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adeldolgov.feeder.data.database.entity.ProfileEntity
import io.reactivex.Single

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profile")
    fun getProfiles(): Single<List<ProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg profileEntities: ProfileEntity)

    @Query("DELETE FROM profile")
    fun deleteDataFromTable()
}