package com.example.myapplication.presentation.components

import android.widget.CalendarView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.*

@Composable
fun SharedCalendar(
    initialDate: Long = System.currentTimeMillis(),
    weekStartMillis: Long,
    onDateSelected: (Date) -> Unit
) {
    AndroidView(
        factory = { context ->
            CalendarView(context).apply {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = initialDate
                date = initialDate
                minDate = weekStartMillis

                val maxCalendar = Calendar.getInstance().apply {
                    timeInMillis = weekStartMillis
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