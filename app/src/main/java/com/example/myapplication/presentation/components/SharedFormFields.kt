package com.example.myapplication.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.myapplication.utils.getUpcomingMondays

@Composable
fun SharedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SharedWeekCommencingField(
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    val options = getUpcomingMondays().map { it.first }
    val labelToValueMap = getUpcomingMondays().toMap()

    SharedDropdownField(
        label = "Select Week Commencing Date",
        options = options,
        selected = options.firstOrNull { labelToValueMap[it] == selectedValue } ?: ""
    ) { selectedLabel ->
        onValueChange(labelToValueMap[selectedLabel] ?: "")
    }
}
