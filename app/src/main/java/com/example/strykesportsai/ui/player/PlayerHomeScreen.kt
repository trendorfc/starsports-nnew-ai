package com.example.strykesportsai.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.SportsFootball
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strykesportsai.data.local.entity.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerHomeScreen(
    viewModel: PlayerViewModel,
    onNavigateToPlayers: () -> Unit,
    onNavigateToTurfs: () -> Unit,
    onNavigateToCreateMatch: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

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
                    var showProfileMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showProfileMenu = true }) {
                        Icon(Icons.Rounded.AccountCircle, contentDescription = "Profile")
                    }
                    DropdownMenu(
                        expanded = showProfileMenu,
                        onDismissRequest = { showProfileMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Switch to Owner Role") },
                            onClick = {
                                viewModel.switchRole()
                                showProfileMenu = false
                            },
                            leadingIcon = { Icon(Icons.Rounded.SwapHoriz, contentDescription = null) }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search players, turfs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.large,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    title = "Find Players",
                    icon = Icons.Rounded.Groups,
                    onClick = onNavigateToPlayers,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
                ActionCard(
                    title = "Find Turfs",
                    icon = Icons.Rounded.SportsFootball,
                    onClick = onNavigateToTurfs,
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }

            ActionCard(
                title = "Create Match",
                icon = Icons.Rounded.AddBox,
                onClick = onNavigateToCreateMatch,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )

            Text(
                text = "Nearby Matches",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            val matches by viewModel.matches.collectAsState()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(matches) { match ->
                    MatchCard(
                        sport = match.sport,
                        location = match.location,
                        time = "Today, 7 PM", // Format timestamp in real app
                        playersNeeded = match.playersNeeded
                    )
                }
                if (matches.isEmpty()) {
                    items(3) { index ->
                        MatchCard(
                            sport = if (index % 2 == 0) "Football" else "Cricket",
                            location = "Wembley Arena",
                            time = "Tomorrow, 6 PM",
                            playersNeeded = 4
                        )
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
    playersNeeded: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
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
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "$playersNeeded slots", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Button(onClick = { }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Text("Join")
                }
            }
        }
    }
}
