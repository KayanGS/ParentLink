package com.example.myapplication.presentation.ui.playdate

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun ParticipatingRequestPlayDateScreen(
    eventRecordId: String,
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val userId = auth.currentUser?.uid ?: ""

    // Form State
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
    var requestedPlaces by remember { mutableStateOf("1") }
    var requestSent by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Load event + parent details
    LaunchedEffect(Unit) {
        db.collection("Posted Play Date Event Record").document(eventRecordId)
            .get().addOnSuccessListener { doc ->
                eventData = doc.data
                val orgId = doc.getString("organizerId") ?: ""
                db.collection("Users").document(orgId)
                    .get().addOnSuccessListener {
                        organizerName = "${it.getString("parentName") ?: ""} ${it.getString("parentSurname") ?: ""}"
                    }
            }

        db.collection("Users").document(userId)
            .get().addOnSuccessListener { doc ->
                parentName = doc.getString("parentName") ?: ""
                parentSurname = doc.getString("parentSurname") ?: ""
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        ScreenHeader(title = "Request for a Child to Participate in a Selected Play Date", onLogoutClick = { onBack() })

        Spacer(modifier = Modifier.height(16.dp))

        eventData?.let { data ->
            Text("Play Date Title: ${data["playDateTitle"]}")
            Text("Organising Parent: $organizerName")
            Text("Category: ${data["playDateCat"]}")
            Text("Type: ${data["playDateType"]}")
            Text("Date: ${data["date"]}")
            Text("Day of Week: ${data["dayOfWeek"]}")
            Text("Start Time: ${data["startTime"]}")
            Text("End Time: ${data["endTime"]}")
            Text("Venue: ${data["playDateVenue"]}")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Participating Child Parent Name: $parentName")
            Text("Participating Child Parent Surname: $parentSurname")

            Spacer(modifier = Modifier.height(16.dp))
            Text("Please, enter the following details:", fontSize = 16.sp)

            OutlinedTextField(value = childName, onValueChange = { childName = it }, label = { Text("Child Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = childAge, onValueChange = { childAge = it }, label = { Text("Child Age") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = siblingName, onValueChange = { siblingName = it }, label = { Text("Sibling Name (if participating)") }, modifier = Modifier.fillMaxWidth())

            if (siblingName.isNotBlank()) {
                OutlinedTextField(value = siblingAge, onValueChange = { siblingAge = it }, label = { Text("Sibling Age (if participating)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            }

            Text("Child/Sibling Special Needs (Y/N):")
            Row {
                RadioButton(selected = specialNeeds == "Yes", onClick = { specialNeeds = "Yes" })
                Text("Yes", modifier = Modifier.padding(end = 16.dp))
                RadioButton(selected = specialNeeds == "No", onClick = { specialNeeds = "No" })
                Text("No")
            }

            if (specialNeeds == "Yes") {
                OutlinedTextField(value = specialNeedsDesc, onValueChange = { specialNeedsDesc = it }, label = { Text("Special Needs Description") }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Number of Places Required:")
            Row {
                listOf("1", "2").forEach {
                    RadioButton(selected = requestedPlaces == it, onClick = { requestedPlaces = it })
                    Text(it, modifier = Modifier.padding(end = 16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val now = Date()
                    val requestText = buildString {
                        append("Dear $organizerName,\n")
                        append("I would like to place a request for my child $childName to participate in the event \"${data["playDateTitle"]}\" of category ${data["playDateCat"]}, type ${data["playDateType"]} on ${data["date"]}, starting at ${data["startTime"]} in ${data["playDateVenue"]}. ")
                        if (siblingName.isNotBlank()) append("$childName has a sibling named $siblingName. ")
                        append("My request is for $requestedPlaces place(s). ")
                        if (specialNeeds == "Yes") append("My child has the following special needs: $specialNeedsDesc. ")
                        append("\n\nKind regards,\n$parentName $parentSurname")
                    }

                    db.collection("Request for posted play date event participation record")
                        .add(
                            mapOf(
                                "participantParentId" to userId,
                                "organizerId" to data["organizerId"],
                                "playDateId" to eventRecordId,
                                "weekCommenceDate" to data["weekCommenceDate"],
                                "date" to data["date"],
                                "childName" to childName,
                                "childAge" to childAge,
                                "siblingName" to siblingName.ifBlank { null },
                                "siblingAge" to siblingAge.ifBlank { null },
                                "specialNeeds" to specialNeeds,
                                "specialNeedsDesc" to if (specialNeeds == "Yes") specialNeedsDesc else null,
                                "requestedPlaces" to requestedPlaces,
                                "status" to "created",
                                "requestDate" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(now),
                                "requestTime" to SimpleDateFormat("HH:mm", Locale.getDefault()).format(now),
                                "requestText" to requestText
                            )
                        ).addOnSuccessListener {
                            requestSent = true
                            Toast.makeText(context, "Request sent!", Toast.LENGTH_LONG).show()
                        }
                },
                enabled = childName.isNotBlank() && childAge.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create and Send Request to Event Organising Parent")
            }

            if (requestSent) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Request for child's participation has been successfully created. You can now press the button below to go back or view another event.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Close")
                }
            }
        } ?: Text("Loading event details...")
    }
}
