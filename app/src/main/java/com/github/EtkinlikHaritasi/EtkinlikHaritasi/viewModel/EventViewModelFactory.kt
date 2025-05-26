package com.github.EtkinlikHaritasi.EtkinlikHaritasi.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.EventRepository

class EventViewModelFactory(
    private val repository: EventRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventViewModel(repository) as T
    }
}
