package com.example.aquariumtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aquariumtracker.database.models.Aquarium

@Dao
interface AquariumDAO {
    @Query("SELECT * from aquarium_table ORDER BY id ASC")
    fun getAquariumList(): LiveData<List<Aquarium>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(aq: Aquarium)

    @Query("DELETE FROM aquarium_table")
    suspend fun deleteAll()
}