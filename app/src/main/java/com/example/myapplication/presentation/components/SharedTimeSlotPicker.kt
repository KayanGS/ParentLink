package com.example.myapplication.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SharedTimeSlotPicker(
    amStartLabel: String,
    amStartTime: String,
    onAmStartTimeChange: (String) -> Unit,
    pmStartLabel: String,
    pmStartTime: String,
    onPmStartTimeChange: (String) -> Unit,
    amEndLabel: String,
    amEndTime: String,
    onAmEndTimeChange: (String) -> Unit,
    pmEndLabel: String,
    pmEndTime: String,
    onPmEndTimeChange: (String) -> Unit,
    errorMessage: String,
    onErrorChange: (String) -> Unit
) {
    // 🔹 START TIME PICKERS
    if (pmStartTime.isBlank()) {
        SharedDropdownField(
            amStartLabel,
            listOf("09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30"),
            amStartTime
        ) {
            onAmStartTimeChange(it)
            onPmStartTimeChange("")
        }
    }

    if (amStartTime.isBlank()) {

        SharedDropdownField(
            pmStartLabel,
            listOf("13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"),
            pmStartTime
        ) {
            onPmStartTimeChange(it)
        }
    }

    // 🔹 END TIME PICKERS
    if (amStartTime.isNotBlank() || pmStartTime.isNotBlank()) {

        // AM End Time
        if (pmEndTime.isBlank() && pmStartTime.isBlank()) {
            SharedDropdownField(
                amEndLabel,
                listOf("10:00", "10:30", "11:00", "11:30", "12:00", "12:30"),
                amEndTime
            ) {
                val startTime = amStartTime.ifBlank { pmStartTime }
                if (it <= startTime) {
                    onErrorChange("$amEndLabel must be after start time")
                    onAmEndTimeChange("")
                } else {
                    onErrorChange("")
                    onAmEndTimeChange(it)
                    onPmEndTimeChange("") // hide PM end
                }
            }
        }

        // PM End Time
        if (amEndTime.isBlank()) {
            SharedDropdownField(
                pmEndLabel,
                listOf("14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00"),
                pmEndTime
            ) {
                val startTime = amStartTime.ifBlank { pmStartTime }
                if (it <= startTime) {
                    onErrorChange("$pmEndLabel must be after start time")
                    onPmEndTimeChange("")
                } else {
                    onErrorChange("")
                    onPmEndTimeChange(it)
                    onAmEndTimeChange("") // hide AM end
                }
            }
        }
    }

    // 🔹 Error message
    if (errorMessage.isNotBlank()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(4.dp)
        )
    }
}


@Composable
fun SharedSimpleTimeSlotPicker(
    amStartLabel: String,
    pmStartLabel: String,
    amStartTime: String,
    onAmStartTimeChange: (String) -> Unit,
    pmStartTime: String,
    onPmStartTimeChange: (String) -> Unit,
) {
    // AM Start Time
    SharedDropdownField(
        amStartLabel,
        listOf("09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30"),
        amStartTime
    ) {
        onAmStartTimeChange(it)
        onPmStartTimeChange("") // clear PM time if AM is selected
    }

    // PM Start Time (only shown if AM is not selected)
    if (amStartTime.isBlank()) {
        SharedDropdownField(
            pmStartLabel,
            listOf("13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"),
            pmStartTime
        ) {
            onPmStartTimeChange(it)
        }
    }
}
