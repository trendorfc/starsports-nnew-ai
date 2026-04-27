package com.example.strykesportsai.ui.turf

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.strykesportsai.ui.player.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurfDetailScreen(
    turfId: Long,
    playerViewModel: PlayerViewModel,
    turfViewModel: TurfViewModel,
    onNavigateToPayment: (Long, String, Long, Double) -> Unit,
    onNavigateBack: () -> Unit
) {
    val turfs by playerViewModel.turfs.collectAsState()
    val turf = turfs.find { it.id == turfId }
    val user by playerViewModel.user.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val today = LocalDate.now()
    val dates = (0..6).map { today.plusDays(it.toLong()) }
    var selectedDate by remember { mutableStateOf(today) }

    val availableTimings = remember(turf?.availableTimings) {
        turf?.availableTimings?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }
            ?.map { LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm")) }
            ?: listOf(
                LocalTime.of(6, 0), LocalTime.of(7, 0), LocalTime.of(8, 0),
                LocalTime.of(17, 0), LocalTime.of(18, 0), LocalTime.of(19, 0), LocalTime.of(20, 0)
            )
    }

    val timeFormatter = DateTimeFormatter.ofPattern("h a")
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

    var selectedSlot by remember { mutableStateOf<LocalTime?>(null) }
    var selectedSportForBooking by remember { mutableStateOf<String?>(null) }

    val availableSportsInTurf = remember(turf?.sportsSupported) {
        turf?.sportsSupported?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
    }

    LaunchedEffect(selectedDate) {
        selectedSlot = null
    }

    LaunchedEffect(Unit) {
        turfViewModel.bookingSuccess.collectLatest { success ->
            if (success) {
                snackbarHostState.showSnackbar("Booking confirmed!")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(turf?.name ?: "Turf Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (turf == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val openDaysList = remember(turf.openDays) {
                turf.openDays.split(",").map { it.trim() }.filter { it.isNotBlank() }
            }
            val currentDayName = selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
            val isClosedToday = !openDaysList.contains(currentDayName)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ... (image logic stays same)
                val imageUrl = if (turf.imageUrls.isNotBlank()) {
                    turf.imageUrls.split(",").firstOrNull()?.trim()
                } else {
                    val sports = turf.sportsSupported.split(",").map { it.trim().lowercase() }
                    when {
                        sports.contains("football") && sports.contains("cricket") -> "https://images.unsplash.com/photo-1574629810360-7efbbe195018"
                        sports.contains("cricket") -> "https://images.unsplash.com/photo-1531415074968-036ba1b575da"
                        sports.contains("football") -> "https://images.unsplash.com/photo-1574629810360-7efbbe195018"
                        sports.contains("tennis") -> "https://images.unsplash.com/photo-1595435064212-36aa3664d603"
                        sports.contains("badminton") -> "https://images.unsplash.com/photo-1626225967045-9410dd99eaa6"
                        sports.contains("basketball") -> "https://images.unsplash.com/photo-1546519638-68e109498ffc"
                        else -> null
                    }
                }

                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Turf Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("No Image Available", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = turf.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = turf.location,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = turf.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (availableSportsInTurf.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Select Sport",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableSportsInTurf) { sport ->
                                FilterChip(
                                    selected = selectedSportForBooking == sport,
                                    onClick = { selectedSportForBooking = sport },
                                    label = { Text(sport) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Select Date",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dates) { date ->
                            FilterChip(
                                selected = selectedDate == date,
                                onClick = { selectedDate = date },
                                label = { Text(date.format(dateFormatter)) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Select Time Slot",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (isClosedToday) {
                        Text(
                            text = "Closed on $currentDayName",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        val now = LocalDateTime.now()
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableTimings) { slot ->
                                val slotDateTime = LocalDateTime.of(selectedDate, slot)
                                val isPast = slotDateTime.isBefore(now)
                                
                                val endSlot = slot.plusHours(1)
                                val slotText = "${slot.format(timeFormatter).lowercase()} to ${endSlot.format(timeFormatter).lowercase()}"

                                FilterChip(
                                    selected = selectedSlot == slot,
                                    onClick = { if (!isPast) selectedSlot = slot },
                                    label = { 
                                        Text(
                                            text = slotText,
                                            color = if (isPast) Color.Gray else Color.Unspecified
                                        ) 
                                    },
                                    enabled = !isPast
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (selectedSlot != null && user != null) {
                                val bookingStartTime = LocalDateTime.of(selectedDate, selectedSlot!!)
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli()
                                    
                                onNavigateToPayment(
                                    turf.id,
                                    selectedSportForBooking ?: availableSportsInTurf.firstOrNull() ?: "General",
                                    bookingStartTime,
                                    turf.pricePerHour
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = selectedSlot != null && (availableSportsInTurf.isEmpty() || selectedSportForBooking != null) && !isClosedToday,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Book Slot - ₹${turf.pricePerHour}", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
