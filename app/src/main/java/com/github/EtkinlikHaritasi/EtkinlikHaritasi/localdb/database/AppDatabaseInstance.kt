package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database

import android.content.Context
import androidx.room.Room

object AppDatabaseInstance {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "etkinlik_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
