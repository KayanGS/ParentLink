package com.example.myapplication.presentation.components

import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SharedSaveButton(
    modifier: Modifier = Modifier,
    collectionName: String,
    validateFields: () -> Boolean,
    getDataMap: (String) -> Map<String, Any?>,
    onSuccess: () -> Unit,
    onError: ((Exception) -> Unit)? = null,
    buttonText: String = "Save"
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Button(
        onClick = {
            val uid = auth.currentUser?.uid
            if (uid != null && validateFields()) {
                val data = getDataMap(uid)
                db.collection(collectionName)
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Event saved successfully!", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error saving event: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        onError?.invoke(e)
                    }
            } else {
                Toast.makeText(context, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier
    ) {
        Text(buttonText)
    }
}
