package com.example.aquariumtracker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Reminder
import com.example.aquariumtracker.viewmodels.AquariumSelector
import com.example.aquariumtracker.viewmodels.ReminderViewModel

class ReminderList: Fragment() {

    private lateinit var reminderViewModel: ReminderViewModel
    private val aqSelector: AquariumSelector by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recycle_measure)
        val viewAdapter = ReminderListAdapter(view.context.applicationContext)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)

        aqSelector.selected.observe(viewLifecycleOwner, Observer { aq ->
            aq?.let {id ->
                reminderViewModel.allReminders.observe(
                    viewLifecycleOwner, Observer { rems ->
                        rems?.let {
                            viewAdapter.setReminders(it)
                        }
                    })
            }
        })
    }
}





class ReminderListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var reminders = emptyList<Reminder>()

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val measure1: TextView = itemView.findViewById(R.id.measure1)
        val measure2: TextView = itemView.findViewById(R.id.measure2)
        val paramNameTextView: TextView = itemView.findViewById(R.id.param_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_measurement_table, parent, false)
        return ReminderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val current = reminders[position]
    }

    internal fun setReminders(reminders: List<Reminder>) {
        this.reminders = reminders
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return reminders.size
    }
}
