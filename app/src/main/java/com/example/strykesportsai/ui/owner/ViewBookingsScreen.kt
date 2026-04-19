package com.example.strykesportsai.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strykesportsai.data.local.entity.BookingEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewBookingsScreen(
    turfId: Long,
    viewModel: OwnerViewModel,
    onNavigateBack: () -> Unit
) {
    val bookings by viewModel.getBookingsForTurf(turfId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incoming Bookings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No bookings for this turf yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    BookingListItem(booking)
                }
            }
        }
    }
}

@Composable
fun BookingListItem(booking: BookingEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Player ID: ${booking.userId}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "Status: ${booking.status}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                Text(text = "Price Paid: ₹${booking.totalPrice}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "Today, 6 PM", // Simplified for now
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
