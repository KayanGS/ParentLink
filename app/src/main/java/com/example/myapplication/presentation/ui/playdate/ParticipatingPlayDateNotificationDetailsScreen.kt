// ParticipatingPlayDateNotificationDetailsScreen.kt
package com.example.myapplication.presentation.ui.playdate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ParticipatingPlayDateNotificationDetailsScreen(
    notificationId: String,
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val scrollState = rememberScrollState()

    var notificationData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var eventData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var organizerName by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentSurname by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        db.collection("Notification_of_play_date_request_acceptance_rejection").document(notificationId)
            .get().addOnSuccessListener { doc ->
                notificationData = doc.data

                val playDateId = doc.getString("playDateId") ?: ""
                val organizerId = doc.getString("organizerId") ?: ""
                val parentId = doc.getString("participantParentId") ?: ""

                // Load event data
                db.collection("Posted Play Date Event Record").document(playDateId)
                    .get().addOnSuccessListener { eventDoc ->
                        eventData = eventDoc.data
                    }

                // Load organizer name
                db.collection("Users").document(organizerId)
                    .get().addOnSuccessListener {
                        organizerName = "${it.getString("parentName") ?: ""} ${it.getString("parentSurname") ?: ""}"
                    }

                // Load participant parent name
                db.collection("Users").document(parentId)
                    .get().addOnSuccessListener {
                        parentName = it.getString("parentName") ?: ""
                        parentSurname = it.getString("parentSurname") ?: ""
                    }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = "Notification Details",
            onLogoutClick = {
                auth.signOut()
                onBack()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (notificationData != null && eventData != null) {
            val playDateTitle = eventData!!["playDateTitle"] as? String ?: ""
            val playDateCat = eventData!!["playDateCat"] as? String ?: ""
            val playDateType = eventData!!["playDateType"] as? String ?: ""
            val status = notificationData!!["status"] as? String ?: ""
            val notificationDate = notificationData!!["notificationDate"] as? String ?: ""
            val notificationTime = notificationData!!["notificationTime"] as? String ?: ""
            val message = notificationData!!["notificationText"] as? String ?: ""

            Text("Play Date Event Title: $playDateTitle")
            Text("Play Date Event Category: $playDateCat")
            Text("Play Date Event Type: $playDateType")
            Text("Organising Parent: $organizerName")
            Text("Requesting Parent: $parentName $parentSurname")
            Text("Request Status: $status")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Message:")
            Spacer(modifier = Modifier.height(8.dp))
            Text(message)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Notification Date: $notificationDate")
            Text("Notification Time: $notificationTime")

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onBack) {
                Text("Back")
            }
        } else {
            Text("Loading notification details...")
        }
    }
}
