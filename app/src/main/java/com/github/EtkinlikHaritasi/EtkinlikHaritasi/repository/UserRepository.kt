package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.auth.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.UserApi
import retrofit2.Response


class UserRepository {
    private val api = RetrofitInstance.retrofit.create(UserApi::class.java)

    suspend fun getUsers(token: String) = api.getAllUsers(token)

    suspend fun getUser(id: Int, token: String) = api.getUserById(id, token)

    suspend fun getUser(email: String, token: String): Response<User> {
        var a = email.replace("-","--").replace(".","-")
        return api.getUser(a, token)
    }

    suspend fun addUser(user: User, token: String): Response<User> {
        var a = user.email.replace("-","--").replace(".","-")
        return api.createUser(user, a, token)
    }

    suspend fun updateUser(id: Int, user: User, token: String) = api.updateUser(id, user, token)

    suspend fun deleteUser(id: Int, token: String) = api.deleteUser(id, token)
}