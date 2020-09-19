package com.example.aquariumtracker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.MeasurementsByDate
import com.example.aquariumtracker.database.model.Parameter
import com.example.aquariumtracker.database.model.ParameterWithMeasurements
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.AquariumViewModel
import com.example.aquariumtracker.ui.viewmodel.MeasurementViewModel
import com.example.aquariumtracker.ui.viewmodel.ParameterViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DateFormat

class ParameterDetail : Fragment() {

    private lateinit var paramViewModel: ParameterViewModel
    private lateinit var measureViewModel: MeasurementViewModel
    private lateinit var aqViewModel: AquariumViewModel
    private val aqSelector: AquariumSelector by activityViewModels()
    private val colorScheme = ColorTemplate.MATERIAL_COLORS

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parameter_detail, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paramViewModel = ViewModelProvider(this).get(ParameterViewModel::class.java)
        measureViewModel = ViewModelProvider(this).get(MeasurementViewModel::class.java)
        aqViewModel = ViewModelProvider(this).get(AquariumViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycle_param)
        val viewAdapter = ParameterWMeasurementsAdapter(view.context.applicationContext)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        val chart = view.findViewById<LineChart>(R.id.chart)
        val titleRow = view.findViewById<LinearLayout>(R.id.title_row)

        aqSelector.selected.observe(viewLifecycleOwner, Observer { aqID ->
            aqViewModel.getAquariumWithMeasurements(aqID)
                .observe(viewLifecycleOwner, Observer { awm ->
                    viewAdapter.setParametersWithMeasurements(awm.measurementSets)
                })
            paramViewModel.getParametersForAquarium(aqID)
                .observe(viewLifecycleOwner, Observer {
                    setTitleRow(it, titleRow)
                })
            paramViewModel.getParametersWithMeasurements(aqID)
                .observe(viewLifecycleOwner, Observer {
                    makeGraph(it, chart)
                })
        })
    }

    private fun setTitleRow(params: List<Parameter>, titleRow: LinearLayout) {
        val dateTV = TextView(context)
        dateTV.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1f
        )
        dateTV.textAlignment = TEXT_ALIGNMENT_CENTER
        dateTV.text = "Date"
        titleRow.addView(dateTV)

        params.forEachIndexed { i, param ->
            val color = TextView(context)
            color.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
            )
//            color.setPadding(R.dimen.padding)
            color.setBackgroundColor(colorScheme[i % colorScheme.size])
            titleRow.addView(color)
        }
    }

    private fun makeGraph(values: List<ParameterWithMeasurements>, chart: LineChart) {
        val dataSets = ArrayList<ILineDataSet>()
        val labels = HashMap<Int, String>()

        values.forEachIndexed { i, pwm ->
            val entries = ArrayList<Entry>()
            pwm.getSortedMeasurements().reversed().forEachIndexed { j, m ->
                if (m.value != null) {
                    entries.add(Entry(j.toFloat(), m.value.toFloat()))
                    labels[j] = DateFormat.getDateInstance(DateFormat.SHORT).format(m.time)
                }
            }
            val dataSet = LineDataSet(entries, pwm.param.name)
            val color = colorScheme[i % colorScheme.size]
            dataSet.color = color
            dataSet.setCircleColor(color)
            dataSets.add(dataSet)
        }

        val lineData = LineData(dataSets)
        chart.data = lineData

        val formatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return labels[value.toInt()] ?: ""
            }
        }
        chart.xAxis.granularity = 1f
        chart.xAxis.valueFormatter = formatter
        chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        chart.invalidate()
    }
}


class ParameterWMeasurementsAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<ParameterWMeasurementsAdapter.ParameterWMeasurementsHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var measurementsByDate = emptyList<MeasurementsByDate>()

    class ParameterWMeasurementsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout: LinearLayout = itemView.findViewById(R.id.measure_table_row)
        val paramNameTextView: TextView = itemView.findViewById(R.id.param_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParameterWMeasurementsHolder {
        val itemView = inflater.inflate(R.layout.recycle_measurement_detail, parent, false)
        return ParameterWMeasurementsHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParameterWMeasurementsHolder, position: Int) {
        val current = measurementsByDate[position]
        holder.paramNameTextView.text =
            DateFormat.getDateInstance(DateFormat.SHORT).format(current.mset.time)

        current.measurements.forEachIndexed { index, measurement ->
            val tv = TextView(context)
            tv.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
            )
            //tv.gravity = Gravity.CENTER_HORIZONTAL
            tv.textAlignment = TEXT_ALIGNMENT_CENTER
            tv.text = measurement.value.toString()

            holder.linearLayout.addView(tv)
        }
    }

    internal fun setParametersWithMeasurements(values: List<MeasurementsByDate>) {
        this.measurementsByDate = values.sortedBy { it -> it.mset.time }.reversed()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return measurementsByDate.size
    }
}
