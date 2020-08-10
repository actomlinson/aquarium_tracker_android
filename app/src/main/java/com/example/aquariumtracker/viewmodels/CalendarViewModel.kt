package com.example.aquariumtracker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.text.DateFormat
import java.util.*

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val cal = Calendar.getInstance()

    init {
        cal.set(Calendar.HOUR_OF_DAY, Companion.DEFAULT_TIME_HOUR)
        cal.set(Calendar.MINUTE, DEFAULT_TIME_MIN)
    }

    fun getDateStr(): String {
        return DateFormat.getDateInstance().format(cal.time)
    }

    fun getTimeStr(): String {
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.time)
    }

    fun set(yr: Int, mon: Int, day: Int) {
        cal.set(yr, mon, day)
    }

    fun set(hr: Int, min: Int) {
        cal.set(Calendar.HOUR_OF_DAY, hr)
        cal.set(Calendar.MINUTE, min)
    }

    fun getYr(): Int {
        return cal.get(Calendar.YEAR)
    }

    fun getMon(): Int {
        return cal.get(Calendar.MONTH)
    }

    fun getDay(): Int {
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    fun getHr(): Int {
        return cal.get(Calendar.HOUR)
    }

    fun getMin(): Int {
        return cal.get(Calendar.MINUTE)
    }

    companion object {
        private const val DEFAULT_TIME_HOUR = 9
        private const val DEFAULT_TIME_MIN = 0
    }

}
