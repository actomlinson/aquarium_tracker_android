package com.example.aquariumtracker.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.aquariumtracker.database.model.ParameterWithMeasurements
import com.example.aquariumtracker.viewmodels.AquariumSelector
import com.example.aquariumtracker.viewmodels.MeasurementViewModel
import com.example.aquariumtracker.viewmodels.ParameterViewModel

class ParameterList : Fragment() {

    private lateinit var paramViewModel: ParameterViewModel
    private lateinit var measureViewModel: MeasurementViewModel
    private val aqSelector: AquariumSelector by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parameter_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycle_measure)
        val viewAdapter = MeasurementListAdapter(view.context.applicationContext)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        paramViewModel = ViewModelProvider(this).get(ParameterViewModel::class.java)
        measureViewModel = ViewModelProvider(this).get(MeasurementViewModel::class.java)

        aqSelector.selected.observe(viewLifecycleOwner, Observer { aq ->
            aq?.let {id ->
                paramViewModel.getParametersWithMeasurements(id).observe(
                    viewLifecycleOwner, Observer { params ->

                        params?.let {
                            viewAdapter.setParametersWithMeasurements(it)
                        }
                    })
            }
        })
    }

}


class MeasurementListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<MeasurementListAdapter.MeasurementViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var parametersWithMeaurements = emptyList<ParameterWithMeasurements>()

    inner class MeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val measure1: TextView = itemView.findViewById(R.id.measure1)
        val measure2: TextView = itemView.findViewById(R.id.measure2)
        val paramNameTextView: TextView = itemView.findViewById(R.id.param_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_measurement_table, parent, false)
        return MeasurementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val current = parametersWithMeaurements[position]
        val currentm = current.getSortedMeasurements()
        holder.paramNameTextView.text = current.param.aq_id.toString() //.name
        Log.i("",current.getSortedMeasurements().toString())
        holder.measure1.text = if (currentm.size > 0) currentm[0].value.toString() else "-"
        holder.measure2.text = if (currentm.size > 1) currentm[1].value.toString() else "-"
    }

    internal fun setParametersWithMeasurements(params: List<ParameterWithMeasurements>) {
        Log.i("param list fetched", params.last().toString())
        this.parametersWithMeaurements = params
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return parametersWithMeaurements.size
    }
}
