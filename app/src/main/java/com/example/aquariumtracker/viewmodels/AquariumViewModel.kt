package com.example.aquariumtracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.aquariumtracker.database.AppDatabase
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.repository.AquariumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AquariumViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AquariumRepository

    val allAquariums: LiveData<List<Aquarium>>

    init {
        val aquariumDAO = AppDatabase.getDatabase(application, viewModelScope).aquariumDao()
        repository = AquariumRepository(aquariumDAO)
        allAquariums = repository.allAquariums
    }

    fun insert(aq: Aquarium) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(aq)
    }

}
