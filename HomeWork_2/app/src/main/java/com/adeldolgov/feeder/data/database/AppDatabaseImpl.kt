package com.adeldolgov.feeder.data.database

import android.content.Context
import androidx.room.Room
import com.adeldolgov.feeder.FeederApp

class AppDatabaseImpl {

    companion object {
        private const val APP_DATABASE_NAME = "app-database"
    }

    val db = initAppDatabase(FeederApp.applicationContext())

    private fun initAppDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            APP_DATABASE_NAME
        ).build()
    }
}