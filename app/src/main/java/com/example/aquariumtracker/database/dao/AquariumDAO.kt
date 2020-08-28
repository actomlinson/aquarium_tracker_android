package com.example.aquariumtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.database.model.AquariumWithImages
import com.example.aquariumtracker.database.model.AquariumWithMeasurements

@Dao
interface AquariumDAO {

    @Query("SELECT * from aquarium_table WHERE aq_id = :aqID")
    fun getAquarium(aqID: Long): LiveData<Aquarium>

    @Query("SELECT * from aquarium_table WHERE aq_id = :aqID")
    fun getAquariumNonLive(aqID: Long): Aquarium

    @Query("SELECT * from aquarium_table ORDER BY aq_id ASC")
    fun getAquariumList(): LiveData<List<Aquarium>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(aq: Aquarium): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(aqs: List<Aquarium>): List<Long>

    @Query("DELETE FROM aquarium_table")
    suspend fun deleteAll()

    @Query("DELETE FROM aquarium_table WHERE aq_id = :aqID")
    suspend fun deleteAquarium(aqID: Long)

    @Transaction
    @Query("SELECT * FROM aquarium_table WHERE aq_id = :aqID")
    fun getAquariumWithImages(aqID: Long): LiveData<AquariumWithImages>

    @Transaction
    @Query("SELECT * FROM aquarium_table")
    fun getAquariumsWithImages(): LiveData<List<AquariumWithImages>>

    @Transaction
    @Query("SELECT * FROM aquarium_table WHERE aq_id = :aqID")
    fun getAquariumWithMeasurements(aqID: Long): LiveData<AquariumWithMeasurements>
}