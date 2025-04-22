package com.example.myapplication.presentation.ui.participating

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun ParticipatingDashboardScreen(
    onLogout: () -> Unit,
    onPlayDateClick: () -> Unit,
    onCarpoolingClick: () -> Unit,
    onPlayDateNotificationsClick: () -> Unit,
    onCarpoolingNotificationsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.parentlink_logo),
            contentDescription = "Logo",
            modifier = Modifier.height(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Logout
        Text(
            text = "Logout",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onLogout() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Screen Title
        Text("Events Participating Child's Parent Dashboard", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // Posted Play Date Events
        Button(onClick = onPlayDateClick, modifier = Modifier.fillMaxWidth()) {
            Text("Posted Play Date Events")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Posted Carpooling Events
        Button(onClick = onCarpoolingClick, modifier = Modifier.fillMaxWidth()) {
            Text("Posted Carpooling Events")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Play Date Notifications
        Button(onClick = onPlayDateNotificationsClick, modifier = Modifier.fillMaxWidth()) {
            Text("Play Date Event Notifications")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Carpooling Notifications
        Button(onClick = onCarpoolingNotificationsClick, modifier = Modifier.fillMaxWidth()) {
            Text("Carpooling Event Notifications")
        }
    }
}
