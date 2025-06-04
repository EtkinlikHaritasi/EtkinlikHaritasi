package com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int ,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val email: String,
    //val password: String,
//    val joinedEvents: List<Event>,   //daha önceden katıldığı etkinlikler
 //   val notJoinedEvents: List<Event>  //daha önceden katıldığı ama henüz gerçekleştirilmemiş etkinlikler
)
