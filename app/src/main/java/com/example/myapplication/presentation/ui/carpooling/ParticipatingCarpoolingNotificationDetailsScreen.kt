// ParticipatingCarpoolingNotificationDetailsScreen.kt
package com.example.myapplication.presentation.ui.participating

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
fun ParticipatingCarpoolingNotificationDetailsScreen(
    notificationId: String,
    onBack: () -> Unit,
    onLogout: () -> Unit
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
        db.collection("Notification_of_carpooling_request_acceptance_rejection").document(notificationId)
            .get().addOnSuccessListener { doc ->
                notificationData = doc.data

                val carpoolingId = doc.getString("carpoolingId") ?: ""
                val organizerId = doc.getString("organizerId") ?: ""
                val parentId = doc.getString("participantParentId") ?: ""

                db.collection("Posted Carpooling Event Record").document(carpoolingId)
                    .get().addOnSuccessListener { eventDoc ->
                        eventData = eventDoc.data
                    }

                db.collection("Users").document(organizerId)
                    .get().addOnSuccessListener {
                        organizerName = "${it.getString("parentName") ?: ""} ${it.getString("parentSurname") ?: ""}"
                    }

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
                onLogout()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (notificationData != null && eventData != null) {
            val title = eventData!!["carpoolingTitle"] as? String ?: ""
            val status = notificationData!!["status"] as? String ?: ""
            val notificationDate = notificationData!!["notificationDate"] as? String ?: ""
            val notificationTime = notificationData!!["notificationTime"] as? String ?: ""
            val message = notificationData!!["notificationText"] as? String ?: ""

            Text("Carpooling Event Title: $title")
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
