package com.example.strykesportsai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.strykesportsai.data.local.entity.UserRole
import com.example.strykesportsai.ui.navigation.Screen
import com.example.strykesportsai.ui.navigation.StrykeNavGraph
import com.example.strykesportsai.ui.theme.StrykeSportsAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val application = application as StrykeApplication
        
        // Insert sample data for demo purposes
        com.example.strykesportsai.util.SampleData.insertSampleTurfs(application.repository)
        
        setContent {
            StrykeSportsAITheme {
                val navController = rememberNavController()
                val user by application.repository.getUser().collectAsState(initial = null)
                
                val startDestination = when {
                    user == null -> Screen.PhoneLogin.route
                    user?.role == UserRole.PLAYER -> Screen.PlayerHome.route
                    user?.role == UserRole.TURF_OWNER -> Screen.TurfOwnerHome.route
                    else -> Screen.Onboarding.route
                }
                
                StrykeNavGraph(
                    navController = navController,
                    application = application,
                    startDestination = startDestination
                )
            }
        }
    }
}
