package com.example.aquariumtracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.aquariumtracker.database.AquariumRoomDatabase
import com.example.aquariumtracker.database.models.Aquarium
import com.example.aquariumtracker.repository.AquariumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AquariumViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AquariumRepository

    val allAquariums: LiveData<List<Aquarium>>

    init {
        val aquariumDAO = AquariumRoomDatabase.getDatabase(application, viewModelScope).aquariumDao()
        repository = AquariumRepository(aquariumDAO)
        allAquariums = repository.allAquariums
    }

    fun insert(aq: Aquarium) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(aq)
    }

}
