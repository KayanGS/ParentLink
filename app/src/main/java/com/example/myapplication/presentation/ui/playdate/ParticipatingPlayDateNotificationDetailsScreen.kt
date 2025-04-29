// ParticipatingPlayDateNotificationDetailsScreen.kt
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
fun ParticipatingPlayDateNotificationDetailsScreen(
    notificationId: String,
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val scrollState = rememberScrollState()

    var notificationData by remember { mutableStateOf<Map<String, Any>?>(null) }

    LaunchedEffect(Unit) {
        db.collection("Notification_of_play_date_request_acceptance_rejection").document(notificationId)
            .get().addOnSuccessListener { doc ->
                notificationData = doc.data
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

        if (notificationData != null) {
            Text("Notification Date: ${notificationData!!["notificationDate"]}")
            Text("Notification Time: ${notificationData!!["notificationTime"]}")
            Text("Status: ${notificationData!!["status"]}")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Message:")
            Spacer(modifier = Modifier.height(8.dp))
            Text(notificationData!!["notificationText"].toString())

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onBack) {
                Text("Back")
            }
        } else {
            Text("Loading notification details...")
        }
    }
}
