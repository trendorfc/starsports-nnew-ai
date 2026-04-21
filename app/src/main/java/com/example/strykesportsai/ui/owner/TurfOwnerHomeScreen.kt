package com.example.strykesportsai.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.strykesportsai.ui.navigation.Screen
import com.example.strykesportsai.ui.navigation.ownerBottomNavItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurfOwnerHomeScreen(
    viewModel: OwnerViewModel,
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                ownerBottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.TurfOwnerHome.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        content(padding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboard(
    viewModel: OwnerViewModel,
    onNavigateToListTurf: () -> Unit,
    onNavigateToManageTurf: () -> Unit
) {
    val user by viewModel.user.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Owner Dashboard", fontWeight = FontWeight.Bold) },
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
                            text = { Text("Switch to Player Role") },
                            onClick = {
                                viewModel.switchRole()
                                showProfileMenu = false
                            },
                            leadingIcon = { Icon(Icons.Rounded.SwapHoriz, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                viewModel.logout()
                                showProfileMenu = false
                            },
                            leadingIcon = { Icon(Icons.Rounded.Logout, contentDescription = null) },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error,
                                leadingIconColor = MaterialTheme.colorScheme.error
                            )
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome, ${user?.name ?: "Partner"}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "Manage your sports facility efficiently.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            OwnerActionCard(
                title = "List Your Turf",
                description = "Add a new facility to our platform",
                onClick = onNavigateToListTurf,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            OwnerActionCard(
                title = "Manage Turfs",
                description = "Edit details and view bookings",
                onClick = onNavigateToManageTurf,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerActionCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = description, fontSize = 14.sp)
        }
    }
}

// Extension to get current route properly
@Composable
fun NavHostController.currentBackStackEntryAsState() = currentBackStackEntryFlow.collectAsState(null)
