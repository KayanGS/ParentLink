package com.example.myapplication.presentation.ui.playdate
//Imports
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.components.ScreenHeader
import com.example.myapplication.presentation.components.SharedDropdownField
import com.example.myapplication.presentation.components.SharedCalendar
import com.example.myapplication.utils.getUpcomingMondays
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create a Play Date Event Screen
 */
@Composable
fun CreatePlayDateEventScreen(
    onBackToDashboard: () -> Unit, // Callback to navigate back to the dashboard
    onLogout: () -> Unit = {},
) {

// ######################################## LOCAL VARIABLES #######################################
    val context = LocalContext.current // For displaying toasts
    val auth = FirebaseAuth.getInstance() // For authentication
    val db = FirebaseFirestore.getInstance() // For database access

    // Form state
    var playDateType by remember { mutableStateOf("") }
    var playDateCat by remember { mutableStateOf("") }
    var playDateTitle by remember { mutableStateOf("") }
    var weekCommenceDate by remember { mutableStateOf("") }
    var dayOfWeek by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var slotAmStart by remember { mutableStateOf("") }
    var slotPmStart by remember { mutableStateOf("") }
    var slotAmEnd by remember { mutableStateOf("") }
    var slotPmEnd by remember { mutableStateOf("") }
    var maxPlaces by remember { mutableStateOf("") }
    var ageGroup by remember { mutableStateOf("") }
    var specialRequirements by remember { mutableStateOf("") }
    var showSuccessPopup by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState() // For scrolling the form
// ###################################### END LOCAL VARIABLES #####################################

    Column(
        modifier = Modifier // Modifier for the form
            .fillMaxSize() // Fill the entire screen
            .verticalScroll(scrollState) // Scroll the form
            .padding(16.dp) // Add padding to the form
    ) {
// ########################################### HEADER #############################################
        ScreenHeader(
            title = "Create a Play Date Event Screen",
            onLogoutClick = {
                auth.signOut()
                onLogout()
            }
        )
// ########################################## END HEADER ##########################################



// ############################################## FORM #############################################
        // PLAY DATE TYPE DROPDOWN
        SharedDropdownField(
            "Select Play Date Type", // Label for the dropdown
            listOf("Birthday", "Fun Day", "Visiting a Place"), // Options for the dropdown
            playDateType // Selected option
        ) { playDateType = it } // Callback when an option is selected

        // PLAY DATE CATEGORY DROPDOWN
        SharedDropdownField(
            "Select Play Date Category", // Label for the dropdown
            listOf("Indoor", "Outdoor"), // Options for the dropdown
            playDateCat // Selected option
        ) { playDateCat = it } // Callback when an option is selected

        // PLAY DATE TITLE
        OutlinedTextField(
            value = playDateTitle, // Text field value
            onValueChange = { playDateTitle = it }, // Callback when text changes
            label = { Text("Enter Play Date Title") }, // Label for the text field
            modifier = Modifier.fillMaxWidth() // Fill the width of the parent
        )

        // WEEK COMMENCING DATE
        SharedDropdownField(
            "Select Week Commencing Date", // Label for the dropdown
            getUpcomingMondays().map { it.first }, // Options for the dropdown
            weekCommenceDate // Selected option
        ) { selectedLabel ->  // Callback when an option is selected
            weekCommenceDate = // Set the selected date
                getUpcomingMondays().firstOrNull { it.first == selectedLabel }?.second
                    ?: "" // Find the corresponding date
        }

        // DAY OF THE WEEK
        SharedDropdownField(
            "Select Day of the Week", // Label for the dropdown
            // List of days of the week
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
            dayOfWeek // Selected day of the week
        ) { dayOfWeek = it } // Callback when an option is selected

        if (weekCommenceDate.isNotBlank()) { // If a week commencing date is selected
            Text("Select Date for the Play Date", fontSize = 16.sp) // Label for the date picker

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Date formatter
            val mondayCal = Calendar.getInstance().apply { // Monday calendar
                time = formatter.parse(weekCommenceDate) ?: Date() // Set to the selected date
            }

            // Map selected day of week to offset
            val dayOffsets = mapOf(
                "Monday" to 0,
                "Tuesday" to 1,
                "Wednesday" to 2,
                "Thursday" to 3,
                "Friday" to 4,
                "Saturday" to 5,
                "Sunday" to 6
            )
            val offset = dayOffsets[dayOfWeek] ?: 0 // Get the offset for the selected day
            mondayCal.add(Calendar.DAY_OF_MONTH, offset) // Add the offset to the Monday date

            SharedCalendar( // Embedded calendar
                initialDate = mondayCal.timeInMillis, // Initial date
                weekStartMillis = formatter.parse(weekCommenceDate)?.time // Week start date
                    ?: System.currentTimeMillis() // Default to current time if parsing fails
            ) { date -> // Callback when a date is selected
                // Format the selected date
                selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                // Get the day of the week
                dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
            }
            // Display the selected date
            Text("Selected Date: $selectedDate", modifier = Modifier.padding(8.dp))
        }

        // TIME SLOTS
        // A.M. START TIME SLOT
        SharedDropdownField(
            "Play Date A.M. Start Time", // Label for the dropdown
            listOf(
                "09:00",
                "09:30",
                "10:00",
                "10:30",
                "11:00",
                "11:30",
                "12:00",
                "12:30"
            ), // Options for the dropdown
            slotAmStart // Selected option
        ) {
            slotAmStart = it // Callback when an option is selected
            slotPmStart = "" // Clear the PM start time
        }

        // P.M. START TIME SLOT
        if (slotAmStart.isBlank()) { // If no A.M. start time is selected
            SharedDropdownField(
                "Play Date P.M. Start Time", // Label for the dropdown
                listOf( // Options for the dropdown
                    "13:00",
                    "13:30",
                    "14:00",
                    "14:30",
                    "15:00",
                    "15:30",
                    "16:00",
                    "16:30",
                    "17:00",
                    "17:30",
                    "18:00"
                ),
                slotPmStart // Selected option
            ) {
                slotPmStart = it // Callback when an option is selected
            }
        }

        // A.M. END TIME SLOT
        if (slotAmStart.isNotBlank()) {
            SharedDropdownField(
                "Play Date A.M. End Time", // Label for the dropdown
                listOf(
                    "10:00",
                    "10:30",
                    "11:00",
                    "11:30",
                    "12:00",
                    "12:30"
                ), // Options for the dropdown
                slotAmEnd // Selected option
            ) {
                if (it <= slotAmStart) { // If the end time is before the start time
                    errorMessage = "A.M. End Time must be after Start Time" // Set the error message
                    slotAmEnd = "" // Clear the end time
                } else { // If the end time is after the start time
                    errorMessage = "" // Clear the error message
                    slotAmEnd = it // Set the end time
                }
            }

            // P.M. END TIME SLOT
            SharedDropdownField(
                "Play Date P.M. End Time", // Label for the dropdown
                listOf( // Options for the dropdown
                    "14:00",
                    "14:30",
                    "15:00",
                    "15:30",
                    "16:00",
                    "16:30",
                    "17:00",
                    "17:30",
                    "18:00",
                    "18:30",
                    "19:00"
                ),
                slotPmEnd // Selected option
            ) {
                if (it <= slotPmStart) {
                    errorMessage = "P.M. End Time must be after Start Time" // Set the error message
                    slotPmEnd = "" // Clear the end time
                } else { // If the end time is after the start time
                    errorMessage = "" // Clear the error message
                    slotPmEnd = it // Set the end time
                }
            }
        }

        if (errorMessage.isNotBlank()) { // If there is an error message
            Text(
                text = errorMessage, // Display the error message
                color = MaterialTheme.colorScheme.error, // Set the color to red
                modifier = Modifier.padding(4.dp) // Add padding around the error message
            )
        }

        // MAX NUMBER OF PLACES IN THE PLAY DATE
        OutlinedTextField(
            value = maxPlaces, // Text field value
            onValueChange = { maxPlaces = it }, // Callback when text changes
            label = { Text("Max No of Places") }, // Label for the text field
            // Keyboard options for the text field
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth() // Fill the width of the parent
        )

        // AGE GROUP SUITABILITY
        SharedDropdownField(
            "Age Group Suitability", // Label for the dropdown
            listOf("4-6", "7-11"), // Options for the dropdown
            ageGroup
        ) {
            ageGroup = it
        } // Callback when an option is selected

        // SPECIAL REQUIREMENTS
        OutlinedTextField(
            value = specialRequirements, // Text field value
            onValueChange = { specialRequirements = it }, // Callback when text changes
            label = { Text("Special Requirements (Optional)") }, // Label for the text field
            modifier = Modifier.fillMaxWidth() // Fill the width of the parent
        )

        // SAVE BUTTON
        Button(
            onClick = { // Callback when the button is clicked
                val uid = auth.currentUser?.uid // Get the user's UID
                // If the user is logged in and all required fields are filled
                if (uid != null && playDateTitle.isNotBlank() && selectedDate.isNotBlank()) {
                    val eventData = hashMapOf( // Create a hash map for the event data
                        "organizerId" to uid,
                        "playDateTitle" to playDateTitle,
                        "playDateType" to playDateType,
                        "playDateCat" to playDateCat,
                        "weekCommenceDate" to weekCommenceDate,
                        "dayOfWeek" to dayOfWeek,
                        "date" to selectedDate,
                        "startTime" to (slotAmStart.ifBlank { slotPmStart }),
                        "endTime" to (slotAmEnd.ifBlank { slotPmEnd }),
                        "maxPlaces" to maxPlaces,
                        "remainingPlaces" to maxPlaces,
                        "ageGroup" to ageGroup,
                        "specialReq" to specialRequirements.ifBlank { null },
                        "postedDate" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                            Date()
                        ),
                        "postedTime" to SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(Date())
                    )

                    // Save the event data to the database
                    db.collection("Posted Play Date Event Record")
                        .add(eventData)
                        .addOnSuccessListener {
                            showSuccessPopup = true
                            Toast.makeText(context, "Event saved successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Log to console and show in-app error
                            e.printStackTrace()
                            Toast.makeText(context, "Error saving event: ${e.message}", Toast.LENGTH_LONG).show()
                            println("🔥 Firestore save failed")
                            println("🔥 Reason: ${e.localizedMessage}")
                            println("🔥 Data being saved: $eventData")
                        }

                // If any required fields are not filled, ask to fill them
                } else {
                    Toast.makeText(context, "Please fill all required fields.", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save")
        }

        // SUCCESS POPUP
        if (showSuccessPopup) {
            AlertDialog(
                onDismissRequest = { }, // Callback when the dialog is dismissed
                confirmButton = {}, // No confirmation button
                title = { Text("Success") }, // Title of the dialog
                text = { // Text of the dialog
                    Column {
                        Text("New Record of play date event was successfully created.")
                        Button(onClick = {
                            playDateType = ""; playDateCat = ""; playDateTitle = ""
                            weekCommenceDate = ""; dayOfWeek = ""; selectedDate = ""
                            slotAmStart = ""; slotPmStart = ""; slotAmEnd = ""; slotPmEnd = ""
                            maxPlaces = ""; ageGroup = ""; specialRequirements = ""
                            showSuccessPopup = false
                        }) { Text("Another Play Date Event") }
                        Button(onClick = { onBackToDashboard() }) { Text("Back to Dashboard Screen") }
                    }
                }
            )
        }

    }
}