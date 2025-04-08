package com.example.myapplication.presentation.ui.carpooling

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import com.example.myapplication.presentation.components.ScreenHeader
import com.example.myapplication.presentation.components.SharedCalendar
import com.example.myapplication.presentation.components.SharedDropdownField
import com.example.myapplication.utils.getUpcomingMondays


@Composable
fun CreateCarpoolingEventScreen(
    onBackToDashboard: () -> Unit,
    onLogout: () -> Unit = {},
) {
    // ######################################## LOCAL VARIABLES #######################################
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    // 🔸 Form State
    var carpoolingTitle by remember { mutableStateOf("") }
    var weekCommenceDate by remember { mutableStateOf("") }
    var dayOfWeek by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var pickUp by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var slotAmStart by remember { mutableStateOf("") }
    var slotPmStart by remember { mutableStateOf("") }
    var slotAmEnd by remember { mutableStateOf("") }
    var slotPmEnd by remember { mutableStateOf("") }
    var maxSeats by remember { mutableStateOf("") }
    var ageGroup by remember { mutableStateOf("") }
    var returnYN by remember { mutableStateOf("") }
    var returnPickUp by remember { mutableStateOf("") }
    var returnDestination by remember { mutableStateOf("") }
    var returnAmStart by remember { mutableStateOf("") }
    var returnPmStart by remember { mutableStateOf("") }
    var returnAmEnd by remember { mutableStateOf("") }
    var returnPmEnd by remember { mutableStateOf("") }
    var specialRequirements by remember { mutableStateOf("") }
    var showPopup by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState() // For scrolling the form
// ###################################### END LOCAL VARIABLES #####################################


// ############################################## FORM #############################################
    Column(
        modifier = Modifier // Modifier for the form
            .fillMaxSize() // Fill the entire screen
            .verticalScroll(scrollState) // Scroll the form
            .padding(16.dp) // Add padding to the form
    ) {
// ########################################### HEADER #############################################
        ScreenHeader(
            title = "Create a Carpooling Event Screen",
            onLogoutClick = {
                auth.signOut()
                onLogout()
            }
        )
// ########################################## END HEADER ##########################################

// ############################################## FORM #############################################

        // CARPOOLING TITLE
        OutlinedTextField(
            value = carpoolingTitle, // Text field value
            onValueChange = { carpoolingTitle = it }, // Callback when text changes
            label = { Text("Enter Carpooling Title") }, // Label for the text field
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

        // PICK-UP POINT
        OutlinedTextField(
            value = pickUp, // Text field value
            onValueChange = { pickUp = it }, // Callback when text changes
            label = { Text("Pick-up Point") }, // Label for the text field
            modifier = Modifier.fillMaxWidth() // Fill the width of the parent
        )

        //DESTINATION POINT
        OutlinedTextField(
            value = destination, // Text field value
            onValueChange = { destination = it }, // Callback when text changes
            label = { Text("Destination Point") }, // Label for the text field
            modifier = Modifier.fillMaxWidth() // Fill the width of the parent
        )

        // PICK-UP TIME SLOTS
        // PICK-UP A.M. START TIME SLOT
        SharedDropdownField(
            "Carpooling Pick-up A.M. Start Time", // Label for the dropdown
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

        // PICK-UP P.M. START TIME SLOT
        if (slotAmStart.isBlank()) { // If no A.M. start time is selected
            SharedDropdownField(
                "Carpooling Pick-up P.M. Start Time", // Label for the dropdown
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

        // PICK-UP A.M. END TIME SLOT
        if (slotAmStart.isNotBlank()) {
            SharedDropdownField(
                "Carpooling Pick-up A.M. End Time", // Label for the dropdown
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
                    errorMessage = "A.M. Pick-up End Time must be after Start Time" // Set the error message
                    slotAmEnd = "" // Clear the end time
                } else { // If the end time is after the start time
                    errorMessage = "" // Clear the error message
                    slotAmEnd = it // Set the end time
                }
            }

            // PICK-UP P.M. END TIME SLOT
            SharedDropdownField(
                "Carpooling Pick-Up P.M. End Time", // Label for the dropdown
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
                    errorMessage = "Pick-up P.M. End Time must be after Start Time" // Set the error message
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

        // MAX NUMBER OF SEATS IN THE CARPOOLING
        OutlinedTextField(
            value = maxSeats, // Text field value
            onValueChange = { maxSeats = it }, // Callback when text changes
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


        // RETURN JOURNEY
        SharedDropdownField(
            "Return Journey?",
            listOf("Yes", "No"),
            returnYN) {
                returnYN = it }

        if (returnYN == "Yes") {

            OutlinedTextField(
                returnPickUp, {
                    returnPickUp = it
                },
                label = {
                    Text(
                        "Return Pick-up Point"
                    )
                },

                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                returnDestination, {
                    returnDestination = it
                },
                label = {
                    Text("Return Destination Point")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // RETURN TIME SLOTS
            // RETURN A.M. START TIME SLOT
            SharedDropdownField(
                "Carpooling Return A.M. Start Time", // Label for the dropdown
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
                returnAmStart // Selected option
            ) {
                returnAmStart = it // Callback when an option is selected
                returnPmStart = "" // Clear the PM start time
            }

            // RETURN P.M. START TIME SLOT
            if (returnAmStart.isBlank()) { // If no A.M. start time is selected
                SharedDropdownField(
                    "Carpooling Return P.M. Start Time", // Label for the dropdown
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
                    returnPmStart // Selected option
                ) {
                    returnPmStart = it // Callback when an option is selected
                }
            }

            // RETURN A.M. END TIME SLOT
            if (returnAmStart.isNotBlank()) {
                SharedDropdownField(
                    "Carpooling Return A.M. End Time", // Label for the dropdown
                    listOf(
                        "10:00",
                        "10:30",
                        "11:00",
                        "11:30",
                        "12:00",
                        "12:30"
                    ), // Options for the dropdown
                    returnAmEnd // Selected option
                ) {
                    if (it <= returnAmStart) { // If the end time is before the start time
                        errorMessage =
                            "A.M. Return End Time must be after Start Time" // Set the error message
                        returnAmEnd = "" // Clear the end time
                    } else { // If the end time is after the start time
                        errorMessage = "" // Clear the error message
                        returnAmEnd = it // Set the end time
                    }
                }

                // RETURN P.M. END TIME SLOT
                SharedDropdownField(
                    "Carpooling Return P.M. End Time", // Label for the dropdown
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
                    returnPmEnd // Selected option
                ) {
                    if (it <= returnPmStart) {
                        errorMessage =
                            "Return P.M. End Time must be after Start Time" // Set the error message
                        returnPmEnd = "" // Clear the end time
                    } else { // If the end time is after the start time
                        errorMessage = "" // Clear the error message
                        returnPmEnd = it // Set the end time
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
        }

            // SPECIAL REQUIREMENTS
        OutlinedTextField(
            value = specialRequirements, // Text field value
            onValueChange = { specialRequirements = it }, // Callback when text changes
            label = { Text("Special Requirements (Optional)") }, // Label for the text field
            modifier = Modifier.fillMaxWidth() // Fill the width of the parent
        )

        // Save Button
        Button(onClick = {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val data = hashMapOf(
                    "organizerId" to uid,
                    "carpoolingTitle" to carpoolingTitle,
                    "weekCommenceDate" to weekCommenceDate,
                    "dayOfWeek" to dayOfWeek,
                    "date" to selectedDate,
                    "pickup" to pickUp,
                    "destination" to destination,
                    "startTime" to (slotAmStart.ifBlank { slotPmStart }),
                    "endTime" to (slotAmEnd.ifBlank { slotPmEnd }),
                    "maxSeats" to maxSeats,
                    "remainingSeats" to maxSeats,
                    "ageGroup" to ageGroup,
                    "returnJourney" to returnYN,
                    "returnPickup" to if (returnYN == "Yes") returnPickUp else null,
                    "returnDestination" to if (returnYN == "Yes") returnDestination else null,
                    "returnStartTime" to if (returnYN == "Yes") (returnAmStart.ifBlank { returnPmStart }) else null,
                    "returnEndTime" to if (returnYN == "Yes") (returnAmEnd.ifBlank { returnPmEnd }) else null,
                    "specialRequirements" to specialRequirements.ifBlank { null },
                    "postedDate" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    "postedTime" to SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                )
                db.collection("Posted Carpooling Event Record").add(data)
                    .addOnSuccessListener { showPopup = true }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
            }
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Save")
        }

        // Success Popup
        if (showPopup) {
            AlertDialog(
                onDismissRequest = { },
                confirmButton = {},
                title = { Text("Success") },
                text = {
                    Column {
                        Text("New Record of carpooling event was successfully created.")
                        Button(onClick = {
                            // Clear all
                            carpoolingTitle = ""; weekCommenceDate = ""; dayOfWeek = ""; selectedDate = ""
                            pickUp = ""; destination = ""; slotAmStart = ""; slotPmStart = ""
                            slotAmEnd = ""; slotPmEnd = ""; maxSeats = ""; ageGroup = ""
                            returnYN = ""; returnPickUp = ""; returnDestination = ""
                            returnAmStart = ""; returnPmStart = ""; returnAmEnd = ""; returnPmEnd = ""
                            specialRequirements = ""; showPopup = false
                        }) { Text("Another Carpooling Event") }
                        Button(onClick = onBackToDashboard) { Text("Back to Dashboard Screen") }
                    }
                }
            )
        }
    }
}

