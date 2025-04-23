package com.example.myapplication.presentation.ui.participating

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ParticipatingPlayDateDetailScreen(
    eventRecordId: String,
    organizerId: String,
    onBack: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Event ID: $eventRecordId")
        Text("Organizer ID: $organizerId")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
