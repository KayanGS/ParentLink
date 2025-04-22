package com.example.myapplication.presentation.ui.playdate
//Imports
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.components.*
import com.google.firebase.auth.FirebaseAuth
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
    val auth = FirebaseAuth.getInstance() // For authentication

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
        SharedTextField(
            value = playDateTitle,
            onValueChange = { playDateTitle = it },
            label = "Enter Play Date Title"
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

        // TIME SLOTS
        SharedTimeSlotPicker(
            amStartLabel = "Play Date A.M. Start Time",
            pmStartLabel = "Play Date P.M. Start Time",
            amEndLabel = "Play Date A.M. End Time",
            pmEndLabel = "Play Date P.M. End Time",
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

        // MAX NUMBER OF PLACES IN THE PLAY DATE
        SharedNumberInputField(
            label = "Max No of Places",
            value = maxPlaces,
            onValueChange = { maxPlaces = it }
        )

        // AGE GROUP SUITABILITY
        SharedAgeGroupDropdown(
            selected = ageGroup,
            onSelected = { ageGroup = it }
        )

        // SPECIAL REQUIREMENTS
        SharedSpecialRequirementsField(
            value = specialRequirements,
            onValueChange = { specialRequirements = it }
        )

        // SAVE BUTTON
        SharedSaveButton(
            collectionName = "Posted Play Date Event Record",
            validateFields = {
                playDateTitle.isNotBlank() && selectedDate.isNotBlank()
            },
            getDataMap = { uid ->
                hashMapOf(
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
                    "postedDate" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    "postedTime" to SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                )
            },
            onSuccess = {
                showSuccessPopup = true
            }
        )

        // SUCCESS POPUP
        SharedSuccessPopup(
            visible = showSuccessPopup,
            message = "New Record of play date event was successfully created.",
            onAnother = {
                playDateType = ""; playDateCat = ""; playDateTitle = ""
                weekCommenceDate = ""; dayOfWeek = ""; selectedDate = ""
                slotAmStart = ""; slotPmStart = ""; slotAmEnd = ""; slotPmEnd = ""
                maxPlaces = ""; ageGroup = ""; specialRequirements = ""
                showSuccessPopup = false
            },
            onBackToDashboard = onBackToDashboard
        )
    }
}