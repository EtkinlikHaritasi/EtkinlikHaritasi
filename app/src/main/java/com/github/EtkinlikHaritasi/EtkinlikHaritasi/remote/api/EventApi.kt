package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import retrofit2.Response
import retrofit2.http.*

interface EventApi {

//    @GET("events")
//    suspend fun getAllEvents(): Response<List<Event>>
    @GET("events.json")
   // suspend fun getAllEvents(@Query("auth") token: String): Response<List<Event>>
    suspend fun getAllEvents(@Query("auth") token: String): Response<Map<String, Event>>


    @GET("events/{id}.json")
    suspend fun getEventById(
        @Path("id") eventId: Int,
        @Query("auth") token: String
    ): Response<Event>

    @PUT("events/{id}.json")
    suspend fun createEvent(
        @Body event: Event,
        @Path("id") eventId: Int = event.eventId,
        @Query("auth") token: String,
    ): Response<Event>

    @PATCH("events/{id}.json")
    suspend fun updateEvent(
        @Path("id") eventId: Int,
        @Body event: Event,
        @Query("auth") token: String
    ): Response<Event>

    @DELETE("events/{id}.json")
    suspend fun deleteEvent(
        @Path("id") eventId: Int,
        @Query("auth") token: String
    ): Response<Unit>
}
