package com.example.myapplication.presentation.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun WelcomeScreen(
    onOrganizerLoginClick: () -> Unit = {},
    onParticipantLoginClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.parentlink_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .height(120.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Welcome to ParentLink!",
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onOrganizerLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("I'm Organizing an Event")
        }

        Button(
            onClick = onParticipantLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I'm Joining an Event")
        }
    }
}
