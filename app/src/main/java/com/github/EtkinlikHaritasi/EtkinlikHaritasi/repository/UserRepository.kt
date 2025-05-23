package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.UserApi

class UserRepository {
    private val api = RetrofitInstance.retrofit.create(UserApi::class.java)

    suspend fun getUsers() = api.getAllUsers()

    suspend fun getUser(id: Int) = api.getUserById(id)

    suspend fun addUser(user: User) = api.createUser(user)

    suspend fun updateUser(id: Int, user: User) = api.updateUser(id, user)

    suspend fun deleteUser(id: Int) = api.deleteUser(id)
}
