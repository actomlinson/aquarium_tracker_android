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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aquariumtracker.NotificationService
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Reminder
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.ReminderViewModel
import com.example.aquariumtracker.utilities.longtoTimeStr

class ReminderList: Fragment() {

    private lateinit var reminderViewModel: ReminderViewModel
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

        val recyclerView = view.findViewById<RecyclerView>(R.id.rem_list)
        val viewAdapter = ReminderListAdapter(view.context)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)

        aqSelector.selected.observe(viewLifecycleOwner, Observer { aq ->
            aq?.let {id ->
                reminderViewModel.getRemindersForAquarium(id).observe(
                    viewLifecycleOwner, Observer { rems ->
                        rems?.let {
                            viewAdapter.setReminders(it.reminders)
                            Log.i("ReminderList", it.aq.nickname)
                            Log.i("ReminderList", it.reminders.toString())
                        }
                    })
            }
        })

        val not = NotificationService(this.requireContext())
        not.alarm()
    }

}


class ReminderListAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder>(), DialogEditDelete.DialogListener {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var reminders = emptyList<Reminder>()

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val remCard: CardView = itemView.findViewById(R.id.rem_list_card)
        val remNameCheck: CheckBox = itemView.findViewById(R.id.rem_name_check)
        val remDate: TextView = itemView.findViewById(R.id.rem_date)
        val remTime: TextView = itemView.findViewById(R.id.rem_time)
        val remRepeat: TextView = itemView.findViewById(R.id.rem_repeat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_reminder_card, parent, false)
        return ReminderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.remCard.setOnLongClickListener {
            val editDeleteDialog = DialogEditDelete(context, this, position)
            editDeleteDialog.show()
            Log.i("ReminderList", position.toString())
            true
        }
        val current = reminders[position]
        holder.remNameCheck.text = current.name
        holder.remDate.text = "need to calc next date"
        holder.remTime.text = longtoTimeStr(current.notification_time)
    }

    internal fun setReminders(reminders: List<Reminder>) {
        this.reminders = reminders
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    override fun onDeleteConfirmation(position: Int) {
        Log.i("ReminderList", "delete " + reminders[position].name)
    }

    override fun onEditConfirmation(position: Int) {
        Log.i("ReminderList", "edit " + reminders[position].name)
    }
}
