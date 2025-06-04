package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.auth

// remote/RetrofitInstance.kt

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object RetrofitInstance {
    private const val BASE_URL = //"https://etkinlikharitasi-9596a-default-rtdb.firebaseio.com/" //FİER BASE URL
        "https://etkinlikharitasi-yzm304-default-rtdb.europe-west1.firebasedatabase.app/"

//    private val client = OkHttpClient.Builder()
//        .addInterceptor(AuthInterceptor())
//        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
