package com.example.aquariumtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aquariumtracker.database.model.Measurement

@Dao
interface MeasurementDAO {

    @Query("SELECT * from measurement_table  WHERE param_id = :pID ORDER BY time ASC")
    fun getAllMeasurementsForParameter(pID: Int): LiveData<List<Measurement>>

    @Query("SELECT :n from measurement_table  WHERE param_id = :pID ORDER BY time ASC")
    fun getMostRecentMeasurementsForParameter(n: Int, pID: Int): LiveData<List<Measurement>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(measurement: Measurement)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(measurements: List<Measurement>)

    @Query("DELETE FROM measurement_table")
    suspend fun deleteAll()

}