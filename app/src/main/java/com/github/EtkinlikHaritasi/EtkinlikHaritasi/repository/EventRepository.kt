package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.EventApi

class EventRepository {
    private val api = RetrofitInstance.retrofit.create(EventApi::class.java)

    suspend fun getEvents() = api.getAllEvents()

    suspend fun getEvent(id: Int) = api.getEventById(id)

    suspend fun addEvent(event: Event) = api.createEvent(event)

    suspend fun updateEvent(id: Int, event: Event) = api.updateEvent(id, event)

    suspend fun deleteEvent(id: Int) = api.deleteEvent(id)
}
