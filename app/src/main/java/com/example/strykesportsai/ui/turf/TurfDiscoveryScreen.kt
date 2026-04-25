package com.example.strykesportsai.ui.turf

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SportsFootball
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.strykesportsai.ui.navigation.PlayerBottomBar
import com.example.strykesportsai.ui.navigation.Screen
import com.example.strykesportsai.data.local.entity.TurfEntity
import com.example.strykesportsai.ui.player.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TurfDiscoveryScreen(
    viewModel: PlayerViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMyMatches: () -> Unit,
    onNavigateToTurfs: () -> Unit,
    onNavigateToCreateMatch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val turfs by viewModel.filteredTurfs.collectAsState(initial = emptyList())
    val selectedSport by viewModel.selectedSport.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Turfs") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.switchRole() }) {
                        Icon(Icons.Rounded.SwapHoriz, contentDescription = "Switch Role")
                    }
                }
            )
        },
        bottomBar = {
            PlayerBottomBar(
                selectedScreen = Screen.TurfDiscovery,
                onNavigateToHome = onNavigateToHome,
                onNavigateToMyMatches = onNavigateToMyMatches,
                onNavigateToTurfs = onNavigateToTurfs,
                onNavigateToCreateMatch = onNavigateToCreateMatch,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val availableSports = listOf("Football", "Cricket", "Tennis", "Badminton", "Basketball")

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Filter by Sport",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableSports.forEach { sport ->
                        FilterChip(
                            selected = selectedSport == sport,
                            onClick = {
                                if (selectedSport == sport) viewModel.onSportSelected(null)
                                else viewModel.onSportSelected(sport)
                            },
                            label = { Text(sport) },
                            leadingIcon = if (selectedSport == sport) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
            }

            if (turfs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nothing here yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else {
                val distinctTurfs = remember(turfs) { turfs.distinctBy { it.name + it.location } }
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(distinctTurfs) { turf ->
                        TurfListItem(
                            turf = turf,
                            onViewDetails = { onNavigateToDetail(turf.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TurfListItem(
    turf: TurfEntity,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            val imageUrl = remember(turf.sportsSupported) {
                val sports = turf.sportsSupported.split(",").map { it.trim().lowercase() }
                when {
                    sports.contains("football") && sports.contains("cricket") -> "https://images.unsplash.com/photo-1574629810360-7efbbe195018"
                    sports.contains("cricket") -> "https://images.unsplash.com/photo-1531415074968-036ba1b575da"
                    sports.contains("football") -> "https://images.unsplash.com/photo-1574629810360-7efbbe195018"
                    sports.contains("tennis") -> "https://images.unsplash.com/photo-1595435064212-36aa3664d603"
                    sports.contains("badminton") -> "https://images.unsplash.com/photo-1626225967045-9410dd99eaa6"
                    sports.contains("basketball") -> "https://images.unsplash.com/photo-1546519638-68e109498ffc"
                    else -> turf.imageUrls.split(",").firstOrNull()?.trim()
                }
            }

            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Turf Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("No Image Available", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = turf.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(text = turf.location, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = turf.sportsSupported,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${turf.pricePerHour}/hr",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = onViewDetails) {
                        Text("View Details")
                    }
                }
            }
        }
    }
}
