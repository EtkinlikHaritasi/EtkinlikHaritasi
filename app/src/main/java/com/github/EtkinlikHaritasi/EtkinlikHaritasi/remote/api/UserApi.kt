package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @GET("users.json")
    //suspend fun getAllUsers(): Response<List<User>>
    suspend fun getAllUsers(@Query("auth") token: String): Response<List<User>>


    @GET("users.json/?orderBy=\"id\"")
    suspend fun getUserById(
        @Query("equalTo") userId: Int,
        @Query("auth") token: String
    ): Response<Map<String, User>>

    @GET("users/{email}.json")
    suspend fun getUser(
        @Path("email") email: String,
        @Query("auth") token: String
    ): Response<User>

    @PUT("users/{email}.json")
    suspend fun createUser(
        @Body user: User,
        @Path("email") email: String,
        @Query("auth") token: String
    ): Response<User>

    @PATCH("users/{email}.json")
    suspend fun updateUser(
        @Path("email") email: String,
        @Body user: User,
        @Query("auth") token: String
    ): Response<User>

    @DELETE("users/{email}.json")
    suspend fun deleteUser(
        @Path("email") userId: String,
        @Query("auth") token: String
    ): Response<Unit>
}


