package com.example.aquariumtracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.AquariumReminderCrossRef
import com.example.aquariumtracker.database.model.Reminder
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.ReminderViewModel
import com.example.aquariumtracker.utilities.longtoTimeStr
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class NextReminder : Fragment() {
    private val aqSelector: AquariumSelector by activityViewModels()
    private lateinit var remVM: ReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_next_reminder, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        remVM = ViewModelProvider(this).get(ReminderViewModel::class.java)

        val remCard: CardView = view.findViewById(R.id.rem_list_card)
        val remNameCheck: CheckBox = view.findViewById(R.id.rem_name_check)
        val remDate: TextView = view.findViewById(R.id.rem_date)
        val remTime: TextView = view.findViewById(R.id.rem_time)
        val remCompleted: TextView = view.findViewById(R.id.reminders_completed)

        aqSelector.selected.observe(viewLifecycleOwner, Observer { aq ->
            aq?.let { id ->
                remVM.getRemindersForAquarium(id).observe(
                    viewLifecycleOwner, Observer { rems ->
                        val nextReminder = rems.getNextReminder()
                        if (nextReminder != null) {
                            remNameCheck.setOnClickListener {
                                if (remNameCheck.isChecked) {
                                    markReminderCompleted(nextReminder)
                                } else {
                                    remNameCheck.isChecked = true
                                }
                            }
                            remCompleted.visibility = ViewGroup.GONE
                            remNameCheck.text = nextReminder.name
                            remNameCheck.isChecked = nextReminder.completed
                            if (!nextReminder.completed) {
                                remDate.text = "Due on " + nextReminder.nextReminderStr()
                                if (nextReminder.notification_time != null) {
                                    remTime.text =
                                        " " + longtoTimeStr(nextReminder.notification_time)
                                } else {
                                    remTime.text = ""
                                }
                            } else {
                                remDate.text =
                                    "Completed on " + DateFormat.getDateInstance()
                                        .format(nextReminder.completedOn)
                                remTime.text = ""
                            }
                        } else {
                            remCard.visibility = ViewGroup.GONE
                            remCompleted.text = "No active reminders!"
                        }
                    })
            }
        })

    }

    fun markReminderCompleted(rem: Reminder) {
        aqSelector.selected.observe(viewLifecycleOwner, Observer { aqID ->
            viewLifecycleOwner.lifecycleScope.launch {
                if (rem.repeatable && rem.repeat_time != null) {
                    val nextRem = rem.nextReminderCal().timeInMillis
                    if (nextRem != null) {
                        val remID = remVM.insert(
                            Reminder(
                                reminder_id = 0,
                                name = rem.name,
                                repeat_time = rem.repeat_time,
                                repeatable = rem.repeatable,
                                start_time = nextRem,
                                notification_time = rem.notification_time,
                                notify = rem.notify
                            )
                        )
                        val remaqXref = AquariumReminderCrossRef(aqID, remID)
                        remVM.insertRelation(remaqXref)
                    }
                }
                rem.completed = true
                rem.completedOn = Calendar.getInstance().timeInMillis
                remVM.updateReminder(rem)
            }
        })
    }

}