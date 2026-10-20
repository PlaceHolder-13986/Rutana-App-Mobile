package com.example.rutana.planning

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rutana.R
import com.example.rutana.data.planning.PlanningDatabaseHelper
import com.example.rutana.data.planning.Route
import com.example.rutana.data.planning.RouteStatus
import com.example.rutana.ui.theme.RutanaNavyDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RoutesListScreen(
    onOpenPlanning: (routeId: Long) -> Unit,
    onOpenExecution: (routeId: Long) -> Unit
) {
    val context = LocalContext.current
    val db = remember { PlanningDatabaseHelper(context) }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedDateFilter by remember { mutableStateOf(todayStr) }

    var routesList by remember { mutableStateOf<List<Route>>(emptyList()) }
    var showNewRouteModal by remember { mutableStateOf(false) }

    fun refreshData() {
        routesList = db.getAllRoutes(selectedDateFilter)
    }

    LaunchedEffect(selectedDateFilter) {
        refreshData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subtitle
        Text(
            text = "Plan, review and publish daily routes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Date Filter Field
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Planned date",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedDateFilter,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = RutanaNavyDark
                    )
                }

                OutlinedButton(
                    onClick = {
                        // Toggle between Today and All Dates for demo
                        selectedDateFilter = if (selectedDateFilter.isEmpty()) todayStr else ""
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (selectedDateFilter.isEmpty()) "Ver fecha hoy" else "Ver todas",
                        fontSize = 12.sp,
                        color = RutanaNavyDark
                    )
                }
            }
        }

        // Action Button: + New route
        Button(
            onClick = { showNewRouteModal = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = RutanaNavyDark,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_route),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("+ New route", fontWeight = FontWeight.Bold)
        }

        // Header for Routes Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Routes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = RutanaNavyDark
            )
            Text(
                text = "Total routes: ${routesList.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Routes List
        if (routesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.ic_route),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("No routes found for selected date", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(routesList) { route ->
                    RouteCardItem(
                        route = route,
                        onClick = {
                            if (route.status == RouteStatus.DRAFT) {
                                onOpenPlanning(route.id)
                            } else {
                                onOpenExecution(route.id)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showNewRouteModal) {
        NewRouteModal(
            onDismissRequest = { showNewRouteModal = false },
            onConfirm = { name, type, colorTag ->
                showNewRouteModal = false
                val createdId = db.createDraftRoute(
                    name = name,
                    type = type,
                    colorTag = colorTag,
                    date = if (selectedDateFilter.isNotEmpty()) selectedDateFilter else todayStr
                )
                if (createdId > 0) {
                    onOpenPlanning(createdId)
                } else {
                    refreshData()
                }
            }
        )
    }
}

@Composable
fun RouteCardItem(
    route: Route,
    onClick: () -> Unit
) {
    val statusColor = when (route.status) {
        RouteStatus.DRAFT -> Color(0xFFF59E0B) // Amber/Yellow
        RouteStatus.PUBLISHED -> Color(0xFF0EA5E9) // Sky Blue/Cyan
        RouteStatus.IN_PROGRESS -> Color(0xFF6366F1) // Indigo Blue
        RouteStatus.COMPLETED -> Color(0xFF10B981) // Emerald Green
        RouteStatus.CLOSED_WITH_PENDINGS -> Color(0xFFEF4444) // Coral Red
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Code + Name + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Color tag indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(parseHexColor(route.colorTag))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = route.code,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = RutanaNavyDark
                    )
                }

                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = route.status.displayName,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = route.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Vehicle Plate & Locations Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_truck),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = route.vehiclePlate ?: "Sin vehículo asignado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clients),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Locations: ${route.locationCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = RutanaNavyDark
                    )
                }
            }

            // Progress bar if route is published/in progress
            if (route.status != RouteStatus.DRAFT && route.locationCount > 0) {
                val progressFraction = route.completedCount.toFloat() / route.locationCount.toFloat()
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progreso de entregas",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${route.completedCount}/${route.locationCount} (${(progressFraction * 100).toInt()}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = statusColor,
                        trackColor = statusColor.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}
