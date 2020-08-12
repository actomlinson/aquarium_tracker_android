package com.example.aquariumtracker.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.AquariumReminderCrossRef
import com.example.aquariumtracker.database.model.Reminder
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.CalendarViewModel
import com.example.aquariumtracker.ui.viewmodel.ReminderViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ReminderAdd: Fragment() {
    private lateinit var calVM: CalendarViewModel
    private lateinit var reminderVM: ReminderViewModel
    private val aqSelector: AquariumSelector by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        calVM = ViewModelProvider(this).get(CalendarViewModel::class.java)
        reminderVM = ViewModelProvider(this).get(ReminderViewModel::class.java)

        val repeatable = view.findViewById<Switch>(R.id.repeat_switch)
        val repeatOptions = view.findViewById<LinearLayout>(R.id.repeat_options)
        repeatable.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                repeatOptions.visibility = View.VISIBLE
            } else {
                repeatOptions.visibility = View.GONE
            }
        }

        val notification = view.findViewById<Switch>(R.id.notification_switch)
        val notificationOptions = view.findViewById<LinearLayout>(R.id.notification_options)
        notification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                notificationOptions.visibility = View.VISIBLE
            } else {
                notificationOptions.visibility = View.GONE
            }
        }

        val date = view.findViewById<TextView>(R.id.date)
        val dateSetButton = view.findViewById<Button>(R.id.date_set)
        date.text = calVM.getDateStr()

        dateSetButton.setOnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            if (this.context != null) {
                val tpd = DatePickerDialog(this.context!!,
                    DatePickerDialog.OnDateSetListener { _, yr, mon, day ->
                        calVM.set(yr, mon, day)
                        date.text = calVM.getDateStr()
                    },
                    calVM.getYr(),
                    calVM.getMon(),
                    calVM.getDay()
                )
                tpd.show()
            }
        }

        val time = view.findViewById<TextView>(R.id.notification_time)
        val timeButton = view.findViewById<Button>(R.id.time_set)
        time.text = calVM.getTimeStr()

        timeButton.setOnClickListener {
            if (this.context != null) {
                val tpd = TimePickerDialog(this.requireContext(),
                    TimePickerDialog.OnTimeSetListener { _, hr, min ->
                        calVM.set(hr, min)
                        time.text = calVM.getTimeStr()
                    },
                    calVM.getHr(),
                    calVM.getMin(),
                    false
                )
                tpd.show()
            }
        }
    }

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

    private fun saveReminder(view: View) {

        val repeatable = view.findViewById<Switch>(R.id.repeat_switch)?.isChecked
        val repeatTime = view.findViewById<EditText>(R.id.repeat_period)?.text.toString()
        val repeatCycle = view.findViewById<Spinner>(R.id.repeat_cycle)?.selectedItemId
        val notification = view.findViewById<Switch>(R.id.notification_switch)?.isChecked
        val notificationTime = calVM.getTimeinMillis()
        val reminderName = view.findViewById<EditText>(R.id.rem_name)?.text.toString()
        val startDate = calVM.getTimeinMillis()

        if (repeatable != null &&
            notification != null &&
            validReminderName(reminderName) &&
            validRepeatPeriod(repeatTime)) {
            val rem = Reminder(reminder_id = 0, name = reminderName, repeatable = repeatable,
                repeat_time = repeatTime.toInt(), start_time = startDate, notify = notification,
                notification_time = notificationTime
            )

            lifecycleScope.launch {
                val remID = reminderVM.insert(rem)
                aqSelector.selected.observe(viewLifecycleOwner, Observer { aqID ->
                    val remaqXref = AquariumReminderCrossRef(aqID, remID)
                    reminderVM.insertRelation(remaqXref)
                    Log.i("ReminderAdd", remaqXref.toString())
                })
            }

        } else {
            val snack = Snackbar.make(view, getString(R.string.rem_input_error), LENGTH_LONG)
            snack.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_save -> {
                saveReminder(this.requireView())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
