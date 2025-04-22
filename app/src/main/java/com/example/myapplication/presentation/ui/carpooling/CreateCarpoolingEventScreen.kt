package com.example.myapplication.presentation.ui.carpooling

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import com.example.myapplication.presentation.components.*

@Composable
fun CreateCarpoolingEventScreen(
    onBackToDashboard: () -> Unit,
    onLogout: () -> Unit = {},
) {
    // ######################################## LOCAL VARIABLES #######################################
    val auth = FirebaseAuth.getInstance()
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

        SharedTextField(
            value = carpoolingTitle,
            onValueChange = { carpoolingTitle = it },
            label = "Enter Carpooling Title"
        )

        // WEEK COMMENCING DATE
        SharedWeekCommencingField(
            selectedValue = weekCommenceDate,
            onValueChange = { weekCommenceDate = it }
        )

        // DAY OF THE WEEK
        SharedDayAndDatePicker(
            weekCommenceDate = weekCommenceDate,
            selectedDayOfWeek = dayOfWeek,
            selectedDate = selectedDate,
            onDayOfWeekChange = { dayOfWeek = it },
            onDateSelected = { selectedDate = it }
        )

        // PICK-UP POINT
        SharedTextField(
            value = pickUp,
            onValueChange = { pickUp = it },
            label = "Pick-up Point"
        )

        //DESTINATION POINT
        SharedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = "Destination Point"
        )

        // PICK-UP TIME SLOTS
        SharedTimeSlotPicker(
            amStartLabel = "Carpooling Pick-Up A.M. Start Time",
            pmStartLabel = "Carpooling Pick-Up P.M. Start Time",
            amEndLabel = "Carpooling Pick-Up A.M. End Time",
            pmEndLabel = "Carpooling Pick-Up P.M. End Time",
            amStartTime = slotAmStart,
            onAmStartTimeChange = { slotAmStart = it; slotPmStart = "" },
            pmStartTime = slotPmStart,
            onPmStartTimeChange = { slotPmStart = it },
            amEndTime = slotAmEnd,
            onAmEndTimeChange = { slotAmEnd = it },
            pmEndTime = slotPmEnd,
            onPmEndTimeChange = { slotPmEnd = it },
            errorMessage = errorMessage,
            onErrorChange = { errorMessage = it }
        )

        if (errorMessage.isNotBlank()) { // If there is an error message
            Text(
                text = errorMessage, // Display the error message
                color = MaterialTheme.colorScheme.error, // Set the color to red
                modifier = Modifier.padding(4.dp) // Add padding around the error message
            )
        }

        // MAX NUMBER OF SEATS IN THE CARPOOLING
        SharedNumberInputField(
            label = "Max No of Places",
            value = maxSeats,
            onValueChange = { maxSeats = it }
        )

        // AGE GROUP SUITABILITY
        SharedAgeGroupDropdown(
            selected = ageGroup,
            onSelected = { ageGroup = it }
        )

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
            SharedTimeSlotPicker(
                amStartLabel = "Carpooling Return A.M. Start Time",
                pmStartLabel = "Carpooling Return P.M. Start Time",
                amEndLabel = "Carpooling Return A.M. End Time",
                pmEndLabel = "Carpooling Return P.M. End Time",
                amStartTime = slotAmStart,
                onAmStartTimeChange = { slotAmStart = it; slotPmStart = "" },
                pmStartTime = slotPmStart,
                onPmStartTimeChange = { slotPmStart = it },
                amEndTime = slotAmEnd,
                onAmEndTimeChange = { slotAmEnd = it },
                pmEndTime = slotPmEnd,
                onPmEndTimeChange = { slotPmEnd = it },
                errorMessage = errorMessage,
                onErrorChange = { errorMessage = it }
            )
        }

        // SPECIAL REQUIREMENTS
        SharedSpecialRequirementsField(
            value = specialRequirements,
            onValueChange = { specialRequirements = it }
        )

        SharedSaveButton(
            collectionName = "Posted Carpooling Event Record",
            validateFields = {
                carpoolingTitle.isNotBlank() && selectedDate.isNotBlank()
            },
            getDataMap = { uid ->
                hashMapOf(
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
            },
            onSuccess = {
                showPopup = true
            }
        )

        // Success Popup
        SharedSuccessPopup(
            visible = showPopup,
            message = "New Record of carpooling event was successfully created.",
            onAnother = {
                carpoolingTitle = ""; weekCommenceDate = ""; dayOfWeek = ""; selectedDate = ""
                pickUp = ""; destination = ""; slotAmStart = ""; slotPmStart = ""
                slotAmEnd = ""; slotPmEnd = ""; maxSeats = ""; ageGroup = ""
                returnYN = ""; returnPickUp = ""; returnDestination = ""
                returnAmStart = ""; returnPmStart = ""; returnAmEnd = ""; returnPmEnd = ""
                specialRequirements = ""; showPopup = false
            },
            onBackToDashboard = onBackToDashboard
        )
    }
}

