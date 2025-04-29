package com.example.myapplication.presentation.ui.playdate

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ParticipatingPlayDateDetailScreen(
    eventRecordId: String,
    organizerId: String,
    onBack: () -> Unit,
    onRequestClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var eventData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var organizerName by remember { mutableStateOf("") }

    // Fetch event data
    LaunchedEffect(eventRecordId) {
        db.collection("Posted Play Date Event Record").document(eventRecordId)
            .get().addOnSuccessListener { doc ->
                eventData = doc.data
            }

        db.collection("Users").document(organizerId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val name = doc.getString("parentName") ?: "[No Name]"
                    val surname = doc.getString("parentSurname") ?: "[No Surname]"
                    organizerName = "$name $surname"
                } else {
                    organizerName = "[User not found]"
                }
            }
            .addOnFailureListener {
                organizerName = "[Error loading name]"
            }

    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        ScreenHeader(title = "Selected Play Date Event Details", onLogoutClick = {
            auth.signOut()
            onBack()
        })

        Spacer(modifier = Modifier.height(16.dp))

        eventData?.let { data ->
            Text("Title: ${data["playDateTitle"]}")
            Text("Organising Parent: $organizerName")
            Text("Category: ${data["playDateCat"]}")
            Text("Type: ${data["playDateType"]}")
            Text("Date: ${data["date"]}")
            Text("Day of Week: ${data["dayOfWeek"]}")
            Text("Start Time: ${data["startTime"]}")
            Text("End Time: ${data["endTime"]}")
            Text("Venue: ${data["playDateVenue"]}")
            Text("Max Places: ${data["maxPlaces"]}")
            Text("Remaining Places: ${data["remainingPlaces"]}")

            val specialReq = data["specialReq"] as? String
            if (!specialReq.isNullOrBlank()) {
                Text("Special Requirements: $specialReq")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRequestClick, modifier = Modifier.fillMaxWidth()) {
                Text("Request to Participate")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to List of Play Dates")
            }
        } ?: Text("Loading event details...")
    }
}
