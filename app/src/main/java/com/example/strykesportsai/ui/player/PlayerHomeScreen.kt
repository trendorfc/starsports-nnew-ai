package com.example.strykesportsai.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.strykesportsai.ui.navigation.PlayerBottomBar
import com.example.strykesportsai.ui.navigation.Screen
import com.example.strykesportsai.data.local.entity.UserEntity

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlayerHomeScreen(
    viewModel: PlayerViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToTurfs: () -> Unit,
    onNavigateToCreateMatch: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val selectedSport by viewModel.selectedSport.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, ${user?.name ?: "Player"}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ready for some action today?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                selectedScreen = Screen.PlayerHome,
                onNavigateToHome = onNavigateToHome,
                onNavigateToPlayers = onNavigateToPlayers,
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val availableSports = listOf("Football", "Cricket", "Tennis", "Badminton", "Basketball")

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

            Text(
                text = "Nearby Matches",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            val matches by viewModel.filteredMatches.collectAsState()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(matches) { match ->
                    MatchCard(
                        sport = match.sport,
                        location = match.location,
                        time = "Today, 7 PM", // Format timestamp in real app
                        playersNeeded = match.playersNeeded,
                        isCreator = match.creatorId == user?.id
                    )
                }
                if (matches.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matches found${if (selectedSport != null) " for $selectedSport" else ""}.\nBe the first to create one!",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun MatchCard(
    sport: String,
    location: String,
    time: String,
    playersNeeded: Int,
    isCreator: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            val imageUrl = remember(sport) {
                when (sport.lowercase().trim()) {
                    "football" -> "https://images.unsplash.com/photo-1574629810360-7efbbe195018"
                    "cricket" -> "https://images.unsplash.com/photo-1531415074968-036ba1b575da"
                    "tennis" -> "https://images.unsplash.com/photo-1595435064212-36aa3664d603"
                    "badminton" -> "https://images.unsplash.com/photo-1626225967045-9410dd99eaa6"
                    "basketball" -> "https://images.unsplash.com/photo-1546519638-68e109498ffc"
                    else -> "https://images.unsplash.com/photo-1574629810360-7efbbe195018"
                }
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = "Match Sport Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = sport, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(text = location, style = MaterialTheme.typography.bodySmall)
                    Text(text = time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    if (isCreator) {
                        Text(
                            text = "You created this match",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "$playersNeeded slots", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Button(
                        onClick = { },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        enabled = !isCreator
                    ) {
                        Text(if (isCreator) "Joined" else "Join")
                    }
                }
            }
        }
    }
}
