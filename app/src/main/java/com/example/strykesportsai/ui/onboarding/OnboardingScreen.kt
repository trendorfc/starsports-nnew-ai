package com.example.strykesportsai.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strykesportsai.R
import com.example.strykesportsai.data.local.entity.UserRole
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.ui.tooling.preview.Preview
import com.example.strykesportsai.ui.theme.StrykeSportsAITheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel?,
    onOnboardingComplete: (UserRole) -> Unit
) {
    val firstName = viewModel?.firstName?.collectAsState()?.value ?: ""
    val lastName = viewModel?.lastName?.collectAsState()?.value ?: ""
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
            Image(
                painter = painterResource(id = R.drawable.stryke_logo),
                contentDescription = "Stryke Sports Logo",
                modifier = Modifier
                    .size(140.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "Welcome! Let's get started.",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { viewModel?.onFirstNameChange(it) },
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { viewModel?.onLastNameChange(it) },
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            var showDatePicker by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = if (dob.isNotEmpty()) {
                    try {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dob)?.time
                    } catch (e: Exception) { null }
                } else null
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                                viewModel?.onDobChange(formattedDate)
                            }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            OutlinedTextField(
                value = dob,
                onValueChange = { },
                label = { Text("Date of Birth") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Rounded.CalendarToday, contentDescription = "Select Date")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            if (dob.isNotEmpty()) {
                val age = remember(dob) {
                    try {
                        val birthDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dob)
                        if (birthDate != null) {
                            val birthCalendar = Calendar.getInstance().apply { time = birthDate }
                            val today = Calendar.getInstance()
                            var years = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
                            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                                years--
                            }
                            years
                        } else null
                    } catch (e: Exception) { null }
                }
                
                age?.let {
                    Text(
                        text = "You are $it years old",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 2.dp)
                    )
                }
            }

            Text(
                text = "Sports You Play",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )

            val availableSports = listOf(
                "Football", "Badminton", "Cricket", "Lawn Tennis", 
                "Table Tennis", "Running", "Marathon", "Basketball", 
                "Volleyball", "Swimming", "Yoga", "Cycling"
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                availableSports.forEach { sport ->
                    val isSelected = sportsInterests.split(", ").contains(sport)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel?.toggleSportInterest(sport) },
                        label = { Text(sport) },
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    )
                }
            }

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
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && dob.isNotBlank() && sportsInterests.isNotBlank() && selectedRole != UserRole.UNDEFINED,
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
