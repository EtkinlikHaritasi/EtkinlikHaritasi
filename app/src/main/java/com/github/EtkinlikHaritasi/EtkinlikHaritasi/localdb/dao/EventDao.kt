package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: Event)

    // TODO: implement full CRUD
}