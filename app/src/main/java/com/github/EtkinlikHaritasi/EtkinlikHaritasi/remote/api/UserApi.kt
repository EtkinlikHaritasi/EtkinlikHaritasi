package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @GET("users.json")
    //suspend fun getAllUsers(): Response<List<User>>
    suspend fun getAllUsers(@Query("auth") token: String): Response<List<User>>

//    @GET("users/{id}")
//    suspend fun getUserById(
//        @Path("id") userId: Int
//    ): Response<User>
    @GET("users/{id}.json")
    suspend fun getUserById(
        @Path("id") userId: Int,
        @Query("auth") token: String
    ): Response<User>

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

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: User,
        @Query("auth") token: String
    ): Response<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") userId: Int,
        @Query("auth") token: String
    ): Response<Unit>
}


