package com.example.aquariumtracker.repository

import com.example.aquariumtracker.database.dao.ParameterDAO
import com.example.aquariumtracker.database.model.Parameter

class ParameterRepository(private val parameterDAO: ParameterDAO) {

    fun getParametersForAquarium(aqID: Int) = parameterDAO.getParametersForAquarium(aqID)

    suspend fun insert(param: Parameter) {
        parameterDAO.insert(param)
    }
}