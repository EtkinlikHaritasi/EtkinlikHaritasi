package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote

// remote/RetrofitInstance.kt

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://etkinlikharitasi-9596a-default-rtdb.firebaseio.com/" //FİER BASE URL

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
