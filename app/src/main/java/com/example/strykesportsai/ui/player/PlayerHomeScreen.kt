package com.example.strykesportsai.ui.player

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.strykesportsai.data.local.entity.MatchEntity
import com.example.strykesportsai.ui.navigation.PlayerBottomBar
import com.example.strykesportsai.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerHomeScreen(
    viewModel: PlayerViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMyMatches: () -> Unit,
    onNavigateToTurfs: () -> Unit,
    onNavigateToCreateMatch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDiscovery: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val matches by viewModel.upcomingMatches.collectAsState()
    val selectedSport by viewModel.selectedSport.collectAsState()

    Scaffold(
        bottomBar = {
            PlayerBottomBar(
                selectedScreen = Screen.PlayerHome,
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
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Good Morning,",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = user?.name ?: "Player One",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onNavigateToProfile() },
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        if (user?.profileImageUrl != null) {
                            AsyncImage(
                                model = user?.profileImageUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.padding(8.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActionCard(
                        title = "Find Players",
                        icon = Icons.Rounded.PersonSearch,
                        onClick = onNavigateToDiscovery,
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    ActionCard(
                        title = "Book Turf",
                        icon = Icons.Rounded.Stadium,
                        onClick = onNavigateToTurfs,
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }

            // Sports Filter
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Popular Sports",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 20.dp)
                    ) {
                        val sports = listOf("All", "Football", "Cricket", "Tennis", "Badminton", "Basketball")
                        items(sports) { sport ->
                            val isSelected = if (sport == "All") selectedSport == null else selectedSport == sport
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onSportSelected(if (sport == "All") null else sport) },
                                label = { Text(sport) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }
            }

            // Upcoming Matches Section
            item {
                Text(
                    text = "Upcoming Matches",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(matches) { match ->
                val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                val timeString = sdf.format(Date(match.startTime))
                
                MatchCard(
                    sport = match.sport,
                    location = match.location,
                    time = timeString,
                    playersNeeded = match.playersNeeded,
                    isCreator = match.creatorId == user?.id,
                    onJoinClick = { viewModel.joinMatch(match.id) }
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
        modifier = modifier.height(115.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative background icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 18.dp)
                    .alpha(0.12f),
                tint = MaterialTheme.colorScheme.onSurface
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.sp
                )
            }
        }
    }
}

@Composable
fun MatchCard(
    sport: String,
    location: String,
    time: String,
    playersNeeded: Int,
    isCreator: Boolean,
    onJoinClick: () -> Unit = {},
    onLeaveClick: (String) -> Unit = {},
    timeLeft: String? = null,
    isPast: Boolean = false,
    showLeaveButton: Boolean = false,
    statusText: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var showConfirmation by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Join Match") },
            text = { Text("Are you sure you want to join this $sport match at $location?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        Toast.makeText(context, "Match joined, enjoy the match!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLeaveDialog) {
        LeaveMatchDialog(
            onDismiss = { showLeaveDialog = false },
            onConfirm = { reason ->
                onLeaveClick(reason)
                showLeaveDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isPast) 0.6f else 1f)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
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

            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Match Sport Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (expanded) 180.dp else 150.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                    contentScale = ContentScale.Crop
                )
                
                if (!expanded && !isPast) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "$playersNeeded SLOTS",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sport,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (!expanded && !isPast) {
                        Button(
                            onClick = { expanded = true },
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text("Details", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isPast) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = statusText ?: "Completed",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    DetailRow(icon = Icons.Rounded.Schedule, label = "Starting Time", value = "07:00 PM")
                    DetailRow(icon = Icons.Rounded.NotificationsActive, label = "Reporting Time", value = "06:45 PM")
                    DetailRow(icon = Icons.Rounded.Stadium, label = "Turf Name", value = location)
                    DetailRow(icon = Icons.Rounded.Person, label = "Organizer", value = if (isCreator) "You" else "Pro Player")
                    DetailRow(icon = Icons.Rounded.Groups, label = "Joined", value = "5 Players")
                    DetailRow(icon = Icons.Rounded.EventSeat, label = "Slots Left", value = "$playersNeeded Slots")

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { expanded = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Show Less")
                        }
                        Button(
                            onClick = { 
                                if (showLeaveButton) {
                                    showLeaveDialog = true
                                } else {
                                    onJoinClick()
                                    showConfirmation = true 
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isCreator || showLeaveButton
                        ) {
                            Text(
                                text = when {
                                    showLeaveButton -> "Leave Match"
                                    isCreator -> "Already Joined"
                                    else -> "Join Match"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaveMatchDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val reasons = listOf(
        "I don't want to play the match",
        "Joined by mistake",
        "I have an urgent meeting",
        "Due to some emergency"
    )
    var selectedReason by remember { mutableStateOf("") }
    var customReason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Leave Match") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Why are you leaving?", style = MaterialTheme.typography.bodyMedium)
                reasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Text(text = reason, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedReason = "Other" }
                ) {
                    RadioButton(
                        selected = selectedReason == "Other",
                        onClick = { selectedReason = "Other" }
                    )
                    Text(text = "Other", style = MaterialTheme.typography.bodySmall)
                }
                if (selectedReason == "Other") {
                    OutlinedTextField(
                        value = customReason,
                        onValueChange = { customReason = it },
                        label = { Text("Custom Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalReason = if (selectedReason == "Other") customReason else selectedReason
                    if (finalReason.isNotBlank()) {
                        onConfirm(finalReason)
                    }
                },
                enabled = (selectedReason.isNotBlank() && selectedReason != "Other") || (selectedReason == "Other" && customReason.isNotBlank())
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PlayerHomeScreenPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val currentTime = System.currentTimeMillis()
            val dummyMatches = listOf(
                MatchEntity(
                    id = 1,
                    creatorId = 0,
                    turfId = 1,
                    location = "Powerplay Sports, Bangalore",
                    sport = "Football",
                    startTime = currentTime + 3600000,
                    endTime = currentTime + 7200000,
                    playersNeeded = 5,
                    currentPlayers = 5,
                    description = "Casual 5v5 football match."
                ),
                MatchEntity(
                    id = 2,
                    creatorId = 1,
                    turfId = 2,
                    location = "Decathlon, Whitefield",
                    sport = "Cricket",
                    startTime = currentTime + 7200000,
                    endTime = currentTime + 10800000,
                    playersNeeded = 11,
                    currentPlayers = 0,
                    description = "Competitive T20 match."
                )
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Good Morning,",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Player One",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                        }
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.padding(8.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Quick Actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            title = "Find Players",
                            icon = Icons.Rounded.PersonSearch,
                            onClick = {},
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        )
                        ActionCard(
                            title = "Book Turf",
                            icon = Icons.Rounded.Stadium,
                            onClick = {},
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                // Sports Filter
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Football", "Cricket").forEach { sport ->
                            FilterChip(
                                selected = sport == "All",
                                onClick = {},
                                label = { Text(sport) }
                            )
                        }
                    }
                }

                // Section Title
                item {
                    Text(
                        text = "Upcoming Matches",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(dummyMatches) { match ->
                    MatchCard(
                        sport = match.sport,
                        location = match.location,
                        time = "07:00 PM",
                        playersNeeded = match.playersNeeded,
                        isCreator = false
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
