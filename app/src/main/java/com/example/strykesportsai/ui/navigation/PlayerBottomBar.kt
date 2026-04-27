package com.example.strykesportsai.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayerBottomBar(
    selectedScreen: Screen,
    onNavigateToHome: () -> Unit,
    onNavigateToMyMatches: () -> Unit,
    onNavigateToTurfs: () -> Unit,
    onNavigateToCreateMatch: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.height(80.dp)
        ) {
            NavigationBarItem(
                selected = selectedScreen == Screen.PlayerHome,
                onClick = onNavigateToHome,
                icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = selectedScreen == Screen.MyMatches,
                onClick = onNavigateToMyMatches,
                icon = { Icon(Icons.Rounded.Sports, contentDescription = null) },
                label = { Text("My Matches") }
            )
            
            // Placeholder for the central FAB
            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = { Spacer(modifier = Modifier.size(24.dp)) },
                label = { Text("") },
                enabled = false
            )

            NavigationBarItem(
                selected = selectedScreen == Screen.TurfDiscovery,
                onClick = onNavigateToTurfs,
                icon = { Icon(Icons.Rounded.SportsFootball, contentDescription = null) },
                label = { Text("Turfs") }
            )
            NavigationBarItem(
                selected = selectedScreen == Screen.Profile,
                onClick = onNavigateToProfile,
                icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                label = { Text("Profile") }
            )
        }

        // Circular Green FAB for Create Match
        FloatingActionButton(
            onClick = onNavigateToCreateMatch,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .offset(y = (-28).dp)
                .size(72.dp), // Larger size
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 12.dp)
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = "Create Match",
                modifier = Modifier.size(40.dp) // Larger icon
            )
        }
    }
}
