package com.example.aquariumtracker.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.aquariumtracker.api.getNetworkService
import com.example.aquariumtracker.database.dao.ParameterDAO
import com.example.aquariumtracker.database.model.Parameter
import com.example.aquariumtracker.database.model.ParameterWithMeasurements
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        val paramIDs = parameterDAO.insertAll(params)

        withContext(Dispatchers.IO) {
            try {
                val network = getNetworkService()
                for (p in params.indices) {
                    params[p].param_id = paramIDs[p]
                    val result = network.insertParameter(params[p]).execute()
                    if (result.isSuccessful) {
                        Log.i("ParameterRepository", "Insertion successful")
                    } else {}
                }
            } catch (cause: Throwable) {
                Log.e("ParameterRepository", cause.message.toString())
            }
        }
    }

}