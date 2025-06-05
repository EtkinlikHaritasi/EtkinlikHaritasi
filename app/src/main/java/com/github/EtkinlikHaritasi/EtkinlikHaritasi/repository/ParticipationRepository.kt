package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Participation
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.auth.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.ParticipationApi

class ParticipationRepository {
    private val api = RetrofitInstance.retrofit.create(ParticipationApi::class.java)

    suspend fun getParticipations(token: String) = api.getAllParticipations(token)

    //suspend fun getParticipation(userId: Int, eventId: Int) = api.getParticipation(userId, eventId)
    suspend fun getParticipationsByUser(userId: Int, token: String): List<Participation> {
        return api.getPartiticipationsByUser(userId, token).body()?.values?.toList().orEmpty()
    }
    suspend fun getParticipationsByEvent(eventId: Int, token: String): List<Participation> {
        return api.getPartiticipationsByEvent(eventId, token).body()?.values?.toList().orEmpty()
    }

    suspend fun addParticipation(participation: Participation, token: String) =
        api.createParticipation(participation, token = token)

    suspend fun updateParticipation(userId: Int, eventId: Int, participation: Participation,
                                    token: String) =
        api.updateParticipation(userId, eventId, participation, token)

    suspend fun deleteParticipation(userId: Int, eventId: Int, token: String) =
        api.deleteParticipation(userId, eventId, token)
}
