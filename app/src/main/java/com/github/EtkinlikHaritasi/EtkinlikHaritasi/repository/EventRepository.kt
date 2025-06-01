package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import androidx.lifecycle.LiveData
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.dao.EventDao
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.EventApi
import retrofit2.Response

class EventRepository(private val eventDao: EventDao){
    private val api = RetrofitInstance.retrofit.create(EventApi::class.java)

    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    //room icin
    suspend fun refreshEventsFromApi() {
        val response = api.getAllEvents()
        if (response.isSuccessful) {
            response.body()?.values?.toList()?.let { eventsList ->
                eventDao.clearAll()
                eventDao.insertAll(eventsList)
            }
        }
    }

    suspend fun getEventsMap(): Response<Map<String, Event>> {
        return api.getAllEvents()
    }

    suspend fun getAllEventsList(): List<Event>? {
        val mapResp = api.getAllEvents()
        return if (mapResp.isSuccessful) {
            mapResp.body()?.values?.toList()
        } else {
            null
        }
    }

    suspend fun addEvent(event: Event) = api.createEvent(event)

    suspend fun updateEvent(id: Int, event: Event) = api.updateEvent(id, event)

    suspend fun deleteEvent(id: Int) = api.deleteEvent(id)
}
