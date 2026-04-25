package com.example.strykesportsai.ui.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.strykesportsai.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastMatchesScreen(
    viewModel: PlayerViewModel,
    onNavigateBack: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val pastMatches by viewModel.pastMatches.collectAsState()
    val leftMatchIds by viewModel.leftMatchIds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Past Matches", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
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
            items(pastMatches) { match ->
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val timeString = sdf.format(Date(match.startTime))
                val isLeft = leftMatchIds.contains(match.id)

                MatchCard(
                    sport = match.sport,
                    location = match.location,
                    time = timeString,
                    playersNeeded = match.playersNeeded,
                    isCreator = match.creatorId == user?.id,
                    isPast = true,
                    statusText = if (isLeft) "Left" else "Completed"
                )
            }
            if (pastMatches.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "No past matches found",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
