// ParticipatingPlayDateNotificationsScreen.kt
package com.example.myapplication.presentation.ui.participating

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.presentation.components.ScreenHeader
import com.example.myapplication.presentation.components.SharedWeekCommencingField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ParticipatingPlayDateNotificationsScreen(
    navController: NavController,
    onLogout: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val parentId = auth.currentUser?.uid ?: ""

    var selectedWeek by remember { mutableStateOf("") }
    var notifications by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = "Play Date Notifications",
            onLogoutClick = {
                auth.signOut()
                onLogout()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SharedWeekCommencingField(
            selectedValue = selectedWeek,
            onValueChange = {
                selectedWeek = it
                db.collection("Notification_of_play_date_request_acceptance_rejection")
                    .whereEqualTo("participantParentId", parentId)
                    .whereEqualTo("weekCommenceDate", it)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        notifications = snapshot.documents.mapNotNull { doc ->
                            doc.data?.toMutableMap()?.apply {
                                this["id"] = doc.id
                            }
                        }
                    }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Received Notifications", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        notifications.forEach { notif ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation()
            ) {
                Column(Modifier.padding(8.dp)) {
                    Text("Notification Date: ${notif["notificationDate"]}")
                    Text("Notification Time: ${notif["notificationTime"]}")
                    Text("Status: ${notif["status"]}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        val notificationId = notif["id"] as? String ?: ""
                        navController.navigate("viewPlayDateNotificationDetails/$notificationId")
                    }) {
                        Text("View Message")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                selectedWeek = ""
                notifications = emptyList()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Select Another Week")
        }

        Button(
            onClick = onBackToDashboard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Dashboard")
        }
    }
}
