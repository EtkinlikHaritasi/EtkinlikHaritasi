package com.github.EtkinlikHaritasi.EtkinlikHaritasi.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(
    private val repository: EventRepository
) : ViewModel() {

    val allEvents: LiveData<List<Event>> = repository.allEvents

    fun refreshEvents() = viewModelScope.launch {
        repository.refreshEventsFromApi()
    }
}