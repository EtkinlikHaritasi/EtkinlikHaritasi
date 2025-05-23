package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Participation
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.ParticipationApi

class ParticipationRepository {
    private val api = RetrofitInstance.retrofit.create(ParticipationApi::class.java)

    suspend fun getParticipations() = api.getAllParticipations()

    suspend fun getParticipation(userId: Int, eventId: Int) = api.getParticipation(userId, eventId)

    suspend fun addParticipation(participation: Participation) = api.createParticipation(participation)

    suspend fun updateParticipation(userId: Int, eventId: Int, participation: Participation) =
        api.updateParticipation(userId, eventId, participation)

    suspend fun deleteParticipation(userId: Int, eventId: Int) =
        api.deleteParticipation(userId, eventId)
}
