package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Logo and Logout Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.parentlink_logo),
                contentDescription = "Logo",
                modifier = Modifier.height(60.dp)
            )
            Button(onClick = {
                auth.signOut()
                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                onLogout()
            }) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Organizing Parent Dashboard",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
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
