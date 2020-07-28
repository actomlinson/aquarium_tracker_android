package com.example.aquariumtracker.repository

import androidx.lifecycle.LiveData
import com.example.aquariumtracker.database.dao.ReminderDAO
import com.example.aquariumtracker.database.model.Reminder


class ReminderRepository(private val reminderDAO: ReminderDAO) {

    fun getRemindersForAquarium(aqID: Int) = reminderDAO.getAquariumWithReminders(aqID)

    val allReminders: LiveData<List<Reminder>> = reminderDAO.getAllReminders()

    suspend fun insert(rem: Reminder) {
        reminderDAO.insert(rem)
    }

    suspend fun insertAll(rems: List<Reminder>) {
        reminderDAO.insertAll(rems)
    }

}