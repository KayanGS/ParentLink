package com.example.myapplication.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Get the upcoming Mondays for the dropdown
 * @return List of pairs of label and value
 */
fun getUpcomingMondays(): List<Pair<String, String>> {
    val internalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // for storing
    val labelFormat = SimpleDateFormat("EEEE dd/MM", Locale.getDefault()) // for displaying

    val today = Calendar.getInstance() // today's date
    val mondays = mutableListOf<Pair<String, String>>() // list of upcoming mondays

    val monday = today.clone() as Calendar // clone today's date
    monday.set(Calendar.HOUR_OF_DAY, 0) // set to midnight
    monday.set(Calendar.MINUTE, 0) // set to midnight
    monday.set(Calendar.SECOND, 0) // set to midnight
    monday.set(Calendar.MILLISECOND, 0) // set to midnight

    val dow = monday.get(Calendar.DAY_OF_WEEK) // day of week of today
    if (dow != Calendar.MONDAY) { // if today is not Monday
        monday.add(Calendar.DAY_OF_MONTH, -((dow + 5) % 7)) // set to Monday
    }

    for (i in 0..4) {
        val label = labelFormat.format(monday.time)
        val value = internalFormat.format(monday.time)
        mondays.add(Pair(label, value))
        monday.add(Calendar.DAY_OF_MONTH, 7)
    }

    return mondays
}