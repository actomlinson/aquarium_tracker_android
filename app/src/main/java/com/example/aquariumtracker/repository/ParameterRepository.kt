package com.example.aquariumtracker.repository

import androidx.lifecycle.LiveData
import com.example.aquariumtracker.database.dao.ParameterDAO
import com.example.aquariumtracker.database.model.Parameter

class ParameterRepository(private val parameterDAO: ParameterDAO) {

    fun getParametersForAquarium(aqID: Int) = parameterDAO.getParametersForAquarium(aqID)

    val aq0Params: LiveData<List<Parameter>> = parameterDAO.getParametersForAquarium(0)

    val allParams: LiveData<List<Parameter>> = parameterDAO.getAllParametersList()

    suspend fun insert(param: Parameter) {
        parameterDAO.insert(param)
    }

    suspend fun insertAll(params: List<Parameter>) {
        parameterDAO.insertAll(params)
    }


}