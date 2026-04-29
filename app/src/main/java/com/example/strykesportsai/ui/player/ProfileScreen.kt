package com.example.strykesportsai.ui.player

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.strykesportsai.data.local.entity.UserEntity
import com.example.strykesportsai.ui.navigation.Screen
import com.example.strykesportsai.ui.navigation.PlayerBottomBar
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: PlayerViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMyMatches: () -> Unit,
    onNavigateToTurfs: () -> Unit,
    onNavigateToCreateMatch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPastBookings: () -> Unit,
    onNavigateToPastMatches: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current
    var showEditNameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCropDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            showCropDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.switchRole() }) {
                        Icon(Icons.Rounded.SwapHoriz, contentDescription = "Switch Role")
                    }
                }
            )
        },
        bottomBar = {
            PlayerBottomBar(
                selectedScreen = Screen.Profile,
                onNavigateToHome = onNavigateToHome,
                onNavigateToMyMatches = onNavigateToMyMatches,
                onNavigateToTurfs = onNavigateToTurfs,
                onNavigateToCreateMatch = onNavigateToCreateMatch,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profile Photo
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (user?.profileImageUrl != null) {
                    AsyncImage(
                        model = user?.profileImageUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Rounded.AccountCircle,
                        contentDescription = "Default Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
                
                SmallFloatingActionButton(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Rounded.CameraAlt, contentDescription = "Change Photo", modifier = Modifier.size(18.dp))
                }
            }

            UserInfoSection(user)

            HorizontalDivider()

            ProfileActionsSection(
                user = user,
                viewModel = viewModel,
                onEditName = { 
                    newName = user?.name ?: ""
                    showEditNameDialog = true 
                },
                onViewPastBookings = onNavigateToPastBookings,
                onViewPastMatches = onNavigateToPastMatches,
                onSwitchRole = { viewModel.switchRole() },
                onLogout = { viewModel.logout() }
            )
        }
    }

    if (showCropDialog && selectedImageUri != null) {
        CropPreviewDialog(
            imageUri = selectedImageUri!!,
            onDismiss = { 
                showCropDialog = false
                selectedImageUri = null
            },
            onConfirm = { croppedUri ->
                val internalUri = saveImageToInternalStorage(context, croppedUri)
                viewModel.updateProfile(user?.name ?: "User", internalUri?.toString())
                showCropDialog = false
                selectedImageUri = null
            }
        )
    }

    if (showEditNameDialog) {
        EditNameDialog(
            currentName = newName,
            onNameChange = { newName = it },
            onDismiss = { showEditNameDialog = false },
            onConfirm = {
                viewModel.updateProfile(newName, user?.profileImageUrl)
                showEditNameDialog = false
            }
        )
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "profile_picture_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun UserInfoSection(user: UserEntity?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = user?.name ?: "User Name",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Birthday: ${user?.dob ?: "Not set"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Phone: +91 ${user?.phoneNumber ?: "Not set"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Interests: ${user?.sportsInterests ?: "None"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileActionsSection(
    user: UserEntity?,
    viewModel: PlayerViewModel,
    onEditName: () -> Unit,
    onViewPastBookings: () -> Unit,
    onViewPastMatches: () -> Unit,
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ProfileOptionItem(
            icon = Icons.Rounded.Edit,
            title = "Change Name",
            onClick = onEditName
        )
        
        ProfileOptionItem(
            icon = Icons.Rounded.History,
            title = "Past Bookings",
            onClick = onViewPastBookings
        )

        ProfileOptionItem(
            icon = Icons.Rounded.Sports,
            title = "Past Matches",
            onClick = onViewPastMatches
        )

        ProfileOptionItem(
            icon = Icons.Rounded.SwapHoriz,
            title = "Switch to Owner Role",
            onClick = onSwitchRole
        )
        
        ProfileOptionItem(
            icon = Icons.AutoMirrored.Rounded.Logout,
            title = "Logout",
            onClick = onLogout,
            textColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun CropPreviewDialog(
    imageUri: Uri,
    onDismiss: () -> Unit,
    onConfirm: (Uri) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Preview Profile Photo", style = MaterialTheme.typography.titleLarge)
                
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Preview Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    "This is how your photo will look to others.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(imageUri) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Use Photo")
                    }
                }
            }
        }
    }
}

@Composable
fun EditNameDialog(
    currentName: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Name") },
        text = {
            OutlinedTextField(
                value = currentName,
                onValueChange = onNameChange,
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (textColor == MaterialTheme.colorScheme.error) textColor else MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }
}
