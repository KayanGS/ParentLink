package com.example.myapplication.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun ScreenHeader(
    title: String,
    onLogoutClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.parentlink_logo),
                contentDescription = "Logo",
                modifier = Modifier.height(50.dp)
            )
            Button(onClick = onLogoutClick) {
                Text("Logout")
            }
        }

        Text(
            text = title,
            modifier = Modifier.padding(bottom = 16.dp),
            fontSize = 20.sp
        )
    }
}
