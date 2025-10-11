package com.example.proyectouni.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyectouni.R

@Composable
fun CreatePlaceScreen(
    onNavigate: (String) -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    var placeName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    // Horarios
    var weekdaysStart by remember { mutableStateOf("") }
    var weekdaysEnd by remember { mutableStateOf("") }
    var saturdayStart by remember { mutableStateOf("") }
    var saturdayEnd by remember { mutableStateOf("") }
    var sundayStart by remember { mutableStateOf("") }
    var sundayEnd by remember { mutableStateOf("") }

    val totalSteps = 3

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val remainingSlots = 6 - selectedImages.size
        if (remainingSlots > 0) {
            selectedImages = selectedImages + uris.take(remainingSlots)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .widthIn(max = 400.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (currentStep > 1) {
                                currentStep--
                            } else {
                                onNavigate("main")
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.add_place),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.step_progress, currentStep, totalSteps),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Progress indicator
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(totalSteps) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (index < currentStep)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }
            }

            // Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (currentStep) {
                            1 -> Step1Content(
                                placeName = placeName,
                                onPlaceNameChange = { placeName = it },
                                category = category,
                                onCategoryChange = { category = it },
                                description = description,
                                onDescriptionChange = { description = it },
                                selectedImages = selectedImages,
                                onRemoveImage = { index ->
                                    selectedImages = selectedImages.filterIndexed { i, _ -> i != index }
                                },
                                onAddImage = { imagePickerLauncher.launch("image/*") },
                                showCategoryDropdown = showCategoryDropdown,
                                onShowCategoryDropdownChange = { showCategoryDropdown = it }
                            )
                            2 -> Step2Content(
                                address = address,
                                onAddressChange = { address = it },
                                phone = phone,
                                onPhoneChange = { phone = it },
                                website = website,
                                onWebsiteChange = { website = it },
                                weekdaysStart = weekdaysStart,
                                onWeekdaysStartChange = { weekdaysStart = it },
                                weekdaysEnd = weekdaysEnd,
                                onWeekdaysEndChange = { weekdaysEnd = it },
                                saturdayStart = saturdayStart,
                                onSaturdayStartChange = { saturdayStart = it },
                                saturdayEnd = saturdayEnd,
                                onSaturdayEndChange = { saturdayEnd = it },
                                sundayStart = sundayStart,
                                onSundayStartChange = { sundayStart = it },
                                sundayEnd = sundayEnd,
                                onSundayEndChange = { sundayEnd = it }
                            )
                            3 -> Step3Content(
                                placeName = placeName,
                                category = category,
                                description = description,
                                address = address,
                                phone = phone,
                                selectedImages = selectedImages
                            )
                        }
                    }
                }
            }

            // Navigation Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(stringResource(R.string.previous))
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep < totalSteps) {
                                currentStep++
                            } else {
                                // Enviar para revisión
                                onNavigate("main")
                            }
                        },
                        modifier = Modifier
                            .weight(if (currentStep > 1) 1f else 1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            if (currentStep == totalSteps)
                                stringResource(R.string.submit_review)
                            else
                                stringResource(R.string.continue_button)
                        )
                    }
                }
            }

            // Footer
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.need_help),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = { onNavigate("main") }) {
                            Text(stringResource(R.string.contact_support))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step1Content(
    placeName: String,
    onPlaceNameChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedImages: List<Uri>,
    onRemoveImage: (Int) -> Unit,
    onAddImage: () -> Unit,
    showCategoryDropdown: Boolean,
    onShowCategoryDropdownChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = stringResource(R.string.basic_info),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.basic_info_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Place name
        OutlinedTextField(
            value = placeName,
            onValueChange = onPlaceNameChange,
            label = { Text(stringResource(R.string.place_name)) },
            placeholder = { Text(stringResource(R.string.place_name_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        // Category
        ExposedDropdownMenuBox(
            expanded = showCategoryDropdown,
            onExpandedChange = onShowCategoryDropdownChange
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.category)) },
                placeholder = { Text(stringResource(R.string.select_category)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(16.dp)
            )

            ExposedDropdownMenu(
                expanded = showCategoryDropdown,
                onDismissRequest = { onShowCategoryDropdownChange(false) }
            ) {
                listOf(
                    "Restaurante", "Cafetería", "Hotel", "Museo",
                    "Tienda", "Panadería", "Bar", "Heladería",
                    "Librería", "Otro"
                ).forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            onCategoryChange(cat)
                            onShowCategoryDropdownChange(false)
                        }
                    )
                }
            }
        }

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.description)) },
            placeholder = { Text(stringResource(R.string.description_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5,
            shape = RoundedCornerShape(16.dp)
        )
        Text(
            text = stringResource(R.string.min_characters),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Photos
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.place_photos),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedImages.take(3).forEachIndexed { index, uri ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    ) {

                        IconButton(
                            onClick = { onRemoveImage(index) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(24.dp)
                                .background(
                                    color = Color(0xFFEF4444),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.remove),
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                if (selectedImages.size < 6) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onAddImage() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.add),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Text(
                text = stringResource(R.string.max_photos),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun Step2Content(
    address: String,
    onAddressChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    website: String,
    onWebsiteChange: (String) -> Unit,
    weekdaysStart: String,
    onWeekdaysStartChange: (String) -> Unit,
    weekdaysEnd: String,
    onWeekdaysEndChange: (String) -> Unit,
    saturdayStart: String,
    onSaturdayStartChange: (String) -> Unit,
    saturdayEnd: String,
    onSaturdayEndChange: (String) -> Unit,
    sundayStart: String,
    onSundayStartChange: (String) -> Unit,
    sundayEnd: String,
    onSundayEndChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = stringResource(R.string.location_contact),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.location_contact_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Mini map placeholder
        Text(
            text = stringResource(R.string.map_location),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.tap_select_location),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Address
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text(stringResource(R.string.address)) },
            placeholder = { Text(stringResource(R.string.address_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
            },
            shape = RoundedCornerShape(16.dp)
        )

        // Phone and website
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text(stringResource(R.string.phone)) },
                placeholder = { Text(stringResource(R.string.phone_placeholder)) },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = website,
                onValueChange = onWebsiteChange,
                label = { Text(stringResource(R.string.web)) },
                placeholder = { Text(stringResource(R.string.website_placeholder)) },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Language, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Schedule
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.business_hours),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        ScheduleRow(
            label = stringResource(R.string.weekdays),
            startTime = weekdaysStart,
            onStartTimeChange = onWeekdaysStartChange,
            endTime = weekdaysEnd,
            onEndTimeChange = onWeekdaysEndChange
        )

        ScheduleRow(
            label = stringResource(R.string.saturday),
            startTime = saturdayStart,
            onStartTimeChange = onSaturdayStartChange,
            endTime = saturdayEnd,
            onEndTimeChange = onSaturdayEndChange
        )

        ScheduleRow(
            label = stringResource(R.string.sunday),
            startTime = sundayStart,
            onStartTimeChange = onSundayStartChange,
            endTime = sundayEnd,
            onEndTimeChange = onSundayEndChange
        )
    }
}

@Composable
private fun ScheduleRow(
    label: String,
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(100.dp)
        )

        OutlinedTextField(
            value = startTime,
            onValueChange = onStartTimeChange,
            placeholder = { Text("09:00") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = endTime,
            onValueChange = onEndTimeChange,
            placeholder = { Text("18:00") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun Step3Content(
    placeName: String,
    category: String,
    description: String,
    address: String,
    phone: String,
    selectedImages: List<Uri>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = stringResource(R.string.final_review),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.final_review_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Preview card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box {
                    if (selectedImages.isNotEmpty()) {

                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = 56.dp, y = (-4).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = placeName.ifEmpty { stringResource(R.string.place_name) },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                category.ifEmpty { stringResource(R.string.category) },
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = description.ifEmpty { stringResource(R.string.description_placeholder) },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Info summary
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoRow(
                icon = Icons.Default.LocationOn,
                title = stringResource(R.string.location),
                value = address.ifEmpty { stringResource(R.string.address_pending) }
            )

            if (phone.isNotEmpty()) {
                InfoRow(
                    icon = Icons.Default.Phone,
                    title = stringResource(R.string.phone),
                    value = phone
                )
            }

            InfoRow(
                icon = Icons.Default.CameraAlt,
                title = stringResource(R.string.photos),
                value = stringResource(R.string.images_selected, selectedImages.size)
            )
        }

        // Important info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFEF3C7)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.important_info),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        text = stringResource(R.string.important_info_message),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}