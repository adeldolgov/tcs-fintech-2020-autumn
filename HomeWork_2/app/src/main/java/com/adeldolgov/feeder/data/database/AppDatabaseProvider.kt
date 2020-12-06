package com.adeldolgov.feeder.data.database

import android.content.Context
import androidx.room.Room

class AppDatabaseProvider(applicationContext: Context) {

    companion object {
        private const val APP_DATABASE_NAME = "app-database"
    }

    val db = initAppDatabase(applicationContext)

    private fun initAppDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            APP_DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }
}