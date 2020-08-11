package com.example.aquariumtracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.aquariumtracker.database.AppDatabase
import com.example.aquariumtracker.database.model.AquariumReminderCrossRef
import com.example.aquariumtracker.database.model.Reminder
import com.example.aquariumtracker.repository.ReminderRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReminderRepository

    val allReminders: LiveData<List<Reminder>>

    init {
        val reminderDAO = AppDatabase.getDatabase(application, viewModelScope).reminderDao()
        repository = ReminderRepository(reminderDAO)
        allReminders = repository.allReminders
    }

    suspend fun insert(reminder: Reminder): Long {
        return withContext(viewModelScope.coroutineContext) {
            repository.insert(reminder)
        }
    }

    fun insertRelation(remaqXref: AquariumReminderCrossRef) {
        viewModelScope.launch {
            repository.insertRelation(remaqXref)
        }
    }

    fun getRemindersForAquarium(aqID: Long) = repository.getRemindersForAquarium(aqID)


}