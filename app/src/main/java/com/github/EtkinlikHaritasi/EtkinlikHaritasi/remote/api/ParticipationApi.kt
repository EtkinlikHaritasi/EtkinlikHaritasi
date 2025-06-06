package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Participation
import retrofit2.Response
import retrofit2.http.*

interface ParticipationApi {

    @GET("participations.json")
    suspend fun getAllParticipations(@Query("auth") token: String): Response<Map<String, Participation>>

    @GET("participations.json?orderBy=\"userId\"")
    suspend fun getPartiticipationsByUser(
        @Query("equalTo") userId: Int,
        @Query("auth") token: String
    ): Response<Map<String, Participation>>

    @GET("participations.json?orderBy=\"eventId\"")
    suspend fun getPartiticipationsByEvent(
        @Query("equalTo") eventId: Int,
        @Query("auth") token: String
    ): Response<Map<String, Participation>>

    @GET("participations/{user}_{event}.json")
    suspend fun getParticipation(
        @Path("user") userId: Int,
        @Path("event") eventId: Int,
        @Query("auth") token: String
    ): Response<Participation>

    @PUT("participations/{user_event}.json")
    suspend fun createParticipation(
        @Body participation: Participation,
        @Path("user_event") user_event: String = "${participation.userId}_${participation.eventId}",
        @Query("auth") token: String
    ): Response<Participation>

    @PATCH("participations/{userId}_{eventId}.json")
    suspend fun updateParticipation(
        @Path("userId") userId: Int,
        @Path("eventId") eventId: Int,
        @Body participation: Participation,
        @Query("auth") token: String
    ): Response<Participation>

    @DELETE("participations/{userId}_{eventId}.json")
    suspend fun deleteParticipation(
        @Path("userId") userId: Int,
        @Path("eventId") eventId: Int,
        @Query("auth") token: String
    ): Response<Unit>
}
