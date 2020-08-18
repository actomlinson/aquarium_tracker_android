package com.example.aquariumtracker.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.aquariumtracker.database.AppDatabase
import com.example.aquariumtracker.database.model.Parameter
import com.example.aquariumtracker.repository.ParameterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParameterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ParameterRepository

    val allParams: LiveData<List<Parameter>>

    init {
        val parameterDAO = AppDatabase.getDatabase(application, viewModelScope).parameterDao()
        repository = ParameterRepository(application, parameterDAO)
        allParams = repository.allParams
    }

    fun createDefaultParametersForAquarium(aqID: Long) {
        viewModelScope.launch {
            val paramDefaultNames = listOf<String>(
                "Nitrate", "Nitrite", "Total Hardness (GH)",
                "Chlorine", "Total Alkalinity (KH)", "pH"
            )
            val paramDefaultUnits = listOf<String>(
                "(mg / L)", "(mg / L)", "(mg / L)", "(mg / L)",
                "(mg / L)", ""
            )
            val defaultParams = (paramDefaultNames.indices).map { i ->
                Parameter( p_order = i, aq_id = aqID, name = paramDefaultNames[i],
                    units = paramDefaultUnits[i], param_id = 0
                )
            }

            for (i in defaultParams) {
                Log.i("ParameterViewModel", i.name)
            }
            repository.insertAll(defaultParams)
        }
    }

    fun getParametersForAquarium(aqID: Long) = repository.getParametersForAquarium(aqID)

    fun getParametersWithMeasurements(aqID: Long) = repository.getParameterWithMeasurements(aqID)

    fun insert(param: Parameter) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(param)
    }
}