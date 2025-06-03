package com.github.EtkinlikHaritasi.EtkinlikHaritasi.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.RegisterRepository

class RegisterViewModel(
    private val repository: RegisterRepository
) : ViewModel() {

    val registrationSuccess = mutableStateOf<Boolean?>(null)

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.register(email, password)
            registrationSuccess.value = result
        }
    }
}
