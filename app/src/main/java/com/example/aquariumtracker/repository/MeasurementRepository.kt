package com.example.aquariumtracker.repository

import com.example.aquariumtracker.database.dao.MeasurementDAO
import com.example.aquariumtracker.database.model.Measurement

class MeasurementRepository(private val measurementDAO: MeasurementDAO) {

    fun getAllMeasurementsForParameter(pID: Int) =
        measurementDAO.getAllMeasurementsForParameter(pID)

    fun getMostRecentMeasurementsForParameter(n: Int, pID: Int) =
        measurementDAO.getMostRecentMeasurementsForParameter(n, pID)

    suspend fun insert(m: Measurement) {
        measurementDAO.insert(m)
    }

    suspend fun insertAll(ms: List<Measurement>) {
        measurementDAO.insertAll(ms)
    }

}