package com.example.aquariumtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aquariumtracker.database.model.Parameter

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
}