package com.example.myapplication.presentation.ui.organizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationRequestPopup(
    notificationText: String,
    notificationDate: String,
    notificationTime: String,
    isAcceptance: Boolean,
    onConfirmSendNotification: () -> Unit,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()
    var isConfirmed by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onClose() },
        confirmButton = {},
        title = {
            Text(
                if (isAcceptance)
                    "Notification of Request Acceptance"
                else
                    "Notification of Request Rejection"
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(text = "Notification Message:")
                Spacer(modifier = Modifier.height(8.dp))
                Text(notificationText)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Notification Date: $notificationDate")
                Text("Notification Time: $notificationTime")

                Spacer(modifier = Modifier.height(16.dp))

                if (!isConfirmed) {
                    Button(
                        onClick = {
                            onConfirmSendNotification()
                            isConfirmed = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isAcceptance)
                                "Send Notification of Request Acceptance to Parent"
                            else
                                "Send Notification of Request Rejection to Parent"
                        )
                    }
                } else {
                    Text(
                        if (isAcceptance)
                            "Notification record of request acceptance has been successfully created and sent."
                        else
                            "Notification record of request rejection has been successfully created and sent."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    )
}
