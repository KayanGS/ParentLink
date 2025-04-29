package com.example.myapplication.presentation.ui.carpooling

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ParticipatingRequestCarpoolingScreen(
    eventRecordId: String,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val userId = auth.currentUser?.uid ?: ""

    val scrollState = rememberScrollState()

    // Load Event + Parent Data
    var eventData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var organizerName by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentSurname by remember { mutableStateOf("") }

    var childName by remember { mutableStateOf("") }
    var childAge by remember { mutableStateOf("") }
    var siblingName by remember { mutableStateOf("") }
    var siblingAge by remember { mutableStateOf("") }
    var specialNeeds by remember { mutableStateOf("No") }
    var specialNeedsDesc by remember { mutableStateOf("") }
    var requestedSeats by remember { mutableStateOf("1") }
    var requestSent by remember { mutableStateOf(false) }
    var requiredReturn by remember { mutableStateOf("Yes") }
    var requiredSeatsReturn by remember { mutableStateOf("1") }

    LaunchedEffect(Unit) {
        db.collection("Posted Carpooling Event Record").document(eventRecordId)
            .get().addOnSuccessListener { doc ->
                eventData = doc.data
                val organizerId = doc.getString("organizerId") ?: ""
                db.collection("Users").document(organizerId)
                    .get().addOnSuccessListener {
                        organizerName = "${it.getString("parentName") ?: ""} ${it.getString("parentSurname") ?: ""}"
                    }
            }

        db.collection("Users").document(userId)
            .get().addOnSuccessListener {
                parentName = it.getString("parentName") ?: ""
                parentSurname = it.getString("parentSurname") ?: ""
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        ScreenHeader(title = "Request to Join Carpooling Event", onLogoutClick = { onBack() })

        Spacer(modifier = Modifier.height(16.dp))

        eventData?.let { data ->
            Text("Carpooling Title: ${data["carpoolingTitle"]}")
            Text("Organising Parent: $organizerName")
            Text("Date: ${data["date"]}")
            Text("Day of Week: ${data["dayOfWeek"]}")
            Text("Pick-up: ${data["pickup"]}")
            Text("Destination: ${data["destination"]}")
            Text("Start Time: ${data["startTime"]}")
            Text("End Time: ${data["endTime"]}")
            Text("Venue: ${data["pickup"]} to ${data["destination"]}")
            val returnYN = data["returnJourney"]
            if (returnYN == "Yes") {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Return Pick-up: ${data["returnPickup"]}")
                Text("Return Destination: ${data["returnDestination"]}")
                Text("Return Start: ${data["returnStartTime"]}")
                Text("Return End: ${data["returnEndTime"]}")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Participating Parent: $parentName $parentSurname")

            Text("Please, enter the following details:", fontSize = 16.sp)
            OutlinedTextField(value = childName, onValueChange = { childName = it }, label = { Text("Child Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = childAge, onValueChange = { childAge = it }, label = { Text("Child Age") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = siblingName, onValueChange = { siblingName = it }, label = { Text("Sibling Name (if any)") }, modifier = Modifier.fillMaxWidth())

            if (siblingName.isNotBlank()) {
                OutlinedTextField(value = siblingAge, onValueChange = { siblingAge = it }, label = { Text("Sibling Age (if any)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Child/Sibling Special Needs (Y/N):")
            Row {
                RadioButton(selected = specialNeeds == "Yes", onClick = { specialNeeds = "Yes" })
                Text("Yes", modifier = Modifier.padding(end = 16.dp))
                RadioButton(selected = specialNeeds == "No", onClick = { specialNeeds = "No" })
                Text("No")
            }

            if (specialNeeds == "Yes") {
                OutlinedTextField(
                    value = specialNeedsDesc,
                    onValueChange = { specialNeedsDesc = it },
                    label = { Text("Special Needs Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Number of Seats Required:")
            Row {
                listOf("1", "2").forEach {
                    RadioButton(selected = requestedSeats == it, onClick = { requestedSeats = it })
                    Text(it, modifier = Modifier.padding(end = 16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Required Return Journey?")
            Row {
                RadioButton(selected = requiredReturn == "Yes", onClick = { requiredReturn = "Yes" })
                Text("Yes", modifier = Modifier.padding(end = 16.dp))
                RadioButton(selected = requiredReturn == "No", onClick = { requiredReturn = "No" })
                Text("No")
            }

            if (requiredReturn == "Yes") {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Number of Seats Required on Return Journey:")
                Row {
                    listOf("1", "2").forEach {
                        RadioButton(selected = requiredSeatsReturn == it, onClick = { requiredSeatsReturn = it })
                        Text(it, modifier = Modifier.padding(end = 16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val now = Date()
                    val requestText = buildString {
                        append("Dear $organizerName,\n")
                        append("I would like to request a place for my child $childName to participate in the carpooling titled \"${data["carpoolingTitle"]}\" scheduled on ${data["date"]} from ${data["pickup"]} to ${data["destination"]}. ")
                        if (siblingName.isNotBlank()) append("$childName has a sibling named $siblingName. ")
                        append("I am requesting $requestedSeats seat(s). ")
                        if (specialNeeds == "Yes") append("Special needs: $specialNeedsDesc. ")
                        append("\nKind regards,\n$parentName $parentSurname")
                    }

                    db.collection("Request for posted carpooling event participation record")
                        .add(
                            mapOf(
                                "participantParentId" to userId,
                                "organizerId" to data["organizerId"],
                                "carpoolingId" to eventRecordId,
                                "weekCommenceDate" to data["weekCommenceDate"],
                                "date" to data["date"],
                                "childName" to childName,
                                "childAge" to childAge,
                                "siblingName" to siblingName.ifBlank { null },
                                "siblingAge" to siblingAge.ifBlank { null },
                                "specialNeeds" to specialNeeds,
                                "specialNeedsDesc" to if (specialNeeds == "Yes") specialNeedsDesc else null,
                                "requestedSeats" to requestedSeats,
                                "status" to "created",
                                "requestDate" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(now),
                                "requestTime" to SimpleDateFormat("HH:mm", Locale.getDefault()).format(now),
                                "requestText" to requestText,
                                "requiredReturnJourney" to requiredReturn,
                                "requiredSeatsReturn" to if (requiredReturn == "Yes") requiredSeatsReturn else "0",

                                )
                        ).addOnSuccessListener {
                            requestSent = true
                            Toast.makeText(context, "Carpooling request sent!", Toast.LENGTH_LONG).show()
                        }
                },
                enabled = childName.isNotBlank() && childAge.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Request to Organising Parent")
            }

            if (requestSent) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Request sent successfully! You can now press the button below to go back.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Close")
                }
            }
        } ?: Text("Loading event details...")
    }
}
