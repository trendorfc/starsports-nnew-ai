package com.example.strykesportsai.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTurfScreen(
    viewModel: OwnerViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    turfId: Long = 0,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var sports by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val ownerTurfs by viewModel.ownerTurfs.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }
    
    LaunchedEffect(turfId) {
        if (turfId != 0L) {
            val existing = ownerTurfs.find { it.id == turfId }
            existing?.let {
                name = it.name
                location = it.location
                sports = it.sportsSupported
                price = it.pricePerHour.toString()
                description = it.description
                selectedImageUri = if (it.imageUrls.isNotBlank()) Uri.parse(it.imageUrls) else null
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.operationSuccess.collectLatest { success ->
            if (success) {
                onSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (turfId == 0L) "List Your Turf" else "Edit Turf",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Image Selection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                .clickable { photoPickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Turf Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add Turf Image", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Turf Name *") },
            modifier = Modifier.fillMaxWidth(),
            isError = name.isBlank()
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location *") },
            modifier = Modifier.fillMaxWidth(),
            isError = location.isBlank()
        )

        var expanded by remember { mutableStateOf(false) }
        val allSports = listOf("Football", "Cricket", "Tennis", "Badminton", "Basketball", "Volleyball", "Kabaddi")
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = sports,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sports Supported *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                isError = sports.isBlank()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                allSports.forEach { sport ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = sports.split(", ").contains(sport),
                                    onCheckedChange = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(sport)
                            }
                        },
                        onClick = {
                            val currentSports = sports.split(", ").filter { it.isNotBlank() }.toMutableList()
                            if (currentSports.contains(sport)) {
                                currentSports.remove(sport)
                            } else {
                                currentSports.add(sport)
                            }
                            sports = currentSports.joinToString(", ")
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = price,
            onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
            label = { Text("Price per Hour (₹) *") },
            modifier = Modifier.fillMaxWidth(),
            isError = price.isBlank(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description *") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            isError = description.isBlank()
        )

        Spacer(modifier = Modifier.height(24.dp))

        val isFormValid = name.isNotBlank() && 
                         location.isNotBlank() && 
                         sports.isNotBlank() && 
                         price.isNotBlank() && 
                         description.isNotBlank() && 
                         selectedImageUri != null

        Button(
            onClick = {
                val finalImageUri = selectedImageUri?.let { uri ->
                    if (uri.toString().startsWith("content://")) {
                        saveTurfImageToInternalStorage(context, uri)
                    } else {
                        uri
                    }
                }
                
                viewModel.saveTurf(
                    name = name,
                    location = location,
                    sports = sports,
                    price = price.toDoubleOrNull() ?: 0.0,
                    description = description,
                    imageUrl = finalImageUri?.toString() ?: "",
                    turfId = turfId
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .height(56.dp),
            enabled = isFormValid,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(if (turfId == 0L) "Add Turf" else "Save Changes", fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

fun saveTurfImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "turf_${System.currentTimeMillis()}.jpg")
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
