package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao

import androidx.room.Dao
import androidx.room.Insert
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Participation

@Dao
interface ParticipationDao {
    @Insert
    suspend fun insert(participation: Participation)

    // TODO: query by userId & eventId
}