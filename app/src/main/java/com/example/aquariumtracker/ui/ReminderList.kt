package com.example.aquariumtracker.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.aquariumtracker.R
import com.example.aquariumtracker.SelectableListAdapter
import com.example.aquariumtracker.database.model.AquariumReminderCrossRef
import com.example.aquariumtracker.database.model.Reminder
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.ReminderViewModel
import com.example.aquariumtracker.utilities.longtoTimeStr
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class ReminderList : Fragment(), AdapterOptionsListener {

    private lateinit var remVM: ReminderViewModel
    private val aqSelector: AquariumSelector by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        remVM = ViewModelProvider(this).get(ReminderViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rem_list)
        val viewAdapter = ReminderListAdapter(view.context, this)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        aqSelector.selected.observe(viewLifecycleOwner, Observer { aq ->
            aq?.let { id ->
                remVM.getRemindersForAquarium(id).observe(
                    viewLifecycleOwner, Observer { rems ->
                        rems?.let {
                            viewAdapter.setReminders(
                                it.reminders.sortedBy { elem -> elem.completed }
                            )

                            val wm = WorkManager.getInstance(requireContext())
                            for (r in it.reminders) {
                                Log.i(
                                    "ReminderList",
                                    wm.getWorkInfosByTag(r.reminder_id.toString()).get().toString()
                                )
                            }
                        }
                    })
            }
        })
    }

    override fun markReminderCompleted(rem: Reminder) {
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

    override fun deleteReminder(rem: Reminder) {
        val wm = WorkManager.getInstance(requireContext())
        wm.cancelUniqueWork(rem.reminder_id.toString())
        viewLifecycleOwner.lifecycleScope.launch {
            remVM.deleteRelation(rem.reminder_id)
            remVM.deleteReminder(rem.reminder_id)
        }
    }
}

interface AdapterOptionsListener {
    fun markReminderCompleted(rem: Reminder)
    fun deleteReminder(rem: Reminder)
}

class ReminderListAdapter internal constructor(
    private val context: Context,
    private val listener: AdapterOptionsListener
) : SelectableListAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var reminders = emptyList<Reminder>()

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val remCard: CardView = itemView.findViewById(R.id.rem_list_card)
        val remNameCheck: CheckBox = itemView.findViewById(R.id.rem_name_check)
        val remDate: TextView = itemView.findViewById(R.id.rem_date)
        val remTime: TextView = itemView.findViewById(R.id.rem_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_reminder_card, parent, false)
        return ReminderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder = holder as ReminderViewHolder
        val current = reminders[position]
        holder.remNameCheck.text = current.name
        holder.remNameCheck.isChecked = current.completed
        if (!current.completed) {
            holder.remDate.text = "Due at " + current.nextReminderStr()
            if (current.notification_time != null) {
                holder.remTime.text = " " + longtoTimeStr(current.notification_time)
            } else {
                holder.remTime.text = ""
            }
        } else {
            holder.remDate.text =
                "Completed on " + DateFormat.getDateInstance().format(current.completedOn)
            holder.remTime.text = ""
        }

        holder.remCard.setOnLongClickListener {
            val editDeleteDialog = EditDeleteDialog(context, this, position)
            editDeleteDialog.show()
            true
        }

        holder.remNameCheck.setOnClickListener {
            if (holder.remNameCheck.isChecked) {
                listener.markReminderCompleted(current)
            } else {
                holder.remNameCheck.isChecked = true
            }
        }
    }

    internal fun setReminders(reminders: List<Reminder>) {
        this.reminders = reminders
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    override fun onDeleteConfirmation(pos: Int) {
        listener.deleteReminder(reminders[pos])
    }

    override fun onEditConfirmation(pos: Int) {
        Log.i("ReminderList", "edit " + reminders[pos].name)
    }
}
