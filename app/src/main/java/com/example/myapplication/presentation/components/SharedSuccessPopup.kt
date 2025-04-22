package com.example.myapplication.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp

@Composable
fun SharedSuccessPopup(
    visible: Boolean,
    message: String,
    onAnother: () -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {}
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {},
            title = { Text("Success") },
            text = {
                Column(modifier = modifier) {
                    Text(message)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onAnother) {
                        Text("Another")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onBackToDashboard) {
                        Text("Back to Dashboard Screen")
                    }
                }
            }
        )
    }
}
