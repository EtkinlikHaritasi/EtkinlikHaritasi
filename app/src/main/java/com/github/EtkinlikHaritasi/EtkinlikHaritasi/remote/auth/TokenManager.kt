package com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.auth

object TokenManager {
    private var token: String? = null

    fun setToken(value: String) {
        token = value
    }

    fun getToken(): String? {
        return token
    }
}