package com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class LoginRepository {

    suspend fun login(email: String, password: String): String? {
        return try {
            val result = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            val user = result.user
            user?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            Log.d("Login", e.toString())
            null
        }
    }
}
