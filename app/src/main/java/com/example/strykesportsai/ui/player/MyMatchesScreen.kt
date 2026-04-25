package com.example.strykesportsai.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.strykesportsai.ui.navigation.PlayerBottomBar
import com.example.strykesportsai.ui.navigation.Screen

import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMatchesScreen(
    viewModel: PlayerViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMyMatches: () -> Unit,
    onNavigateToTurfs: () -> Unit,
    onNavigateToCreateMatch: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val myMatches by viewModel.myJoinedMatches.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Matches", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.switchRole() }) {
                        Icon(Icons.Rounded.SwapHoriz, contentDescription = "Switch Role")
                    }
                }
            )
        },
        bottomBar = {
            PlayerBottomBar(
                selectedScreen = Screen.MyMatches,
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
                .padding(horizontal = 20.dp)
        ) {
            if (myMatches.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Join matches to start playing",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp)
                ) {
                    items(myMatches) { match ->
                        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                        val timeString = sdf.format(Date(match.startTime))
                        
                        val currentTime = System.currentTimeMillis()
                        val diff = match.startTime - currentTime
                        val hours = diff / (1000 * 60 * 60)
                        val minutes = (diff / (1000 * 60)) % 60
                        val timeLeft = if (diff > 0) {
                            "Starts in ${if (hours > 0) "${hours}h " else ""}${minutes}m"
                        } else {
                            "Starting now"
                        }

                        MatchCard(
                            sport = match.sport,
                            location = match.location,
                            time = timeString,
                            playersNeeded = match.playersNeeded,
                            isCreator = match.creatorId == user?.id,
                            timeLeft = timeLeft,
                            showLeaveButton = true,
                            onLeaveClick = { reason ->
                                viewModel.leaveMatch(match.id, reason)
                            }
                        )
                    }
                }
            }
        }
    }
}
