package com.example.myapplication.presentation.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun SharedSpecialRequirementsField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Special Requirements (Optional)") },
        modifier = Modifier.fillMaxWidth()
    )
}
