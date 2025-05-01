package com.example.myapplication.presentation.ui.playdate

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
fun ViewPlayDateRequestsScreen(
    navController: NavController,
    onLogout: () -> Unit,
    onBackToDashboard: () -> Unit
)
 {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val organizerId = auth.currentUser?.uid ?: ""

    var selectedWeek by remember { mutableStateOf("") }
    var requests by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = "View Play Date Participation Requests",
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
                db.collection("Request for posted play date event participation record")
                    .whereEqualTo("organizerId", organizerId)
                    .whereEqualTo("weekCommenceDate", it)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        requests = snapshot.documents.mapNotNull { doc ->
                            doc.data?.toMutableMap()?.apply {
                                this["id"] = doc.id
                            }
                        }.sortedWith(
                            compareByDescending<Map<String, Any>> {
                                it["requestDate"] as? String ?: ""
                            }.thenByDescending {
                                it["requestTime"] as? String ?: ""
                            }
                        )
                    }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Play Date Participation Requests", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        requests.forEach { req ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation()
            ) {
                Column(Modifier.padding(8.dp)) {
                    Text("Child: ${req["childName"]} (Age: ${req["childAge"]})")
                    Text("Request Date: ${req["requestDate"]} at ${req["requestTime"]}")
                    Text("Event Title: ${req["requestText"].toString().substringAfter("event \"").substringBefore("\"")}")
                    Text("Status: ${req["status"]}")
                    // 🔽 ADD THIS VIEW BUTTON HERE
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val requestId = req["id"] as? String ?: ""
                        // Navigate to detailed request screen
                        navController.navigate("viewPlayDateRequestDetails/$requestId")
                    }) {
                        Text("View")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                selectedWeek = ""
                requests = emptyList()
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
