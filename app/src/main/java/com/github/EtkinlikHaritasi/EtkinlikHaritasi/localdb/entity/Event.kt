package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val eventId: Int,
    val title: String,
    val description: String,
    val location: String,
    val date: String,
    val time: String
)
