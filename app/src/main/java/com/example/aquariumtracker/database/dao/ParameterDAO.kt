package com.example.aquariumtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aquariumtracker.database.model.Parameter

@Dao
interface ParameterDAO {

    @Query("SELECT * from parameter_table WHERE aq_id = :aqID")
    fun getParametersForAquarium(aqID: Int): LiveData<Parameter>

    @Query("SELECT * from parameter_table ORDER BY aq_id, param_id ASC")
    fun getAllParametersList(): LiveData<List<Parameter>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(param: Parameter)

    @Query("DELETE FROM parameter_table")
    suspend fun deleteAll()
}