package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.auth.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.UserApi


class UserRepository {
    private val api = RetrofitInstance.retrofit.create(UserApi::class.java)

    suspend fun getUsers(token: String) = api.getAllUsers(token)

    suspend fun getUser(id: Int, token: String) = api.getUserById(id, token)

    suspend fun addUser(user: User, token: String) = api.createUser(user, token)

    suspend fun updateUser(id: Int, user: User, token: String) = api.updateUser(id, user, token)

    suspend fun deleteUser(id: Int, token: String) = api.deleteUser(id, token)
}