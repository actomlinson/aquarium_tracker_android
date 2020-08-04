package com.example.aquariumtracker.repository

import androidx.lifecycle.LiveData
import com.example.aquariumtracker.database.dao.ParameterDAO
import com.example.aquariumtracker.database.model.Parameter
import com.example.aquariumtracker.database.model.ParameterWithMeasurements

class ParameterRepository(private val parameterDAO: ParameterDAO) {

    fun getParametersForAquarium(aqID: Long) = parameterDAO.getParametersForAquarium(aqID)

    val aq0Params: LiveData<List<Parameter>> = parameterDAO.getParametersForAquarium(0)

    val allParams: LiveData<List<Parameter>> = parameterDAO.getAllParametersList()

    fun getParameterWithMeasurements(aqID: Long): LiveData<List<ParameterWithMeasurements>>
            = parameterDAO.getParameterWithMeasurements(aqID)

    suspend fun insert(param: Parameter) {
        parameterDAO.insert(param)
    }

    suspend fun insertAll(params: List<Parameter>) {
        parameterDAO.insertAll(params)
    }

}