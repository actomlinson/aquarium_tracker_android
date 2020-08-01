package com.example.aquariumtracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.aquariumtracker.database.AppDatabase
import com.example.aquariumtracker.database.model.Reminder
import com.example.aquariumtracker.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReminderRepository

    val allReminders: LiveData<List<Reminder>>

    init {
        val reminderDAO = AppDatabase.getDatabase(application, viewModelScope).reminderDao()
        repository = ReminderRepository(reminderDAO)
        allReminders = repository.allReminders
    }

    fun insert(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(reminder)
    }
}