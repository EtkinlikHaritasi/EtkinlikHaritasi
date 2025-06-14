package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val eventId: Int,
    val title: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val date: String,
    val time: String,
    val organizerId: Int
)
