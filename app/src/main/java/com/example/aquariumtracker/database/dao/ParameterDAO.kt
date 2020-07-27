package com.example.aquariumtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.aquariumtracker.database.model.Parameter
import com.example.aquariumtracker.database.model.ParameterWithMeasurements

@Dao
interface ParameterDAO {

    @Query("SELECT * from parameter_table WHERE aq_id = :aqID ORDER BY p_order ASC")
    fun getParametersForAquarium(aqID: Int): LiveData<List<Parameter>>

    @Query("SELECT * from parameter_table ORDER BY aq_id, p_order ASC")
    fun getAllParametersList(): LiveData<List<Parameter>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(param: Parameter)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(params: List<Parameter>)

    @Query("DELETE FROM parameter_table")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM parameter_table WHERE aq_id = :aqID")
    fun getParameterWithMeasurements(aqID: Int): LiveData<List<ParameterWithMeasurements>>
}