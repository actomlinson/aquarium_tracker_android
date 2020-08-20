package com.example.aquariumtracker.ui

import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Reminder
import com.example.aquariumtracker.ui.viewmodel.CalendarViewModel


/** Performs error checking and sets an alarm for the reminder */
class ReminderAddHelper(fragment: Fragment) {
    private var calVM = ViewModelProvider(fragment).get(CalendarViewModel::class.java)

    private fun validReminderName(input: String): Boolean {
        return input != "" && input.length <= 50
    }

    private fun validRepeatPeriod(input: String): Boolean {
        return try {
            val cycle = input.toInt()
            cycle > 0
        } catch (e: Exception) {
            false
        }
    }

    private fun saveReminder(view: View): Reminder? {

        val repeatable = view.findViewById<Switch>(R.id.repeat_switch)?.isChecked
        val repeatTime = view.findViewById<EditText>(R.id.repeat_period)?.text.toString()
        val repeatCycle = view.findViewById<Spinner>(R.id.repeat_cycle)?.selectedItemId
        val notification = view.findViewById<Switch>(R.id.notification_switch)?.isChecked
        val notificationTime = calVM.getTimeinMillis()
        val reminderName = view.findViewById<EditText>(R.id.rem_name)?.text.toString()
        val startDate = calVM.getTimeinMillis()

        return if (repeatable != null &&
            notification != null &&
            validReminderName(reminderName) &&
            validRepeatPeriod(repeatTime)) {
            val rem = Reminder(reminder_id = 0, name = reminderName, repeatable = repeatable,
                repeat_time = repeatTime.toInt(), start_time = startDate, notify = notification,
                notification_time = notificationTime
            )
            rem
        } else {
            null
        }
    }

}