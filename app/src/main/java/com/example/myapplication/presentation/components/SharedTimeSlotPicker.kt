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
    // A.M. START
    SharedDropdownField(
        amStartLabel,
        listOf("09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30"),
        amStartTime
    ) {
        onAmStartTimeChange(it)
        onPmStartTimeChange("")
    }

    // P.M. START
    if (amStartTime.isBlank()) {
        SharedDropdownField(
            pmStartLabel,
            listOf("13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"),
            pmStartTime
        ) {
            onPmStartTimeChange(it)
        }
    }

    // A.M. END
    if (amStartTime.isNotBlank()) {
        SharedDropdownField(
            amEndLabel,
            listOf("10:00", "10:30", "11:00", "11:30", "12:00", "12:30"),
            amEndTime
        ) {
            if (it <= amStartTime) {
                onErrorChange("${'$'}amEndLabel must be after Start Time")
                onAmEndTimeChange("")
            } else {
                onErrorChange("")
                onAmEndTimeChange(it)
            }
        }
    }

    // P.M. END
    if (amStartTime.isNotBlank()) {
        SharedDropdownField(
            pmEndLabel,
            listOf("14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00"),
            pmEndTime
        ) {
            if (it <= pmStartTime) {
                onErrorChange("${'$'}pmEndLabel must be after Start Time")
                onPmEndTimeChange("")
            } else {
                onErrorChange("")
                onPmEndTimeChange(it)
            }
        }
    }

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
