package com.example.myapplication.presentation.ui.carpooling

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ParticipatingCarpoolingDetailScreen(
    eventRecordId: String,
    organizerId: String,
    onBack: () -> Unit,
    onRequestClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var eventData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var organizerName by remember { mutableStateOf("") }

    // Load event + organizer data
    LaunchedEffect(eventRecordId) {
        db.collection("Posted Carpooling Event Record").document(eventRecordId)
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

        ScreenHeader(title = "Selected Carpooling Event Details", onLogoutClick = {
            auth.signOut()
            onBack()
        })

        Spacer(modifier = Modifier.height(16.dp))

        eventData?.let { data ->
            Text("Title: ${data["carpoolingTitle"]}")
            Text("Organising Parent: $organizerName")
            Text("Date: ${data["date"]}")
            Text("Day of Week: ${data["dayOfWeek"]}")
            Text("Pick-up: ${data["pickup"]}")
            Text("Destination: ${data["destination"]}")
            Text("Start Time: ${data["startTime"]}")
            Text("End Time: ${data["endTime"]}")
            Text("Max Seats: ${data["maxSeats"]}")
            Text("Remaining Seats: ${data["remainingSeats"]}")
            Text("Age Group: ${data["ageGroup"]}")

            val specialReq = data["specialRequirements"] as? String
            if (!specialReq.isNullOrBlank()) {
                Text("Special Requirements: $specialReq")
            }

            val returnJourney = data["returnJourney"] as? String
            if (returnJourney == "Yes") {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Return Pick-up: ${data["returnPickup"]}")
                Text("Return Destination: ${data["returnDestination"]}")
                Text("Return Start Time: ${data["returnStartTime"]}")
                Text("Return End Time: ${data["returnEndTime"]}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRequestClick, modifier = Modifier.fillMaxWidth()) {
                Text("Request to Participate")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Back to List of Carpooling Events")
            }
        } ?: Text("Loading event details...")
    }
}
