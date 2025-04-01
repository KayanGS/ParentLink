package com.example.myapplication

import android.widget.CalendarView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreatePlayDateEventScreen(
    onBackToDashboard: () -> Unit,
    onLogout: () -> Unit = {},
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

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
    var showDatePicker by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun getUpcomingMondays(): List<String> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // Use standard ISO-like format
        val today = Calendar.getInstance()
        val mondays = mutableListOf<String>()

        val monday = today.clone() as Calendar
        monday.set(Calendar.HOUR_OF_DAY, 0)
        monday.set(Calendar.MINUTE, 0)
        monday.set(Calendar.SECOND, 0)
        monday.set(Calendar.MILLISECOND, 0)

        val dow = monday.get(Calendar.DAY_OF_WEEK)
        if (dow != Calendar.MONDAY) {
            monday.add(Calendar.DAY_OF_MONTH, -((dow + 5) % 7))
        }

        mondays.add(format.format(monday.time))
        for (i in 1..4) {
            monday.add(Calendar.DAY_OF_MONTH, 7)
            mondays.add(format.format(monday.time))
        }

        return mondays
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Image(
                painter = painterResource(id = R.drawable.parentlink_logo),
                contentDescription = null,
                modifier = Modifier.height(50.dp)
            )
            Button(onClick = {
                auth.signOut()
                onLogout()
            }) {
                Text("Logout")
            }
        }

        Text(
            "Create a Play Date Event Screen",
            fontSize = 20.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        DropdownField(
            "Select Play Date Type",
            listOf("Birthday", "Fun Day", "Visiting a Place"),
            playDateType
        ) { playDateType = it }
        DropdownField(
            "Select Play Date Category",
            listOf("Indoor", "Outdoor"),
            playDateCat
        ) { playDateCat = it }

        OutlinedTextField(
            value = playDateTitle,
            onValueChange = { playDateTitle = it },
            label = { Text("Enter Play Date Title") },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownField(
            "Select Week Commencing Date",
            getUpcomingMondays(),
            weekCommenceDate
        ) { weekCommenceDate = it }
        DropdownField(
            "Select Day of the Week",
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
            dayOfWeek
        ) { dayOfWeek = it }

        if (weekCommenceDate.isNotBlank()) {

                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val parsedMonday = formatter.parse(weekCommenceDate)

                parsedMonday?.let {
                    Text("Select Date for the Play Date", fontSize = 16.sp)
                    EmbeddedCalendar(mondayDate = it) { date ->
                        selectedDate = date
                    }
                    Text("Selected Date: $selectedDate", modifier = Modifier.padding(8.dp))
                }
        }



        DropdownField(
            "Play Date A.M. Start Time",
            listOf("09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30"),
            slotAmStart
        ) {
            slotAmStart = it
            slotPmStart = ""
        }

        if (slotAmStart.isBlank()) {
            DropdownField(
                "Play Date P.M. Start Time",
                listOf(
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
                slotPmStart
            ) {
                slotPmStart = it
            }
        }

        if (slotAmStart.isNotBlank()) {
            DropdownField(
                "Play Date A.M. End Time",
                listOf("10:00", "10:30", "11:00", "11:30", "12:00", "12:30"),
                slotAmEnd
            ) {
                if (it <= slotAmStart) {
                    errorMessage = "A.M. End Time must be after Start Time"
                    slotAmEnd = ""
                } else {
                    errorMessage = ""
                    slotAmEnd = it
                }
            }
        } else if (slotPmStart.isNotBlank()) {
            DropdownField(
                "Play Date P.M. End Time",
                listOf(
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
                slotPmEnd
            ) {
                if (it <= slotPmStart) {
                    errorMessage = "P.M. End Time must be after Start Time"
                    slotPmEnd = ""
                } else {
                    errorMessage = ""
                    slotPmEnd = it
                }
            }
        }

        if (errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(4.dp)
            )
        }

        OutlinedTextField(
            value = maxPlaces,
            onValueChange = { maxPlaces = it },
            label = { Text("Max No of Places") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        DropdownField("Age Group Suitability", listOf("4-6", "7-11"), ageGroup) { ageGroup = it }

        OutlinedTextField(
            value = specialRequirements,
            onValueChange = { specialRequirements = it },
            label = { Text("Special Requirements (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val eventData = hashMapOf(
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
                    "postedDate" to SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(Date()),
                    "postedTime" to SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                )
                db.collection("Posted Play Date Event Record").add(eventData)
                    .addOnSuccessListener { showSuccessPopup = true }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error saving event", Toast.LENGTH_SHORT).show()
                    }
            }
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Save")
        }

        if (showSuccessPopup) {
            AlertDialog(
                onDismissRequest = { },
                confirmButton = {},
                title = { Text("Success") },
                text = {
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

@Composable
fun EmbeddedCalendar(
    mondayDate: Date,
    onDateSelected: (String) -> Unit
) {
    val minCal = Calendar.getInstance().apply { time = mondayDate }
    val maxCal = (minCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 6) }

    AndroidView(
        factory = { context ->
            CalendarView(context).apply {
                minDate = minCal.timeInMillis
                maxDate = maxCal.timeInMillis
                date = minCal.timeInMillis

                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val selectedCal = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    val formatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCal.time)
                    onDateSelected(formatted)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
