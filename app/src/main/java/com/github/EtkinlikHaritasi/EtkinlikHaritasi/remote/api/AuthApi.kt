package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api

import LoginResponse
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.auth.models.LoginRequest
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>


}
