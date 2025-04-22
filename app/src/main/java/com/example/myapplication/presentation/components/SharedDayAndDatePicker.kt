package com.example.myapplication.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SharedDayAndDatePicker(
    weekCommenceDate: String,
    selectedDayOfWeek: String,
    selectedDate: String,
    onDayOfWeekChange: (String) -> Unit,
    onDateSelected: (String) -> Unit
) {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val labelFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    SharedDropdownField(
        "Select Day of the Week",
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
        selectedDayOfWeek
    ) {
        onDayOfWeekChange(it)
    }

    if (weekCommenceDate.isNotBlank()) {
        Text("Select Date", fontSize = 16.sp)

        val mondayCal = Calendar.getInstance().apply {
            time = formatter.parse(weekCommenceDate) ?: Date()
        }

        val offset = mapOf(
            "Monday" to 0, "Tuesday" to 1, "Wednesday" to 2,
            "Thursday" to 3, "Friday" to 4, "Saturday" to 5, "Sunday" to 6
        )[selectedDayOfWeek] ?: 0

        mondayCal.add(Calendar.DAY_OF_MONTH, offset)

        SharedCalendar(
            initialDate = mondayCal.timeInMillis,
            weekStartMillis = formatter.parse(weekCommenceDate)?.time ?: System.currentTimeMillis()
        ) { date ->
            onDateSelected(labelFormatter.format(date))
            onDayOfWeekChange(SimpleDateFormat("EEEE", Locale.getDefault()).format(date))
        }

        Text("Selected Date: $selectedDate", modifier = Modifier.padding(8.dp))
    }
}
