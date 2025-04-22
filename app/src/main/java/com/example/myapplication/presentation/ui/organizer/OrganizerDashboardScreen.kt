package com.example.myapplication.presentation.ui.organizer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OrganizerDashboardScreen(
    onLogout: () -> Unit = {},
    onPlayDateCreate: () -> Unit = {},
    onCarpoolingCreate: () -> Unit = {},
    onViewPlayDateRequests: () -> Unit = {},
    onViewCarpoolingRequests: () -> Unit = {},
    onViewFeedback: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        ScreenHeader(
            title = "Organizer Dashboard Screen",
            onLogoutClick = {
                auth.signOut()
                onLogout()
            }
        )

        Button(
            onClick = onPlayDateCreate,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)

        ) {
            Text("Create a Play Date Event Record")
        }

        Button(
            onClick = onCarpoolingCreate,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text("Create a Carpooling Event Record")
        }

        Button(
            onClick = onViewPlayDateRequests,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text("View Requests from Parents (Play Date)")
        }

        Button(
            onClick = onViewCarpoolingRequests,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text("View Requests from Parents (Carpooling)")
        }

        Button(
            onClick = onViewFeedback,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("View Feedback and Ratings")
        }
    }
}
