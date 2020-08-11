package com.example.aquariumtracker.utilities

import java.text.DateFormat
import java.util.*

fun longtoTimeStr(input: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = input
    return DateFormat.getTimeInstance(DateFormat.SHORT).format(cal.time)

}