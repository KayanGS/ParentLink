package com.example.myapplication.presentation.components

import android.widget.CalendarView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.*

/**
 * Shared Calendar
 * @param initialDate Initial date
 * @param weekStartMillis Week start date
 * @param onDateSelected Callback when a date is selected
 */
@Composable
fun SharedCalendar(
    initialDate: Long = System.currentTimeMillis(),
    weekStartMillis: Long,
    onDateSelected: (Date) -> Unit
) {
    // Embedded CalendarView
    AndroidView(
        factory = { context ->
            CalendarView(context).apply {
                val calendar = Calendar.getInstance() // Calendar instance
                calendar.timeInMillis = initialDate // Set initial date
                date = initialDate // Set initial date
                minDate = weekStartMillis // Set week start date

                // Set max date to 6 days from the week start date
                val maxCalendar = Calendar.getInstance().apply {
                    timeInMillis = weekStartMillis // Set week start date
                    add(Calendar.DAY_OF_MONTH, 6)
                }

                maxDate = maxCalendar.timeInMillis

                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val selectedCal = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    onDateSelected(selectedCal.time)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}