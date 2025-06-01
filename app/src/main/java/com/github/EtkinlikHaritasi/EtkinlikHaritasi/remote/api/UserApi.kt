package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @GET("users.json")
    suspend fun getAllUsers(): Response<Map<String, User>>

    @GET("users/{id}.json")
    suspend fun getUserById(
        @Path("id") userId: Int
    ): Response<User>

    @POST("users.json")
    suspend fun createUser(
        @Body user: User
    ): Response<User>

    @PUT("users/{id}.json")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: User
    ): Response<User>

    @DELETE("users/{id}.json")
    suspend fun deleteUser(
        @Path("id") userId: Int
    ): Response<Unit>
}
