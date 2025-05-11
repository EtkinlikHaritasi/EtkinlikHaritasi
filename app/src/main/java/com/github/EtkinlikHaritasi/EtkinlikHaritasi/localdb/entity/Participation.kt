package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity


import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "participation_table",
    primaryKeys = ["userId", "eventId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Event::class,
            parentColumns = ["eventId"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Participation(
    val userId: Int,
    val eventId: Int,
    val joinedAt: String
)
