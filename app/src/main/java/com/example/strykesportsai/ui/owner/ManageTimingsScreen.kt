package com.example.strykesportsai.ui.owner

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTimingsScreen(
    turfId: Long,
    viewModel: OwnerViewModel,
    onNavigateBack: () -> Unit
) {
    val turfs by viewModel.ownerTurfs.collectAsState()
    val turf = turfs.find { it.id == turfId }
    
    val weekDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    
    // Total possible slots (just for logic, we will maintain a set of enabled ones)
    val allPossibleSlots = (6..22).map { String.format("%02d:00", it) }

    var selectedTimings by remember(turf) {
        val timings = turf?.availableTimings?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        mutableStateOf(timings.toSet())
    }

    // Keep track of all slots ever added or default ones
    var allSlots by remember(turf) {
        val timings = turf?.availableTimings?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        val initialSlots = if (timings.isEmpty()) allPossibleSlots else (allPossibleSlots + timings).distinct().sorted()
        mutableStateOf(initialSlots)
    }

    var openDays by remember(turf) {
        val days = turf?.openDays?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: weekDays
        mutableStateOf(days.toSet())
    }

    val saveChanges = {
        viewModel.updateTurfTimings(
            turfId, 
            selectedTimings.joinToString(", "),
            openDays.joinToString(", ")
        )
    }

    BackHandler {
        saveChanges()
        onNavigateBack()
    }

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Manage Slots",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        saveChanges()
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Time Slot")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Days Selection
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Operational Days",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(weekDays) { day ->
                        val isSelected = openDays.contains(day)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF6B8A7A) else Color.White)
                                .clickable {
                                    openDays = if (isSelected) openDays - day else openDays + day
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.take(3),
                                color = if (isSelected) Color.White else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Slots List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Booking Slots",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(allSlots) { time ->
                    val isActive = selectedTimings.contains(time)
                    SlotItem(
                        time = formatTime(time),
                        isActive = isActive,
                        onToggle = { enabled ->
                            selectedTimings = if (enabled) {
                                selectedTimings + time
                            } else {
                                selectedTimings - time
                            }
                        }
                    )
                }
            }

            Button(
                onClick = {
                    saveChanges()
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B8A7A)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Configuration", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formattedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    if (!allSlots.contains(formattedTime)) {
                        allSlots = (allSlots + formattedTime).sorted()
                    }
                    selectedTimings = selectedTimings + formattedTime
                    showTimePicker = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
fun SlotItem(
    time: String,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) Color.White else Color(0xFFF1F1F1),
        shadowElevation = if (isActive) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = time,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) Color(0xFF333333) else Color.Gray
            )
            Switch(
                checked = isActive,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF6B8A7A),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCCCCCC),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

private fun formatTime(timeStr: String): String {
    return try {
        val localTime = LocalTime.parse(timeStr)
        val endLocalTime = localTime.plusHours(1)
        val formatter = DateTimeFormatter.ofPattern("h a")
        "${localTime.format(formatter).lowercase()} to ${endLocalTime.format(formatter).lowercase()}"
    } catch (e: Exception) {
        timeStr
    }
}
