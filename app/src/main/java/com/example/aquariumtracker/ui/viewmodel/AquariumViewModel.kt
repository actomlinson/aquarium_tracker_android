package com.example.aquariumtracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.aquariumtracker.database.AppDatabase
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.repository.AquariumRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AquariumViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AquariumRepository

    val allAquariums: LiveData<List<Aquarium>>

    init {
        val aquariumDAO = AppDatabase.getDatabase(application, viewModelScope).aquariumDao()
        repository = AquariumRepository(aquariumDAO)
        allAquariums = repository.allAquariums
    }

    fun getAquariumsFromNetwork() {
        viewModelScope.launch {
            repository.getAquariumsFromNetwork()
        }
    }

    suspend fun insert(aq: Aquarium): Long {
        return withContext(viewModelScope.coroutineContext) {
            repository.insert(aq)
        }
    }

    fun deleteAquarium(aqID: Long) {
        viewModelScope.launch {
            repository.deleteAquarium(aqID)
        }
    }
}
