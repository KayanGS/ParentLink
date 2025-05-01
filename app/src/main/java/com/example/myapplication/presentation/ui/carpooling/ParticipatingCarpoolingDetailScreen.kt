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
    onLogout: () -> Unit,
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

        ScreenHeader(title = "Selected Carpooling Event Details",
            onLogoutClick = {
            auth.signOut()
            onLogout()
        })

        Spacer(modifier = Modifier.height(16.dp))

        eventData?.let { data ->
            Text("Carpooling Event Title: ${data["carpoolingTitle"]}")
            Text("Organising Parent: $organizerName")
            Text("Age Group Suitability: ${data["ageGroup"]}")
            Text("Carpooling Event Date: ${data["date"]}")
            Text("Carpooling Day of the Week: ${data["dayOfWeek"]}")
            Text("Carpooling Event Start Time: ${data["startTime"]}")
            Text("Carpooling Event Pick-up Point: ${data["pickup"]}")
            Text("Carpooling Event Destination Point: ${data["destination"]}")
            Text("Maximum Number of Seats Available: ${data["maxSeats"]}")
            Text("Remaining Number of Seats Available: ${data["remainingSeats"]}")

            val returnJourney = data["returnJourney"] as? String
            if (returnJourney == "Yes") {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Carpooling Event Return Journey Pick-up Point: ${data["returnPickup"]}")
                Text("Carpooling Event Return Journey Start Time: ${data["returnStartTime"]}")
            }

            val specialReq = data["specialRequirements"] as? String
            if (!specialReq.isNullOrBlank()) {
                Text("Carpooling Event Special Requirements: $specialReq")
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
