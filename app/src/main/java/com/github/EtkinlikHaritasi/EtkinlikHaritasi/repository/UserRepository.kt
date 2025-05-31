package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.User
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.RetrofitInstance
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.remote.api.UserApi
import retrofit2.Response

class UserRepository {
    private val api = RetrofitInstance.retrofit.create(UserApi::class.java)

    suspend fun getUsersMap(): Response<Map<String, User>> {
        return api.getAllUsers()
    }

    suspend fun getAllUsersList(): List<User>? {
        val mapResp = api.getAllUsers()
        return if (mapResp.isSuccessful) {
            mapResp.body()?.values?.toList()
        } else {
            null
        }
    }

    suspend fun getUser(id: Int) = api.getUserById(id)

    suspend fun addUser(user: User) = api.createUser(user)

    suspend fun updateUser(id: Int, user: User) = api.updateUser(id, user)

    suspend fun deleteUser(id: Int) = api.deleteUser(id)

    // loginUser’u da “liste formatını” kullanan şekle çevirelim
    suspend fun loginUser(email: String, password: String): User? {
        val usersList = getAllUsersList() // direkt null veya List<User>
        return usersList?.find { it.email == email && it.password == password }
    }
}
