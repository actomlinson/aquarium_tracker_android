package com.example.aquariumtracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.aquariumtracker.database.model.AquariumReminderCrossRef
import com.example.aquariumtracker.database.model.AquariumWithReminders
import com.example.aquariumtracker.database.model.Reminder

@Dao
interface ReminderDAO {

    @Query("SELECT * FROM reminder_table")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(rem: Reminder): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rems: List<Reminder>): List<Long>

    @Update
    suspend fun updateReminder(rem: Reminder)

    @Query("DELETE FROM reminder_table")
    suspend fun deleteAll()

    @Query("DELETE FROM reminder_table WHERE reminder_id = :remID")
    suspend fun deleteReminder(remID: Long)

    @Transaction
    @Query("SELECT * FROM aquarium_table WHERE aq_id = :aqID")
    fun getAquariumWithReminders(aqID: Long): LiveData<AquariumWithReminders>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(remaqXref: AquariumReminderCrossRef)

    @Query("DELETE FROM AquariumReminderCrossRef WHERE reminder_id = :remID")
    suspend fun deleteRelation(remID: Long)
}