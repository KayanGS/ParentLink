package com.example.myapplication.presentation.ui.participating

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ParticipatingRegistrationScreen(
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var parentName by remember { mutableStateOf("") }
    var parentSurname by remember { mutableStateOf("") }
    var parentMobile by remember { mutableStateOf("") }
    var parentEmail by remember { mutableStateOf("") }
    var parentPass by remember { mutableStateOf("") }
    var parentRePass by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Parent Registration", fontSize = 22.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(value = parentName, onValueChange = { parentName = it }, label = { Text("Parent Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = parentSurname, onValueChange = { parentSurname = it }, label = { Text("Parent Surname") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            value = parentMobile,
            onValueChange = { parentMobile = it },
            label = { Text("Parent Mobile No") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = parentEmail,
            onValueChange = { parentEmail = it },
            label = { Text("Email Address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = parentPass,
            onValueChange = { parentPass = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = parentRePass,
            onValueChange = { parentRePass = it },
            label = { Text("Re-type Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            errorMessage = null
            successMessage = null

            val isIrishMobile = parentMobile.matches(Regex("08[3569][0-9]{7}"))
            val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(parentEmail).matches() &&
                    listOf("gmail.com", "hotmail.com", "yahoo.com", "live.com").any { parentEmail.endsWith(it) }
            val isStrongPass = parentPass.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$"))

            when {
                parentName.isBlank() || parentSurname.isBlank() || parentMobile.isBlank() ||
                        parentEmail.isBlank() || parentPass.isBlank() || parentRePass.isBlank() -> {
                    errorMessage = "Please fill in all fields."
                }
                !isIrishMobile -> {
                    errorMessage = "A valid Irish Mobile Number should start with 083, 085, 086, or 089 and be 10 digits long."
                }
                !isValidEmail -> {
                    errorMessage = "A valid email address should contain @ and end with a known domain."
                }
                !isStrongPass -> {
                    errorMessage = "Password must contain upper/lowercase letters, digits, special characters and be at least 8 characters."
                }
                parentPass != parentRePass -> {
                    errorMessage = "The re-typed password does not match the entered password. Please, try again!"
                }
                else -> {
                    auth.createUserWithEmailAndPassword(parentEmail, parentPass)
                        .addOnSuccessListener { result ->
                            val userId = result.user?.uid
                            if (userId != null) {
                                val data = mapOf(
                                    "parentName" to parentName,
                                    "parentSurname" to parentSurname,
                                    "parentMobile" to parentMobile,
                                    "username" to parentEmail,
                                    "role" to "participatingParentRole"
                                )
                                db.collection("Participating childs parent").document(userId).set(data)
                                    .addOnSuccessListener {
                                        successMessage = "New Record for events participating child's parent successfully created."
                                        Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                                        onRegisterSuccess()
                                    }
                                    .addOnFailureListener {
                                        errorMessage = "Registration failed: ${it.message}"
                                    }
                            }
                        }
                        .addOnFailureListener {
                            errorMessage = "Firebase Auth Error: ${it.message}"
                        }
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }

        errorMessage?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        successMessage?.let {
            Text(it, color = Color.Green, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
