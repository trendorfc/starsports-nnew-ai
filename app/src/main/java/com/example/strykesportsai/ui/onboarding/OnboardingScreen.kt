package com.example.strykesportsai.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strykesportsai.data.local.entity.UserRole

import androidx.compose.ui.tooling.preview.Preview
import com.example.strykesportsai.ui.theme.StrykeSportsAITheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel?,
    onOnboardingComplete: (UserRole) -> Unit
) {
    val name = viewModel?.name?.collectAsState()?.value ?: ""
    val dob = viewModel?.dob?.collectAsState()?.value ?: ""
    val sportsInterests = viewModel?.sportsInterests?.collectAsState()?.value ?: ""
    val selectedRole = viewModel?.selectedRole?.collectAsState()?.value ?: UserRole.UNDEFINED
    val onboardingCompleted = viewModel?.onboardingCompleted?.collectAsState()?.value ?: false

    LaunchedEffect(onboardingCompleted) {
        if (onboardingCompleted) {
            onOnboardingComplete(selectedRole)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Stryke Sports", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Welcome! Let's get started.",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { viewModel?.onNameChange(it) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = dob,
                onValueChange = { viewModel?.onDobChange(it) },
                label = { Text("Date of Birth (DD/MM/YYYY)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = sportsInterests,
                onValueChange = { viewModel?.onSportsInterestsChange(it) },
                label = { Text("Favorite Sports (e.g., Football, Cricket)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select Your Role",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RoleCard(
                    roleName = "Player",
                    isSelected = selectedRole == UserRole.PLAYER,
                    onClick = { viewModel?.onRoleSelected(UserRole.PLAYER) },
                    modifier = Modifier.weight(1f)
                )
                RoleCard(
                    roleName = "Turf Owner",
                    isSelected = selectedRole == UserRole.TURF_OWNER,
                    onClick = { viewModel?.onRoleSelected(UserRole.TURF_OWNER) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel?.completeOnboarding() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = name.isNotBlank() && dob.isNotBlank() && selectedRole != UserRole.UNDEFINED,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Complete Onboarding", fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun OnboardingScreenPreview() {
    StrykeSportsAITheme {
        OnboardingScreen(
            viewModel = null,
            onOnboardingComplete = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleCard(
    roleName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = roleName,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
