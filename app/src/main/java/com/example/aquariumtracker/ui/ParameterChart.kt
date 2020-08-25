package com.example.aquariumtracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aquariumtracker.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

class ParameterChart : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parameter_chart, container)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart = view.findViewById<LineChart>(R.id.chart)
        val cal = Calendar.getInstance()
        val entries = listOf<Entry>(
            Entry(1f, 5f),
            Entry(2f, 15f),
            Entry(3f, 5f),
            Entry(4f, 10f),
            Entry(5f, 15f)
//            Entry(cal.timeInMillis.toFloat() / 1000, 5f),
//            Entry(cal.timeInMillis.toFloat() + 1 / 1000, 10f),
//            Entry(cal.timeInMillis.toFloat() + 2 / 1000, 15f),
//            Entry(cal.timeInMillis.toFloat() + 3 / 1000, 5f),
//            Entry(cal.timeInMillis.toFloat() + 4 / 1000, 15f)
        )
        val dataSet = LineDataSet(entries, "Test")
        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()
    }
}