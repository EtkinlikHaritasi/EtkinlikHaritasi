package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import retrofit2.Response
import retrofit2.http.*

interface EventApi {

    @GET("events.json")
    suspend fun getAllEvents(): Response<List<Event>>

    @GET("events/{id}")
    suspend fun getEventById(
        @Path("id") eventId: Int
    ): Response<Event>

    @POST("events")
    suspend fun createEvent(
        @Body event: Event
    ): Response<Event>

    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") eventId: Int,
        @Body event: Event
    ): Response<Event>

    @DELETE("events/{id}")
    suspend fun deleteEvent(
        @Path("id") eventId: Int
    ): Response<Unit>
}
