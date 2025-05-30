// ViewCarpoolingRequestDetailsScreen.kt
package com.example.myapplication.presentation.ui.carpooling

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ViewCarpoolingRequestDetailsScreen(
    requestId: String,
    onBackToList: () -> Unit,
    onLogout: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val scrollState = rememberScrollState()

    var requestData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var eventData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var organizerName by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentSurname by remember { mutableStateOf("") }

    // Popup control
    var showPopup by remember { mutableStateOf(false) }
    var popupNotificationText by remember { mutableStateOf("") }
    var popupNotificationDate by remember { mutableStateOf("") }
    var popupNotificationTime by remember { mutableStateOf("") }
    var popupIsAcceptance by remember { mutableStateOf(true) }
    var onConfirmAction by remember { mutableStateOf({}) }

    // Load request + parent + event data
    LaunchedEffect(Unit) {
        db.collection("Request for posted carpooling event participation record").document(requestId)
            .get().addOnSuccessListener { reqDoc ->
                requestData = reqDoc.data
                val eventId = reqDoc.getString("carpoolingId") ?: ""
                val parentId = reqDoc.getString("participantParentId") ?: ""

                db.collection("Posted Carpooling Event Record").document(eventId)
                    .get().addOnSuccessListener { eventDoc ->
                        eventData = eventDoc.data
                        val organizerId = eventDoc.getString("organizerId") ?: ""
                        db.collection("Users").document(organizerId)
                            .get().addOnSuccessListener {
                                organizerName = "${it.getString("parentName") ?: ""} ${it.getString("parentSurname") ?: ""}"
                            }
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
            title = "Carpooling Request Details",
            onLogoutClick = {
                auth.signOut()
                onLogout()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (requestData != null && eventData != null) {
            val childName = requestData!!["childName"] as String
            val siblingName = requestData!!["siblingName"] as? String ?: ""
            val specialNeeds = requestData!!["specialNeeds"] as? String ?: "No"
            val specialNeedsDesc = requestData!!["specialNeedsDesc"] as? String ?: ""
            val requestedPlaces = (requestData!!["requestedSeats"] as? String)?.toIntOrNull() ?: 1
            val returnRequired = requestData!!["requiredReturnJourney"] as? String ?: "No"
            val requestedReturnSeats = (requestData!!["requiredSeatsReturn"] as? String)?.toIntOrNull()


            Text("Carpooling Title: ${eventData!!["carpoolingTitle"]}")
            Text("Organising Parent: $organizerName")
            Text("Max Seats: ${eventData!!["maxSeats"]}")
            Text("Remaining Seats: ${eventData!!["remainingSeats"]}")

            if (eventData!!["returnJourney"] == "Yes") {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Return Journey Available: Yes")
                Text("Return Max Seats: ${eventData!!["maxSeats"]}")
                Text("Return Remaining Seats: ${eventData!!["remainingSeats"]}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔵 Message Creation
            val requestText = buildString {
                append("Dear $organizerName,\n")
                append("I would like to place a request for my child $childName to participate " +
                        "in the carpooling event \"${eventData!!["carpoolingTitle"]}\" from " +
                        "${eventData!!["pickup"]} to ${eventData!!["destination"]} on " +
                        "${eventData!!["date"]}.")
                append(" My request is for $requestedPlaces seat(s).")
                if (returnRequired == "Yes" && requestedReturnSeats != null) {
                    append(" Return journey also required for $requestedReturnSeats seat(s).")
                }

                if (siblingName.isNotBlank()) append(" $childName has a sibling $siblingName.")
                if (specialNeeds == "Yes") append(" Special needs: $specialNeedsDesc.")
                append("\n\nKind regards,\n$parentName $parentSurname")
            }

            Text("Request Message:")
            Text(requestText, modifier = Modifier.padding(8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Accept button
            Button(onClick = {
                val now = Date()
                val formatterDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formatterTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                val notificationDate = formatterDate.format(now)
                val notificationTime = formatterTime.format(now)

                val notificationText = buildString {
                    append("Dear $parentName $parentSurname,\n")
                    append("Your request for $childName to participate in the carpooling event " +
                            "\"${eventData!!["carpoolingTitle"]}\" on ${eventData!!["date"]}, " +
                            "starting at ${eventData!!["startTime"]}, with pick up point " +
                            "${eventData!!["pickup"]} to destination point " +
                            "${eventData!!["destination"]} for required $requestedPlaces seat(s) " +
                            "on the event, ")

                    if (returnRequired == "No") { append (" have been accepted")}
                    if (returnRequired == "Yes" && (requestedReturnSeats ?: 0) > 0) {
                        val returnStartTime = eventData!!["returnStartTime"] as? String ?: "[Return Time Missing]"
                        append(" together with the Request for Return Journey starting at " +
                                "$returnStartTime for required $requestedReturnSeats seat(s) have been accepted.")
                    }
                    append(" We are looking forward to welcome $childName on the event")

                    if (siblingName.isNotBlank()) {
                        append(" with $siblingName")
                    }

                    append(".\n\nWith kind regards,\n$organizerName")
                }


                popupNotificationText = notificationText
                popupNotificationDate = notificationDate
                popupNotificationTime = notificationTime
                popupIsAcceptance = true
                showPopup = true

                onConfirmAction = {
                    val carpoolingId = requestData!!["carpoolingId"] as String
                    val requestedSeats = (requestData!!["requestedSeats"] as? String)?.toIntOrNull() ?: 1

                    db.collection("Notification_of_carpooling_request_acceptance_rejection")
                        .add(
                            mapOf(
                                "requestId" to requestId,
                                "organizerId" to eventData!!["organizerId"],
                                "participantParentId" to requestData!!["participantParentId"],
                                "carpoolingId" to carpoolingId,
                                "weekCommenceDate" to eventData!!["weekCommenceDate"],
                                "date" to eventData!!["date"],
                                "status" to "accepted",
                                "notificationDate" to notificationDate,
                                "notificationTime" to notificationTime,
                                "notificationText" to notificationText
                            )
                        )

                    db.collection("Request for posted carpooling event participation record")
                        .document(requestId)
                        .update("status", "accepted")

                    // Reduce remaining carpooling seats
                    val remaining = (eventData!!["remainingSeats"] as? String)?.toIntOrNull() ?: 0
                    val updatedRemaining = (remaining - requestedSeats).coerceAtLeast(0)

                    db.collection("Posted Carpooling Event Record")
                        .document(carpoolingId)
                        .update("remainingSeats", updatedRemaining.toString())
                }

            }) {
                Text("Accept Request")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reject button
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                onClick = {
                    val now = Date()
                    val formatterDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formatterTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val notificationDate = formatterDate.format(now)
                    val notificationTime = formatterTime.format(now)

                    val notificationText = buildString {
                        append("Dear $parentName $parentSurname,\n")
                        append("Your request for $childName to participate in the carpooling " +
                                "event \"${eventData!!["carpoolingTitle"]}\" on " +
                                "${eventData!!["date"]}, starting at ${eventData!!["startTime"]}" +
                                ", with pick up point ${eventData!!["pickup"]} to destination " +
                                "point ${eventData!!["destination"]} for required " +
                                "$requestedPlaces seat(s) on the event, " )

                        if (returnRequired == "Yes" && (requestedReturnSeats ?: 0) > 0) {
                            val returnStartTime = eventData!!["returnStartTime"] as? String ?: "[Return Time Missing]"
                            append("and Return Journey starting at $returnStartTime for required " +
                                    "$requestedReturnSeats ")
                        }

                        append("regrettably have been rejected entirely for lack of available " +
                                "seats.\n")

                        append("However we hope to welcome $childName")

                        if (siblingName.isNotBlank()) {
                            append(" with $siblingName")
                        }

                        append(" to another carpooling event that we organise")

                        if (returnRequired == "No") { append (" have been accepted")}

                        append(".\n\nWith kind regards,\n$organizerName")

                    }

                    popupNotificationText = notificationText
                    popupNotificationDate = notificationDate
                    popupNotificationTime = notificationTime
                    popupIsAcceptance = false
                    showPopup = true

                    onConfirmAction = {
                        db.collection("Notification_of_carpooling_request_acceptance_rejection")
                            .add(
                                mapOf(
                                    "requestId" to requestId,
                                    "organizerId" to eventData!!["organizerId"],
                                    "participantParentId" to requestData!!["participantParentId"],
                                    "carpoolingId" to requestData!!["carpoolingId"],
                                    "weekCommenceDate" to eventData!!["weekCommenceDate"],
                                    "date" to eventData!!["date"],
                                    "status" to "rejected",
                                    "notificationDate" to notificationDate,
                                    "notificationTime" to notificationTime,
                                    "notificationText" to notificationText
                                )
                            )

                        db.collection("Request for posted carpooling event participation record").document(requestId)
                            .update("status", "rejected")
                    }
                }
            ) {
                Text("Reject Request")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onBackToList) {
                Text("Back")
            }
        } else {
            Text("Loading request details...")
        }
    }

    if (showPopup) {
        NotificationRequestPopupCarpooling(
            notificationText = popupNotificationText,
            notificationDate = popupNotificationDate,
            notificationTime = popupNotificationTime,
            isAcceptance = popupIsAcceptance,
            onConfirmSendNotification = {
                onConfirmAction()
            },
            onClose = {
                showPopup = false
                onBackToList()
            }
        )
    }
}
