package com.example.strykesportsai.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object PlayerHome : Screen("player_home")
    object TurfOwnerHome : Screen("turf_owner_home")
    object PlayerDiscovery : Screen("player_discovery")
    object TurfDiscovery : Screen("turf_discovery")
    object TurfDetail : Screen("turf_detail/{turfId}") {
        fun createRoute(turfId: Long) = "turf_detail/$turfId"
    }
    object CreateMatch : Screen("create_match")
    object ListTurf : Screen("list_turf")
    object ManageTurf : Screen("manage_turf")
    object EditTurf : Screen("edit_turf/{turfId}") {
        fun createRoute(turfId: Long) = "edit_turf/$turfId"
    }
    object ViewBookings : Screen("view_bookings/{turfId}") {
        fun createRoute(turfId: Long) = "view_bookings/$turfId"
    }
}
