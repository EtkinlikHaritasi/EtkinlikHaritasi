package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event

@Dao
interface EventDao {
//    @Insert
//    suspend fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<Event>)

    @Query("SELECT * FROM event_table ORDER BY eventId ASC")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM event_table")
    suspend fun getAllEventsList(): List<Event>


    @Query("DELETE FROM event_table")
    suspend fun clearAll()




}