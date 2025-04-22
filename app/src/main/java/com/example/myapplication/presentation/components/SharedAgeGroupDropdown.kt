package com.example.myapplication.presentation.components

import androidx.compose.runtime.Composable

@Composable
fun SharedAgeGroupDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    SharedDropdownField(
        label = "Age Group Suitability",
        options = listOf("4-6", "7-11"),
        selected = selected,
        onSelected = onSelected
    )
}