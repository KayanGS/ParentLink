package com.example.myapplication.presentation.ui.participating

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
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

    var nameError by remember { mutableStateOf<String?>(null) }
    var surnameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }
    var rePassError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var parentNameTouched by remember { mutableStateOf(false) }
    var parentSurnameTouched by remember { mutableStateOf(false) }
    var parentMobileTouched by remember { mutableStateOf(false) }
    var parentEmailTouched by remember { mutableStateOf(false) }
    var parentPassTouched by remember { mutableStateOf(false) }
    var parentRePassTouched by remember { mutableStateOf(false) }

    val successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "Parent Registration",
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = parentName,
            onValueChange = {
                parentName = it
                if (parentNameTouched) {
                    nameError = if (it.isBlank()) "Parent name is required" else null
                }
            },
            label = { Text("Parent Name") },
            isError = parentNameTouched && nameError != null,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    parentNameTouched = true
                    nameError = if (parentName.isBlank()) "Parent name is required" else null
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (parentNameTouched && nameError != null) {
            Text(nameError ?: "", color = Color.Red, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = parentSurname,
            onValueChange = {
                parentSurname = it
                if (parentSurnameTouched) {
                    surnameError = if (it.isBlank()) "Parent surname is required" else null
                }
            },
            label = { Text("Parent Surname") },
            isError = parentSurnameTouched && surnameError != null,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    parentSurnameTouched = true
                    surnameError = if (parentSurname.isBlank())
                        "Parent surname is required" else null
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (parentSurnameTouched && surnameError != null) {
            Text(surnameError ?: "", color = Color.Red, fontSize = 12.sp)
        }


        OutlinedTextField(
            value = parentMobile,
            onValueChange = {
                parentMobile = it
                if (parentMobileTouched) {
                    mobileError = if (!it.matches(Regex("08[3569][0-9]{7}")))
                        "A valid Irish Mobile Number should start with 083, 085, 086, or 089 and " +
                                "be 10 digits long"
                    else null
                }
            },
            label = { Text("Parent Mobile No") },
            isError = parentMobileTouched && mobileError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    parentMobileTouched = true
                    mobileError = if (!parentMobile.matches(Regex("08[3569][0-9]{7}")))
                        "A valid Irish Mobile Number should start with 083, 085, 086, or 089 and " +
                                "be 10 digits long"
                    else null
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (parentMobileTouched && mobileError != null) {
            Text(mobileError ?: "", color = Color.Red, fontSize = 12.sp)
        }


        OutlinedTextField(
            value = parentEmail,
            onValueChange = {
                parentEmail = it
                if (parentEmailTouched) {
                    val validEmail = Patterns.EMAIL_ADDRESS.matcher(it).matches() &&

                            listOf("gmail.com", "hotmail.com", "yahoo.com", "live.com").any {
                                domain -> it.endsWith(domain)
                            }

                    emailError = if (!validEmail)
                        "Enter a valid email address ending with gmail.com, etc." else null
                }
            },
            label = { Text("Email Address") },
            isError = parentEmailTouched && emailError != null,

            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next),

            keyboardActions = KeyboardActions(
                onNext = {
                    parentEmailTouched = true

                    val validEmail = Patterns.EMAIL_ADDRESS.matcher(parentEmail).matches() &&
                            listOf("gmail.com", "hotmail.com", "yahoo.com", "live.com").any {
                                parentEmail.endsWith(it)
                            }

                    emailError = if (!validEmail)
                        "Enter a valid email address ending with @gmail.com, etc." else null
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (parentEmailTouched && emailError != null) {
            Text(emailError ?: "", color = Color.Red, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = parentPass,
            onValueChange = {
                parentPass = it
                if (parentPassTouched) {
                    val strong = it.matches(Regex(
                        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")
                    )
                    passError = if (!strong)
                        "Password must contain upper/lowercase letters, digits, special " +
                                "characters and be at least 8 characters" else null
                }
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = parentPassTouched && passError != null,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    parentPassTouched = true
                    val strong = parentPass.matches(Regex(
                        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")
                    )
                    passError = if (!strong) "Password must be strong" else null
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (parentPassTouched && passError != null) {
            Text(passError ?: "", color = Color.Red, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = parentRePass,
            onValueChange = {
                parentRePass = it
                if (parentRePassTouched) {
                    rePassError = if (it != parentPass) "Passwords do not match" else null
                }
            },
            label = { Text("Re-type Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = parentRePassTouched && rePassError != null,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    parentRePassTouched = true
                    rePassError = if (parentRePass != parentPass)
                        "The re-typed password does not match the entered password. Please, try " +
                                "again!" else null
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (parentRePassTouched && rePassError != null) {
            Text(rePassError ?: "", color = Color.Red, fontSize = 12.sp)
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Recheck before submit
            nameError = if (parentName.isBlank()) "Parent name is required" else null
            surnameError = if (parentSurname.isBlank()) "Parent surname is required" else null
            mobileError =
                if (!parentMobile.matches(Regex("08[3569][0-9]{7}")))
                    "Invalid Irish mobile number" else null
            val validEmail = Patterns.EMAIL_ADDRESS.matcher(parentEmail).matches() &&
                    listOf(
                        "gmail.com",
                        "hotmail.com",
                        "yahoo.com",
                        "live.com"
                    ).any { parentEmail.endsWith(it) }
            emailError = if (!validEmail) "Enter a valid email" else null
            val strong =
                parentPass.matches(Regex(
                    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")
                )
            passError = if (!strong) "Password must be strong" else null
            rePassError = if (parentPass != parentRePass) "Passwords do not match" else null

            val allValid = listOf(
                nameError, surnameError, mobileError, emailError, passError, rePassError
            ).all { it == null }

            if (allValid) {
                auth.createUserWithEmailAndPassword(parentEmail, parentPass)
                    .addOnSuccessListener { result ->
                        val userId = result.user?.uid
                        if (userId != null) {
                            val data = mapOf(
                                "parentName" to parentName,
                                "parentSurname" to parentSurname,
                                "parentMobile" to parentMobile,
                                "username" to parentEmail
                            )
                            db.collection("Users").document(userId).set(data)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Registered successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onRegisterSuccess()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Error: ${it.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Firebase error: ${it.message}",
                            Toast.LENGTH_LONG)
                            .show()
                    }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }

        successMessage?.let {
            Text(it, color = Color.Green, modifier = Modifier.padding(top = 8.dp))
        }
    }
}