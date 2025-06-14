package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import androidx.lifecycle.LiveData
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao.EventDao
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.auth.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.EventApi
import retrofit2.Response

class EventRepository(private val eventDao: EventDao) {
    private val api = RetrofitInstance.retrofit.create(EventApi::class.java)

    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    //room icin
    suspend fun refreshEventsFromApi(token: String) {
        val response = api.getAllEvents(token)
        if (response.isSuccessful) {
            response.body()?.let { eventMap ->
                val eventList = eventMap.values.toList()
                eventDao.clearAll()
                eventDao.insertAll(eventList)
            }
        }
    }
    // Local DB'den tüm etkinlikleri çeker
    suspend fun getLocalEvents(): List<Event> {
        return eventDao.getAllEventsList()
    }

    // Local DB'ye etkinlikleri ekler veya günceller
    suspend fun insertEvents(events: List<Event>) {
        eventDao.insertAll(events)
    }


    suspend fun getEvents(token: String): List<Event>? {
        return api.getAllEvents(token).body()?.values?.toList()
    }

    suspend fun getEvent(id: Int, token: String) = api.getEventById(id, token)

    suspend fun addEvent(event: Event, token: String) = api.createEvent(event, token = token)

    suspend fun updateEvent(id: Int, event: Event, token: String) =
        api.updateEvent(id, event, token)

    suspend fun deleteEvent(id: Int, token: String) = api.deleteEvent(id, token)

}