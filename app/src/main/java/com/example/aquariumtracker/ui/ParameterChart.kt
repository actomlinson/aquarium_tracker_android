package com.example.aquariumtracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.ParameterWithMeasurements
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
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

class ParameterChart : Fragment() {

    private lateinit var paramViewModel: ParameterViewModel
    private lateinit var measureViewModel: MeasurementViewModel
    private val aqSelector: AquariumSelector by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parameter_chart, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paramViewModel = ViewModelProvider(this).get(ParameterViewModel::class.java)
        measureViewModel = ViewModelProvider(this).get(MeasurementViewModel::class.java)
        val chart = view.findViewById<LineChart>(R.id.chart)

        aqSelector.selected.observe(viewLifecycleOwner, Observer { aqID ->
            paramViewModel.getParametersWithMeasurements(aqID)
                .observe(viewLifecycleOwner, Observer {
                    makeGraph(it, chart)
                })
        })
    }

    private fun makeGraph(values: List<ParameterWithMeasurements>, chart: LineChart) {
        val dataSets = ArrayList<ILineDataSet>()
        val labels = HashMap<Int, String>()
        val colorScheme = ColorTemplate.MATERIAL_COLORS

        values.forEachIndexed { i, pwm ->
            val entries = ArrayList<Entry>()
            pwm.measurements.forEachIndexed { j, m ->
                if (m.value != null) {
                    entries.add(Entry(j.toFloat(), m.value.toFloat()))
                    labels[i] = DateFormat.getDateInstance(DateFormat.SHORT).format(m.time)
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

        chart.xAxis.valueFormatter = formatter
        chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        chart.invalidate()
    }
}