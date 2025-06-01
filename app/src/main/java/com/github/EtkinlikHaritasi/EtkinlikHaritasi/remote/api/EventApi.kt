package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import retrofit2.Response
import retrofit2.http.*

interface EventApi {

    @GET("events.json")
    suspend fun getAllEvents(): Response<Map<String, Event>>

    @GET("events/{id}.json")
    suspend fun getEventById(
        @Path("id") eventId: Int
    ): Response<Event>

    @POST("events.json")
    suspend fun createEvent(
        @Body event: Event
    ): Response<Event>

    @PUT("events/{id}.json")
    suspend fun updateEvent(
        @Path("id") eventId: Int,
        @Body event: Event
    ): Response<Event>

    @DELETE("events/{id}.json")
    suspend fun deleteEvent(
        @Path("id") eventId: Int
    ): Response<Unit>
}
