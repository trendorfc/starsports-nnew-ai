package com.example.strykesportsai.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTurfScreen(
    viewModel: OwnerViewModel,
    turfId: Long = 0,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var sports by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val ownerTurfs by viewModel.ownerTurfs.collectAsState()
    
    LaunchedEffect(turfId) {
        if (turfId != 0L) {
            val existing = ownerTurfs.find { it.id == turfId }
            existing?.let {
                name = it.name
                location = it.location
                sports = it.sportsSupported
                price = it.pricePerHour.toString()
                description = it.description
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.operationSuccess.collectLatest { success ->
            if (success) {
                onSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (turfId == 0L) "List Your Turf" else "Edit Turf",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Turf Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = sports,
            onValueChange = { sports = it },
            label = { Text("Sports Supported (e.g. Football, Cricket)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price per Hour (₹)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.saveTurf(
                    name = name,
                    location = location,
                    sports = sports,
                    price = price.toDoubleOrNull() ?: 0.0,
                    description = description,
                    turfId = turfId
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = name.isNotBlank() && location.isNotBlank() && price.isNotBlank(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(if (turfId == 0L) "Add Turf" else "Save Changes", fontSize = 16.sp)
        }
    }
}
