package com.example.myapplication.presentation.ui.organizer

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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ViewPlayDateRequestDetailsScreen(
    requestId: String,
    onBackToList: () -> Unit, // 🔵 back to screen 1.6
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val scrollState = rememberScrollState()

    var requestData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var eventData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var organizerName by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentSurname by remember { mutableStateOf("") }

    // 🔵 For popup control
    var showPopup by remember { mutableStateOf(false) }
    var popupNotificationText by remember { mutableStateOf("") }
    var popupNotificationDate by remember { mutableStateOf("") }
    var popupNotificationTime by remember { mutableStateOf("") }
    var popupIsAcceptance by remember { mutableStateOf(true) }
    var onConfirmAction by remember { mutableStateOf({}) }

    LaunchedEffect(Unit) {
        db.collection("Request for posted play date event participation record").document(requestId)
            .get().addOnSuccessListener { reqDoc ->
                requestData = reqDoc.data
                val eventId = reqDoc.getString("playDateId") ?: ""
                val parentId = reqDoc.getString("participantParentId") ?: ""

                db.collection("Posted Play Date Event Record").document(eventId)
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
            title = "Request Details",
            onLogoutClick = {
                auth.signOut()
                onBackToList()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (requestData != null && eventData != null) {
            val childName = requestData!!["childName"] as String
            val siblingName = requestData!!["siblingName"] as? String ?: ""
            val specialNeeds = requestData!!["specialNeeds"] as? String ?: "No"
            val specialNeedsDesc = requestData!!["specialNeedsDesc"] as? String ?: ""
            val requestedPlaces = (requestData!!["requestedPlaces"] as? String)?.toIntOrNull() ?: 1

            Text("Event Title: ${eventData!!["playDateTitle"]}")
            Text("Organising Parent: $organizerName")
            Text("Max Places: ${eventData!!["maxPlaces"]}")
            Text("Remaining Places: ${eventData!!["remainingPlaces"]}")

            Spacer(modifier = Modifier.height(16.dp))

            val requestText = buildString {
                append("Dear $organizerName,\n")
                append("I would like to place a request for my child $childName to participate in the event \"${eventData!!["playDateTitle"]}\" of category ${eventData!!["playDateCat"]}, type ${eventData!!["playDateType"]} on ${eventData!!["date"]}, starting at ${eventData!!["startTime"]} in ${eventData!!["playDateVenue"]}. ")
                if (siblingName.isNotBlank()) append("$childName has a sibling named $siblingName. ")
                append("My request is for $requestedPlaces place(s). ")
                if (specialNeeds == "Yes") append("My child has the following special needs: $specialNeedsDesc. ")
                append("\n\nKind regards,\n$parentName $parentSurname")
            }

            Text("Message:")
            Text(requestText, modifier = Modifier.padding(8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // 🔵 Accept Button
            Button(onClick = {
                val now = Date()
                val formatterDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formatterTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                val notificationDate = formatterDate.format(now)
                val notificationTime = formatterTime.format(now)

                val notificationText = buildString {
                    append("Dear $parentName $parentSurname,\n")
                    append("Your request for $childName to participate in the event \"${eventData!!["playDateTitle"]}\" of category ${eventData!!["playDateCat"]}, type ${eventData!!["playDateType"]} on ${eventData!!["date"]}, starting at ${eventData!!["startTime"]} in ${eventData!!["playDateVenue"]} for required $requestedPlaces places has been accepted.")
                    if (siblingName.isNotBlank()) append(" We are looking forward to welcome $childName with $siblingName.")
                    append("\n\nWith kind regards,\n$organizerName")
                }

                popupNotificationText = notificationText
                popupNotificationDate = notificationDate
                popupNotificationTime = notificationTime
                popupIsAcceptance = true
                showPopup = true

                onConfirmAction = {
                    // Create notification + update records
                    db.collection("Notification_of_play_date_request_acceptance_rejection")
                        .add(
                            mapOf(
                                "requestId" to requestId,
                                "organizerId" to eventData!!["organizerId"],
                                "participantParentId" to requestData!!["participantParentId"],
                                "playDateId" to requestData!!["playDateId"],
                                "weekCommenceDate" to eventData!!["weekCommenceDate"],
                                "date" to eventData!!["date"],
                                "status" to "accepted",
                                "notificationDate" to notificationDate,
                                "notificationTime" to notificationTime,
                                "notificationText" to notificationText
                            )
                        )

                    db.collection("Request for posted play date event participation record").document(requestId)
                        .update("status", "accepted")

                    val remaining = (eventData!!["remainingPlaces"] as? String)?.toIntOrNull() ?: 0
                    val updatedRemaining = (remaining - requestedPlaces).coerceAtLeast(0)

                    db.collection("Posted Play Date Event Record").document(requestData!!["playDateId"] as String)
                        .update("remainingPlaces", updatedRemaining.toString())
                }
            }) {
                Text("Accept Request")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔵 Reject Button
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
                        append("We regret to inform you that your request for $childName to participate in the event \"${eventData!!["playDateTitle"]}\" of category ${eventData!!["playDateCat"]}, type ${eventData!!["playDateType"]} on ${eventData!!["date"]}, starting at ${eventData!!["startTime"]} in ${eventData!!["playDateVenue"]} has been rejected.")
                        if (siblingName.isNotBlank()) append(" This includes sibling $siblingName.")
                        append("\n\nWith kind regards,\n$organizerName")
                    }

                    popupNotificationText = notificationText
                    popupNotificationDate = notificationDate
                    popupNotificationTime = notificationTime
                    popupIsAcceptance = false
                    showPopup = true

                    onConfirmAction = {
                        db.collection("Notification_of_play_date_request_acceptance_rejection")
                            .add(
                                mapOf(
                                    "requestId" to requestId,
                                    "organizerId" to eventData!!["organizerId"],
                                    "participantParentId" to requestData!!["participantParentId"],
                                    "playDateId" to requestData!!["playDateId"],
                                    "weekCommenceDate" to eventData!!["weekCommenceDate"],
                                    "date" to eventData!!["date"],
                                    "status" to "rejected",
                                    "notificationDate" to notificationDate,
                                    "notificationTime" to notificationTime,
                                    "notificationText" to notificationText
                                )
                            )

                        db.collection("Request for posted play date event participation record").document(requestId)
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

    // 🔵 Popup showing
    if (showPopup) {
        NotificationRequestPopup(
            notificationText = popupNotificationText,
            notificationDate = popupNotificationDate,
            notificationTime = popupNotificationTime,
            isAcceptance = popupIsAcceptance,
            onConfirmSendNotification = {
                onConfirmAction()
            },
            onClose = {
                showPopup = false
                onBackToList() // after close go back to list
            }
        )
    }
}
