package com.example.proyectouni.ui.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectouni.R

enum class PlaceStatus {
    PENDING, APPROVED, REJECTED
}

data class PendingPlace(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val address: String,
    val phone: String,
    val hours: String,
    val submittedBy: String,
    val submittedDate: String,
    var status: PlaceStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorScreen(
    onNavigate: (String) -> Unit
) {
    var filter by remember { mutableStateOf(PlaceStatus.PENDING) }
    var showAllFilter by remember { mutableStateOf(false) }

    // Mock data - En una app real, esto vendría de un ViewModel/Repository
    var places by remember {
        mutableStateOf(
            listOf(
                PendingPlace(
                    id = "1",
                    name = "Café Regional del Norte",
                    category = "Cafetería",
                    description = "Café artesanal con granos locales, ambiente acogedor y música en vivo los fines de semana.",
                    address = "Calle 15 #23-45, Zona Rosa",
                    phone = "+57 301 234 5678",
                    hours = "Lun-Dom: 7:00 AM - 10:00 PM",
                    submittedBy = "María González",
                    submittedDate = "2024-01-15",
                    status = PlaceStatus.PENDING
                ),
                PendingPlace(
                    id = "2",
                    name = "Restaurante La Abuela",
                    category = "Restaurante",
                    description = "Comida tradicional casera con recetas familiares de más de 50 años.",
                    address = "Carrera 8 #12-30, Centro",
                    phone = "+57 312 567 8901",
                    hours = "Mar-Dom: 12:00 PM - 9:00 PM",
                    submittedBy = "Carlos Rodríguez",
                    submittedDate = "2024-01-14",
                    status = PlaceStatus.PENDING
                ),
                PendingPlace(
                    id = "3",
                    name = "Museo de Arte Local",
                    category = "Museo",
                    description = "Exposiciones permanentes y temporales de artistas regionales.",
                    address = "Avenida Principal #45-67",
                    phone = "+57 320 890 1234",
                    hours = "Mié-Dom: 10:00 AM - 6:00 PM",
                    submittedBy = "Ana Martín",
                    submittedDate = "2024-01-13",
                    status = PlaceStatus.APPROVED
                ),
                PendingPlace(
                    id = "4",
                    name = "Bar Nocturno XYZ",
                    category = "Bar",
                    description = "Lugar con ambiente inadecuado y posibles problemas de seguridad.",
                    address = "Calle Dudosa #123",
                    phone = "+57 300 000 0000",
                    hours = "Vie-Sáb: 10:00 PM - 4:00 AM",
                    submittedBy = "Usuario Anónimo",
                    submittedDate = "2024-01-12",
                    status = PlaceStatus.REJECTED
                )
            )
        )
    }

    val filteredPlaces = if (showAllFilter) {
        places
    } else {
        places.filter { it.status == filter }
    }

    val stats = remember(places) {
        mapOf(
            PlaceStatus.PENDING to places.count { it.status == PlaceStatus.PENDING },
            PlaceStatus.APPROVED to places.count { it.status == PlaceStatus.APPROVED },
            PlaceStatus.REJECTED to places.count { it.status == PlaceStatus.REJECTED }
        )
    }

    Scaffold(
        topBar = {
            ModeratorTopBar(
                totalPlaces = filteredPlaces.size,
                onBackClick = { onNavigate("main") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Cards
            item {
                StatsCards(stats)
            }

            // Filter Section
            item {
                FilterSection(
                    currentFilter = filter,
                    showAll = showAllFilter,
                    onFilterChange = { newFilter ->
                        filter = newFilter
                        showAllFilter = false
                    },
                    onShowAllClick = { showAllFilter = true }
                )
            }

            // Places List
            if (filteredPlaces.isEmpty()) {
                item {
                    EmptyState(filter, showAllFilter)
                }
            } else {
                items(filteredPlaces) { place ->
                    PlaceReviewCard(
                        place = place,
                        onApprove = {
                            places = places.map {
                                if (it.id == place.id) it.copy(status = PlaceStatus.APPROVED)
                                else it
                            }
                        },
                        onReject = {
                            places = places.map {
                                if (it.id == place.id) it.copy(status = PlaceStatus.REJECTED)
                                else it
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModeratorTopBar(
    totalPlaces: Int,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(R.string.moderator_panel),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.place_management),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = "$totalPlaces ${stringResource(R.string.places)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}

@Composable
private fun StatsCards(stats: Map<PlaceStatus, Int>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            count = stats[PlaceStatus.PENDING] ?: 0,
            label = stringResource(R.string.pending_plural),
            containerColor = Color(0xFFFEF3C7),
            contentColor = Color(0xFFB45309),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            count = stats[PlaceStatus.APPROVED] ?: 0,
            label = stringResource(R.string.approved_plural),
            containerColor = Color(0xFFD1FAE5),
            contentColor = Color(0xFF065F46),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            count = stats[PlaceStatus.REJECTED] ?: 0,
            label = stringResource(R.string.rejected_plural),
            containerColor = Color(0xFFFEE2E2),
            contentColor = Color(0xFF991B1B),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    count: Int,
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
    }
}

@Composable
private fun FilterSection(
    currentFilter: PlaceStatus,
    showAll: Boolean,
    onFilterChange: (PlaceStatus) -> Unit,
    onShowAllClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.filter_by_status),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !showAll && currentFilter == PlaceStatus.PENDING,
                    onClick = { onFilterChange(PlaceStatus.PENDING) },
                    label = { Text(stringResource(R.string.pending_plural)) },
                    leadingIcon = if (!showAll && currentFilter == PlaceStatus.PENDING) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
                FilterChip(
                    selected = !showAll && currentFilter == PlaceStatus.APPROVED,
                    onClick = { onFilterChange(PlaceStatus.APPROVED) },
                    label = { Text(stringResource(R.string.approved_plural)) },
                    leadingIcon = if (!showAll && currentFilter == PlaceStatus.APPROVED) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
                FilterChip(
                    selected = !showAll && currentFilter == PlaceStatus.REJECTED,
                    onClick = { onFilterChange(PlaceStatus.REJECTED) },
                    label = { Text(stringResource(R.string.rejected_plural)) },
                    leadingIcon = if (!showAll && currentFilter == PlaceStatus.REJECTED) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
                FilterChip(
                    selected = showAll,
                    onClick = onShowAllClick,
                    label = { Text(stringResource(R.string.all)) },
                    leadingIcon = if (showAll) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun PlaceReviewCard(
    place: PendingPlace,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(getCategoryColor(place.category)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = place.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(status = place.status)
            }

            // Description
            Text(
                text = place.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Details
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow(
                    icon = Icons.Default.LocationOn,
                    text = place.address
                )
                DetailRow(
                    icon = Icons.Default.Phone,
                    text = place.phone
                )
                DetailRow(
                    icon = Icons.Default.Schedule,
                    text = place.hours
                )
            }

            HorizontalDivider()

            // Submitted by
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = place.submittedBy.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = place.submittedBy,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = place.submittedDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Action buttons (only for pending)
                if (place.status == PlaceStatus.PENDING) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onReject,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.reject))
                        }

                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.approve))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: PlaceStatus) {
    val (icon, label, containerColor, contentColor) = when (status) {
        PlaceStatus.PENDING -> Quad(
            Icons.Default.Schedule,
            stringResource(R.string.status_pending),
            Color(0xFFFEF3C7),
            Color(0xFFB45309)
        )
        PlaceStatus.APPROVED -> Quad(
            Icons.Default.CheckCircle,
            stringResource(R.string.status_approved),
            Color(0xFFD1FAE5),
            Color(0xFF065F46)
        )
        PlaceStatus.REJECTED -> Quad(
            Icons.Default.Cancel,
            stringResource(R.string.status_rejected),
            Color(0xFFFEE2E2),
            Color(0xFF991B1B)
        )
    }

    AssistChip(
        onClick = { },
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor,
            leadingIconContentColor = contentColor
        )
    )
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyState(filter: PlaceStatus, showAll: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = stringResource(
                    if (showAll) R.string.no_places_all
                    else when (filter) {
                        PlaceStatus.PENDING -> R.string.no_places_pending
                        PlaceStatus.APPROVED -> R.string.no_places_approved
                        PlaceStatus.REJECTED -> R.string.no_places_rejected
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Restaurante" -> Color(0xFF10B981)
        "Cafetería" -> Color(0xFF3B82F6)
        "Museo" -> Color(0xFF8B5CF6)
        "Hotel" -> Color(0xFFEC4899)
        "Bar" -> Color(0xFFF97316)
        else -> Color(0xFF6B7280)
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)