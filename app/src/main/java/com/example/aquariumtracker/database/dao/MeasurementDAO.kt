package com.example.aquariumtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aquariumtracker.database.model.Measurement
import com.example.aquariumtracker.database.model.MeasurementSet

@Dao
interface MeasurementDAO {

    @Query("SELECT * FROM measurement_table WHERE param_id = :pID ORDER BY time ASC")
    fun getAllMeasurementsForParameter(pID: Int): LiveData<List<Measurement>>

    @Query("SELECT * FROM measurement_table WHERE param_id = :pID ORDER BY time ASC LIMIT :n")
    fun getMostRecentMeasurementsForParameter(n: Int, pID: Int): LiveData<List<Measurement>>

    @Query("SELECT time FROM measurement_table")
    fun getMeasurementTimes(): LiveData<Long>

    @Query("SELECT * FROM measurement_table WHERE time = :time")
    fun getMeasurementsAtTime(time: Long): LiveData<List<Measurement>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(measurement: Measurement)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(measurements: List<Measurement>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(mset: MeasurementSet): Long

    @Query("DELETE FROM measurement_table")
    suspend fun deleteAll()
}