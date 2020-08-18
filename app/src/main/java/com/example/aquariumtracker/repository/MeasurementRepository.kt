package com.example.aquariumtracker.repository

import android.util.Log
import com.example.aquariumtracker.api.getNetworkService
import com.example.aquariumtracker.database.dao.MeasurementDAO
import com.example.aquariumtracker.database.model.Measurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MeasurementRepository(private val measurementDAO: MeasurementDAO) {

    fun getAllMeasurementsForParameter(pID: Int) =
        measurementDAO.getAllMeasurementsForParameter(pID)

    fun getMostRecentMeasurementsForParameter(n: Int, pID: Int) =
        measurementDAO.getMostRecentMeasurementsForParameter(n, pID)

    suspend fun insert(m: Measurement) {
        measurementDAO.insert(m)
        withContext(Dispatchers.IO) {
            try {
                val network = getNetworkService()
                val result = network.insertMeasurement(m).execute()
                if (result.isSuccessful) {
                    Log.i("MeasurementRepository", "Success")
                } else {
                    Log.i("MeasurementRepository", result.message())
                }
            } catch (cause: Throwable) {
                Log.i("MeasurementRepository", cause.stackTrace.contentToString())
            }
        }
    }

    suspend fun insertAll(ms: List<Measurement>) {
        measurementDAO.insertAll(ms)
    }

}