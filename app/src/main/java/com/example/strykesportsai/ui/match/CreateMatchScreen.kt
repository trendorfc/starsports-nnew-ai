package com.example.strykesportsai.ui.match

import androidx.compose.foundation.layout.*
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
import com.example.strykesportsai.data.local.entity.TurfEntity
import com.example.strykesportsai.ui.player.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateMatchScreen(
    viewModel: PlayerViewModel,
    onNavigateBack: () -> Unit
) {
    var sport by remember { mutableStateOf("") }
    var selectedTurf by remember { mutableStateOf<TurfEntity?>(null) }
    var needsPlayers by remember { mutableStateOf<Boolean?>(null) }
    var playersCount by remember { mutableIntStateOf(1) }
    var description by remember { mutableStateOf("") }
    
    val turfs by viewModel.turfs.collectAsState()
    val filteredTurfs = remember(sport, turfs) {
        if (sport.isEmpty()) emptyList()
        else turfs.filter { it.sportsSupported.contains(sport, ignoreCase = true) }
            .distinctBy { it.name }
    }

    var turfDropdownExpanded by remember { mutableStateOf(false) }
    var playersDropdownExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.createMatchSuccess.collectLatest { success ->
            if (success) {
                onNavigateBack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create Match") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Organize a Match",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Sport Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select Sport",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                val availableSports = listOf("Football", "Cricket", "Tennis", "Badminton", "Basketball")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableSports.forEach { s ->
                        FilterChip(
                            selected = sport == s,
                            onClick = { 
                                sport = s
                                selectedTurf = null // Reset turf when sport changes
                            },
                            label = { Text(s) },
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }
            }

            // Turf Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select Turf",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                ExposedDropdownMenuBox(
                    expanded = turfDropdownExpanded && sport.isNotEmpty(),
                    onExpandedChange = { if (sport.isNotEmpty()) turfDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedTurf?.name ?: "Select a turf",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = turfDropdownExpanded) },
                        enabled = sport.isNotEmpty(),
                        placeholder = { Text(if (sport.isEmpty()) "Select sport first" else "Choose a turf") }
                    )
                    ExposedDropdownMenu(
                        expanded = turfDropdownExpanded && sport.isNotEmpty(),
                        onDismissRequest = { turfDropdownExpanded = false }
                    ) {
                        filteredTurfs.forEach { turf ->
                            DropdownMenuItem(
                                text = { Text(turf.name) },
                                onClick = {
                                    selectedTurf = turf
                                    turfDropdownExpanded = false
                                }
                            )
                        }
                        if (filteredTurfs.isEmpty() && sport.isNotEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No turfs available for this sport") },
                                onClick = { turfDropdownExpanded = false },
                                enabled = false
                            )
                        }
                    }
                }
            }

            // Needs Players Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Do you need more players?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { needsPlayers = true },
                        modifier = Modifier.weight(1f),
                        colors = if (needsPlayers == true) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
                        border = if (needsPlayers == true) null else ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text("Yes")
                    }
                    OutlinedButton(
                        onClick = { needsPlayers = false },
                        modifier = Modifier.weight(1f),
                        colors = if (needsPlayers == false) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
                        border = if (needsPlayers == false) null else ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text("No")
                    }
                }
            }

            // Players Count Dropdown
            if (needsPlayers == true) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "How many players do you need?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    ExposedDropdownMenuBox(
                        expanded = playersDropdownExpanded,
                        onExpandedChange = { playersDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = playersCount.toString(),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = playersDropdownExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = playersDropdownExpanded,
                            onDismissRequest = { playersDropdownExpanded = false }
                        ) {
                            (1..11).forEach { count ->
                                DropdownMenuItem(
                                    text = { Text(count.toString()) },
                                    onClick = {
                                        playersCount = count
                                        playersDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Match Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                placeholder = { Text("Add any extra details...") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.createMatch(
                        sport = sport,
                        dateTime = System.currentTimeMillis(),
                        location = selectedTurf?.location ?: "Unknown",
                        playersNeeded = if (needsPlayers == true) playersCount else 0,
                        description = description
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = sport.isNotBlank() && selectedTurf != null && needsPlayers != null,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Create Match", fontSize = 16.sp)
            }
        }
    }
}
