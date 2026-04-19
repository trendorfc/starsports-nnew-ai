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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strykesportsai.ui.player.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurfDetailScreen(
    turfId: Long,
    playerViewModel: PlayerViewModel,
    turfViewModel: TurfViewModel,
    onNavigateBack: () -> Unit
) {
    val turfs by playerViewModel.turfs.collectAsState()
    val turf = turfs.find { it.id == turfId }
    val user by playerViewModel.user.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedSlot by remember { mutableStateOf<String?>(null) }
    val slots = listOf("06:00 AM", "07:00 AM", "08:00 AM", "05:00 PM", "06:00 PM", "07:00 PM", "08:00 PM")

    LaunchedEffect(Unit) {
        turfViewModel.bookingSuccess.collectLatest { success ->
            if (success) {
                snackbarHostState.showSnackbar("Booking confirmed for $selectedSlot!")
                // Optionally navigate back or to bookings screen
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Image Placeholder
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Turf Images Carousel", style = MaterialTheme.typography.titleMedium)
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

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Select Time Slot",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(slots) { slot ->
                            FilterChip(
                                selected = selectedSlot == slot,
                                onClick = { selectedSlot = slot },
                                label = { Text(slot) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (selectedSlot != null && user != null) {
                                turfViewModel.bookSlot(
                                    userId = user!!.id,
                                    turfId = turf.id,
                                    slot = selectedSlot!!,
                                    price = turf.pricePerHour
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = selectedSlot != null,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Book Slot - ₹${turf.pricePerHour}", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
