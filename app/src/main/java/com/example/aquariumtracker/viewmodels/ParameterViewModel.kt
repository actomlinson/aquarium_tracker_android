package com.example.aquariumtracker.viewmodels

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
        repository = ParameterRepository(parameterDAO)
        allParams = repository.allParams
    }

    fun createDefaultParametersForAquarium(aqID: Int) {
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
                    units = paramDefaultUnits[i], param_id = i
                )
            }

            for (i in defaultParams) {
                Log.i("param name index", i.name)
            }
            repository.insertAll(defaultParams)
        }
    }

    fun getParametersForAquarium(aqID: Int) = repository.getParametersForAquarium(aqID)

    fun getParametersWithMeasurements(aqID: Int) = repository.getParameterWithMeasurements(aqID)

    fun insert(param: Parameter) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(param)
    }
}