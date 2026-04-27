package com.example.strykesportsai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding")
    object PlayerHome : Screen("player_home", "Home", Icons.Rounded.Home)
    object PlayerDiscovery : Screen("player_discovery", "Players", Icons.Rounded.People)
    object TurfOwnerHome : Screen("turf_owner_home", "Home", Icons.Rounded.Home)
    object MyMatches : Screen("my_matches", "My Matches", Icons.Rounded.Sports)
    object TurfDiscovery : Screen("turf_discovery", "Turfs", Icons.Rounded.SportsFootball)
    object TurfDetail : Screen("turf_detail/{turfId}") {
        fun createRoute(turfId: Long) = "turf_detail/$turfId"
    }
    object CreateMatch : Screen("create_match", "Match", Icons.Rounded.AddBox)
    object Profile : Screen("profile", "Profile", Icons.Rounded.Person)
    object PastBookings : Screen("past_bookings")
    object PastMatches : Screen("past_matches")
    object MockPayment : Screen("mock_payment/{turfId}/{sport}/{startTime}/{price}") {
        fun createRoute(turfId: Long, sport: String, startTime: Long, price: Double) = 
            "mock_payment/$turfId/$sport/$startTime/$price"
    }
    object BookingConfirmation : Screen("booking_confirmation")
    
    // Owner Screens
    object ListTurf : Screen("list_turf", "Add Turf", Icons.Rounded.Add)
    object ManageTurf : Screen("manage_turf", "Manage", Icons.Rounded.Settings)
    object OwnerProfile : Screen("owner_profile", "Profile", Icons.Rounded.Person)
    object EditTurf : Screen("edit_turf/{turfId}") {
        fun createRoute(turfId: Long) = "edit_turf/$turfId"
    }
    object ViewBookings : Screen("view_bookings/{turfId}") {
        fun createRoute(turfId: Long) = "view_bookings/$turfId"
    }
    object ManageTimings : Screen("manage_timings/{turfId}") {
        fun createRoute(turfId: Long) = "manage_timings/$turfId"
    }
}

val ownerBottomNavItems = listOf(
    Screen.TurfOwnerHome,
    Screen.ListTurf,
    Screen.ManageTurf,
    Screen.OwnerProfile
)
