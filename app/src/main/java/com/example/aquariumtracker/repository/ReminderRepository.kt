package com.example.aquariumtracker.repository

import androidx.lifecycle.LiveData
import com.example.aquariumtracker.database.dao.ReminderDAO
import com.example.aquariumtracker.database.model.AquariumReminderCrossRef
import com.example.aquariumtracker.database.model.Reminder


class ReminderRepository(private val reminderDAO: ReminderDAO) {

    fun getRemindersForAquarium(aqID: Long) = reminderDAO.getAquariumWithReminders(aqID)

    val allReminders: LiveData<List<Reminder>> = reminderDAO.getAllReminders()

    suspend fun insert(rem: Reminder): Long {
        return reminderDAO.insert(rem)
    }

    suspend fun insertAll(rems: List<Reminder>): List<Long> {
        return reminderDAO.insertAll(rems)
    }

    suspend fun insertRelation(remaqXref: AquariumReminderCrossRef) {
        reminderDAO.insertRelation(remaqXref)
    }

    suspend fun deleteReminder(remID: Long) = reminderDAO.deleteReminder(remID)
    suspend fun deleteRelation(remID: Long) = reminderDAO.deleteRelation(remID)
    suspend fun updateReminder(rem: Reminder) = reminderDAO.updateReminder(rem)
}