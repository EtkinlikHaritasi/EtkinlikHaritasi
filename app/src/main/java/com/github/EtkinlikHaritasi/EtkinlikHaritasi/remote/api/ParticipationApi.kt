package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Participation
import retrofit2.Response
import retrofit2.http.*

interface ParticipationApi {

    @GET("participations")
    suspend fun getAllParticipations(): Response<List<Participation>>

    @GET("participations/{userId}/{eventId}")
    suspend fun getParticipation(
        @Path("userId") userId: Int,
        @Path("eventId") eventId: Int
    ): Response<Participation>

    @POST("participations")
    suspend fun createParticipation(
        @Body participation: Participation
    ): Response<Participation>

    @PUT("participations/{userId}/{eventId}")
    suspend fun updateParticipation(
        @Path("userId") userId: Int,
        @Path("eventId") eventId: Int,
        @Body participation: Participation
    ): Response<Participation>

    @DELETE("participations/{userId}/{eventId}")
    suspend fun deleteParticipation(
        @Path("userId") userId: Int,
        @Path("eventId") eventId: Int
    ): Response<Unit>
}
