package com.example.myapplication.presentation.ui.carpooling

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
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
        SharedSimpleTimeSlotPicker(
            amStartLabel = "Carpooling Pick-Up A.M. Start Time",
            pmStartLabel = "Carpooling Pick-Up P.M. Start Time",
            amStartTime = slotAmStart,
            onAmStartTimeChange = { slotAmStart = it; slotPmStart = "" },
            pmStartTime = slotPmStart,
            onPmStartTimeChange = { slotPmStart = it }
        )


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
            SharedSimpleTimeSlotPicker(
                amStartLabel = "Carpooling Return A.M. Start Time",
                pmStartLabel = "Carpooling Return P.M. Start Time",
                amStartTime = returnAmStart,
                onAmStartTimeChange = { returnAmStart = it; returnPmStart = "" },
                pmStartTime = returnPmStart,
                onPmStartTimeChange = { returnPmStart = it }
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
                    "returnMaxSeats" to if (returnYN == "Yes") maxSeats else null,
                    "returnRemainingSeats" to if (returnYN == "Yes") maxSeats else null,
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

