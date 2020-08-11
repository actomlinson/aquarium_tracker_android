package com.example.aquariumtracker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Measurement
import com.example.aquariumtracker.database.model.Parameter
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.MeasurementViewModel
import com.example.aquariumtracker.ui.viewmodel.ParameterViewModel
import java.util.*
import kotlin.collections.HashMap

class AddMeasurement : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_measurement, container, false)
    }

    private lateinit var paramViewModel: ParameterViewModel
    private lateinit var measureViewModel: MeasurementViewModel
    private val aqSelector: AquariumSelector by activityViewModels()
    private lateinit var viewAdapter: ParameterListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val recyclerView = view.findViewById<RecyclerView>(R.id.param_list)
        viewAdapter =
            ParameterListAdapter(view.context.applicationContext)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        paramViewModel = ViewModelProvider(this).get(ParameterViewModel::class.java)
        measureViewModel = ViewModelProvider(this).get(MeasurementViewModel::class.java)

        aqSelector.selected.observe(viewLifecycleOwner, Observer { aq ->
            aq?.let {id ->
                paramViewModel.getParametersForAquarium(id).observe(viewLifecycleOwner, Observer { params ->
                    params?.let {
                        viewAdapter.setParameters(it)
                    }
                })
            }
        })
    }

    private fun startTimer(message: String, seconds: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }
        val packageManager = context?.packageManager
        if (packageManager != null && intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun saveMeasurements() {
        val cal = Calendar.getInstance()
        val time = cal.timeInMillis

        for (c in 0 until viewAdapter.itemCount) {
            val entry = viewAdapter.texts[c]
            val entryDouble = try {
                entry?.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                null
            }
            val pID = viewAdapter.parameters[c].param_id
            measureViewModel.insert(Measurement(
                measure_id = 0, param_id = pID, value = entryDouble,
                time = time)
            )
            Log.i("AddMeasurement", time.toString())
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
                saveMeasurements()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

class ParameterListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<ParameterListAdapter.ParameterViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var parameters = emptyList<Parameter>()
    var texts: HashMap<Int, EditText> = HashMap()

    inner class ParameterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val unitsTextView: TextView = itemView.findViewById(R.id.units)
        val paramNameTextView: TextView = itemView.findViewById(R.id.param_name)
        val paramIDStorage: TextView = itemView.findViewById(R.id.param_id)
        val paramInput: EditText = itemView.findViewById(R.id.param_input)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParameterViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_measurement_add, parent, false)
        return ParameterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParameterViewHolder, position: Int) {
        texts[position] = holder.paramInput // save this to be accessed later when saving measurements
        val current = parameters[position]
        holder.paramIDStorage.text = current.param_id.toString()
        holder.unitsTextView.text = current.param_id.toString() // current.units
        holder.paramNameTextView.text = current.name
    }

    internal fun setParameters(params: List<Parameter>) {
        this.parameters = params
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return parameters.size
    }
}
