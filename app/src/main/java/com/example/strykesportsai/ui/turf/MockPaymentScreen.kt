package com.example.strykesportsai.ui.turf

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strykesportsai.ui.player.PlayerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockPaymentScreen(
    turfId: Long,
    sport: String,
    startTime: Long,
    price: Double,
    playerViewModel: PlayerViewModel,
    turfViewModel: TurfViewModel,
    onPaymentSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var showConfirmation by remember { mutableStateOf(false) }
    val user by playerViewModel.user.collectAsState()

    if (showConfirmation) {
        BookingConfirmationScreen(onViewBookings = onPaymentSuccess)
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Secure Payment") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, enabled = !isProcessing) {
                            Icon(Icons.Rounded.Lock, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total Amount",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${price.toInt()}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Sport", sport)
                        DetailRow("Duration", "1 Hour")
                        DetailRow("Payment Method", "Mock UPI / Card")
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                if (isProcessing) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Processing Payment...", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Button(
                        onClick = {
                            isProcessing = true
                            // Simulate network delay
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Pay Now", fontSize = 18.sp)
                    }
                }
            }
        }
    }

    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            delay(2000)
            user?.let {
                turfViewModel.bookSlot(it.id, turfId, sport, startTime, price)
                showConfirmation = true
            }
        }
    }
}

@Composable
fun BookingConfirmationScreen(onViewBookings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Booking Confirmed!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your slot has been successfully reserved.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onViewBookings,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("View My Bookings", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
