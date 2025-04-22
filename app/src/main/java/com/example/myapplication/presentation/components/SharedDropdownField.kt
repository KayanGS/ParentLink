package com.example.myapplication.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


/**
 * Shared Dropdown Field
 * @param label Label for the dropdown
 * @param options Options for the dropdown
 * @param selected Selected option
 * @param onSelected Callback when an option is selected
 * @return Unit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedDropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) } // State for dropdown expansion, remember for persistence
    ExposedDropdownMenuBox(
        expanded = expanded, // State for dropdown expansion
        onExpandedChange = { expanded = !expanded } // Callback when dropdown is expanded or collapsed
    ) {
        OutlinedTextField(
            readOnly = true, // Read-only field
            value = selected, // Selected option
            onValueChange = {}, // Callback when text changes
            label = { Text(label) }, // Label for the text field
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, // Trailing icon for the dropdown
            modifier = Modifier
                .menuAnchor() // Anchor the dropdown menu to the text field
                .fillMaxWidth() // Fill the width of the parent
        )
        ExposedDropdownMenu(
            expanded = expanded, // State for dropdown expansion
            onDismissRequest = { expanded = false } // Callback when dropdown is dismissed
        ) {
            // Loop through options and display them as dropdown items
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
