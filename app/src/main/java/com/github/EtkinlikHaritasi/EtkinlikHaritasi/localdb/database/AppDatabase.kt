package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao.UserDao
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao.EventDao
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao.ParticipationDao
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Participation

@Database(
    entities = [User::class, Event::class, Participation::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun participationDao(): ParticipationDao

}

