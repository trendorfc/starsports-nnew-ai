package com.example.strykesportsai.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.strykesportsai.ui.navigation.PlayerBottomBar
import com.example.strykesportsai.ui.navigation.Screen
import com.example.strykesportsai.data.local.entity.UserEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDiscoveryScreen(
    viewModel: PlayerViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMyMatches: () -> Unit,
    onNavigateToTurfs: () -> Unit,
    onNavigateToCreateMatch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val players by viewModel.players.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Find Players") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            PlayerBottomBar(
                selectedScreen = Screen.PlayerDiscovery,
                onNavigateToHome = onNavigateToHome,
                onNavigateToMyMatches = onNavigateToMyMatches,
                onNavigateToTurfs = onNavigateToTurfs,
                onNavigateToCreateMatch = onNavigateToCreateMatch,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(players) { player ->
                PlayerListItem(
                    player = player,
                    onConnectClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Connection request sent to ${player.name}")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PlayerListItem(
    player: UserEntity,
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = player.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "Plays: ${player.sportsInterests}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Skill Level: Intermediate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            Button(onClick = onConnectClick) {
                Text("Connect")
            }
        }
    }
}
