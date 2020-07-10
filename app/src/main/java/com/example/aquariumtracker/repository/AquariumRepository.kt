package com.example.aquariumtracker.repository

import androidx.lifecycle.LiveData
import com.example.aquariumtracker.database.dao.AquariumDAO
import com.example.aquariumtracker.database.models.Aquarium

class AquariumRepository(private val aquariumDAO: AquariumDAO) {

    val allAquariums: LiveData<List<Aquarium>> = aquariumDAO.getAquariumList()

    suspend fun insert(aq: Aquarium) {
        aquariumDAO.insert(aq)
    }
}