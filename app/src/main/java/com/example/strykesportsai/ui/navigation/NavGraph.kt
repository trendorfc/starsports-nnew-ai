package com.example.strykesportsai.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.strykesportsai.StrykeApplication
import com.example.strykesportsai.data.local.entity.UserRole
import com.example.strykesportsai.ui.match.CreateMatchScreen
import com.example.strykesportsai.ui.onboarding.OnboardingScreen
import com.example.strykesportsai.ui.onboarding.OnboardingViewModel
import com.example.strykesportsai.ui.onboarding.OnboardingViewModelFactory
import com.example.strykesportsai.ui.owner.*
import com.example.strykesportsai.ui.player.*
import com.example.strykesportsai.ui.turf.*

@Composable
fun StrykeNavGraph(
    navController: NavHostController,
    application: StrykeApplication,
    startDestination: String
) {
    val playerViewModel: PlayerViewModel = viewModel(
        factory = PlayerViewModelFactory(application.repository)
    )
    val turfViewModel: TurfViewModel = viewModel(
        factory = TurfViewModelFactory(application.repository)
    )
    val ownerViewModel: OwnerViewModel = viewModel(
        factory = OwnerViewModelFactory(application.repository)
    )

    val userByPlayer by playerViewModel.user.collectAsState()
    val userByOwner by ownerViewModel.user.collectAsState()

    LaunchedEffect(userByPlayer) {
        if (userByPlayer == null) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            userByPlayer?.role?.let { role ->
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (role == UserRole.PLAYER && currentRoute?.startsWith("turf_owner") == true) {
                    navController.navigate(Screen.PlayerHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                } else if (role == UserRole.TURF_OWNER && (currentRoute == Screen.PlayerHome.route || currentRoute == Screen.Onboarding.route)) {
                    navController.navigate(Screen.TurfOwnerHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    LaunchedEffect(userByOwner) {
        if (userByOwner == null) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            userByOwner?.role?.let { role ->
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (role == UserRole.PLAYER && currentRoute?.startsWith("turf_owner") == true) {
                    navController.navigate(Screen.PlayerHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                } else if (role == UserRole.TURF_OWNER && (currentRoute == Screen.PlayerHome.route || currentRoute == Screen.Onboarding.route)) {
                    navController.navigate(Screen.TurfOwnerHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            val onboardingViewModel: OnboardingViewModel = viewModel(
                factory = OnboardingViewModelFactory(application.repository)
            )
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onOnboardingComplete = { role ->
                    val destination = when (role) {
                        UserRole.PLAYER -> Screen.PlayerHome.route
                        UserRole.TURF_OWNER -> Screen.TurfOwnerHome.route
                        else -> Screen.Onboarding.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Player Routes
        composable(Screen.PlayerHome.route) {
            PlayerHomeScreen(
                viewModel = playerViewModel,
                onNavigateToPlayers = { navController.navigate(Screen.PlayerDiscovery.route) },
                onNavigateToTurfs = { navController.navigate(Screen.TurfDiscovery.route) },
                onNavigateToCreateMatch = { navController.navigate(Screen.CreateMatch.route) }
            )
        }
        composable(Screen.PlayerDiscovery.route) {
            PlayerDiscoveryScreen(
                viewModel = playerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.TurfDiscovery.route) {
            TurfDiscoveryScreen(
                viewModel = playerViewModel,
                onNavigateToDetail = { turfId ->
                    navController.navigate(Screen.TurfDetail.createRoute(turfId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.TurfDetail.route,
            arguments = listOf(navArgument("turfId") { type = NavType.LongType })
        ) { backStackEntry ->
            val turfId = backStackEntry.arguments?.getLong("turfId") ?: 0L
            TurfDetailScreen(
                turfId = turfId,
                playerViewModel = playerViewModel,
                turfViewModel = turfViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.CreateMatch.route) {
            CreateMatchScreen(
                viewModel = playerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Owner Routes (Wrapped in TurfOwnerHomeScreen for Bottom Nav)
        composable(Screen.TurfOwnerHome.route) {
            TurfOwnerHomeScreen(viewModel = ownerViewModel, navController = navController) { padding ->
                OwnerDashboard(
                    viewModel = ownerViewModel,
                    onNavigateToListTurf = { navController.navigate(Screen.ListTurf.route) },
                    onNavigateToManageTurf = { navController.navigate(Screen.ManageTurf.route) }
                )
            }
        }
        composable(Screen.ListTurf.route) {
            TurfOwnerHomeScreen(viewModel = ownerViewModel, navController = navController) { padding ->
                ListTurfScreen(
                    viewModel = ownerViewModel,
                    onSuccess = { navController.navigate(Screen.ManageTurf.route) }
                )
            }
        }
        composable(Screen.ManageTurf.route) {
            TurfOwnerHomeScreen(viewModel = ownerViewModel, navController = navController) { padding ->
                ManageTurfScreen(
                    viewModel = ownerViewModel,
                    onEditTurf = { turfId -> navController.navigate(Screen.EditTurf.createRoute(turfId)) },
                    onViewBookings = { turfId -> navController.navigate(Screen.ViewBookings.createRoute(turfId)) }
                )
            }
        }
        composable(
            route = Screen.EditTurf.route,
            arguments = listOf(navArgument("turfId") { type = NavType.LongType })
        ) { backStackEntry ->
            val turfId = backStackEntry.arguments?.getLong("turfId") ?: 0L
            TurfOwnerHomeScreen(viewModel = ownerViewModel, navController = navController) { padding ->
                ListTurfScreen(
                    viewModel = ownerViewModel,
                    turfId = turfId,
                    onSuccess = { navController.navigate(Screen.ManageTurf.route) }
                )
            }
        }
        composable(
            route = Screen.ViewBookings.route,
            arguments = listOf(navArgument("turfId") { type = NavType.LongType })
        ) { backStackEntry ->
            val turfId = backStackEntry.arguments?.getLong("turfId") ?: 0L
            TurfOwnerHomeScreen(viewModel = ownerViewModel, navController = navController) { padding ->
                ViewBookingsScreen(
                    turfId = turfId,
                    viewModel = ownerViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
