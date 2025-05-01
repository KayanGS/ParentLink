package com.example.myapplication.presentation.ui.carpooling

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.components.ScreenHeader
import com.example.myapplication.presentation.components.SharedWeekCommencingField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ParticipatingViewCarpoolingScreen(
    onLogout: () -> Unit,
    onBackToDashboard: () -> Unit,
    onViewEventDetails: (Map<String, Any>) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var selectedWeek by remember { mutableStateOf("") }
    var events by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var selectedTitle by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = "View List of Carpooling Events",
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
                db.collection("Posted Carpooling Event Record")
                    .whereEqualTo("weekCommenceDate", it)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        events = snapshot.documents.mapNotNull { doc ->
                            doc.data?.toMutableMap()?.apply {
                                this["id"] = doc.id
                            }
                        }.sortedWith(
                            compareByDescending<Map<String, Any>> {
                                it["postedDate"] as? String ?: ""
                            }.thenByDescending {
                                it["postedTime"] as? String ?: ""
                            }
                        )
                    }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select a Carpooling Event from the List",
            style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        events.forEach { event ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation()
            ) {
                Column(Modifier.padding(8.dp)) {
                    Text("Title: ${event["carpoolingTitle"]}")
                    Text("Posted on: ${event["postedDate"]} at ${event["postedTime"]}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = {
                        selectedTitle = event["carpoolingTitle"] as String
                        onViewEventDetails(event)
                    }) {
                        Text("View")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                selectedTitle = ""
                selectedWeek = ""
                events = emptyList()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Select Another Carpooling Event to View")
        }

        Button(
            onClick = onBackToDashboard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Dashboard")
        }
    }
}
