package com.example.aquariumtracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.aquariumtracker.database.AppDatabase
import com.example.aquariumtracker.database.model.Measurement
import com.example.aquariumtracker.database.model.MeasurementSet
import com.example.aquariumtracker.repository.MeasurementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MeasurementViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MeasurementRepository

    init {
        val measurementDAO = AppDatabase.getDatabase(application, viewModelScope).measurementDao()
        repository = MeasurementRepository(measurementDAO)
    }

    fun getAllMeasurementsForParameter(pID: Int) =
        repository.getAllMeasurementsForParameter(pID)

    fun getMostRecentMeasurementsForParameter(n: Int, pID: Int) =
        repository.getMostRecentMeasurementsForParameter(n, pID)

    fun insertAll(ms: List<Measurement>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAll(ms)
    }

    fun insert(m: Measurement) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(m)
    }

    suspend fun insert(mset: MeasurementSet): Long = repository.insert(mset)


    fun getMeasurementTimes(): LiveData<Long> = repository.getMeasurementTimes()
    fun getMeasurementsAtTime(time: Long): LiveData<List<Measurement>> =
        repository.getMeasurementsAtTime(time)
}