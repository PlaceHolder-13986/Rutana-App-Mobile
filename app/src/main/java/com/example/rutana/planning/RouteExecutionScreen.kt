package com.example.rutana.planning

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.rutana.R
import com.example.rutana.data.planning.LocationDeliveryStatus
import com.example.rutana.data.planning.PlanningDatabaseHelper
import com.example.rutana.data.planning.Route
import com.example.rutana.data.planning.RouteLocationItem
import com.example.rutana.data.planning.RouteStatus
import com.example.rutana.ui.theme.RutanaNavy
import com.example.rutana.ui.theme.RutanaNavyDark

@Composable
fun RouteExecutionScreen(
    routeId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { PlanningDatabaseHelper(context) }

    var currentRoute by remember { mutableStateOf<Route?>(null) }
    var locationsList by remember { mutableStateOf<List<RouteLocationItem>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf<LocationDeliveryStatus?>(null) }

    var locationToReject by remember { mutableStateOf<RouteLocationItem?>(null) }
    var showCloseConfirmModal by remember { mutableStateOf(false) }

    fun refreshState() {
        currentRoute = db.getRouteById(routeId)
        locationsList = db.getRouteLocations(routeId)
    }

    LaunchedEffect(routeId) {
        refreshState()
    }

    val route = currentRoute ?: return

    val completedCount = locationsList.count { it.status == LocationDeliveryStatus.COMPLETED }
    val totalCount = locationsList.size
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val progressPercent = (progressFraction * 100).toInt()

    val filteredLocations = locationsList.filter {
        selectedFilter == null || it.status == selectedFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RutanaNavy)
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Monitoreo - ${route.code}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${route.name} | Vehículo: ${route.vehiclePlate ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Real-time Progress Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progreso de Entregas",
                                fontWeight = FontWeight.Bold,
                                color = RutanaNavyDark
                            )
                            Text(
                                text = "$completedCount/$totalCount completadas ($progressPercent%)",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFFE5E7EB)
                        )
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("Todos (${locationsList.size})") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == LocationDeliveryStatus.PENDING,
                            onClick = { selectedFilter = LocationDeliveryStatus.PENDING },
                            label = { Text("Pendientes (${locationsList.count { it.status == LocationDeliveryStatus.PENDING }})") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == LocationDeliveryStatus.COMPLETED,
                            onClick = { selectedFilter = LocationDeliveryStatus.COMPLETED },
                            label = { Text("Completados (${locationsList.count { it.status == LocationDeliveryStatus.COMPLETED }})") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == LocationDeliveryStatus.REJECTED,
                            onClick = { selectedFilter = LocationDeliveryStatus.REJECTED },
                            label = { Text("Rechazados (${locationsList.count { it.status == LocationDeliveryStatus.REJECTED }})") }
                        )
                    }
                }
            }

            // Deliveries List
            items(filteredLocations) { item ->
                DeliveryLocationCard(
                    item = item,
                    onComplete = {
                        db.updateDeliveryStatus(item.id, LocationDeliveryStatus.COMPLETED)
                        refreshState()
                        Toast.makeText(context, "Entrega completada con éxito", Toast.LENGTH_SHORT).show()
                    },
                    onRejectClick = {
                        locationToReject = item
                    }
                )
            }

            // Close Route Action
            if (route.status != RouteStatus.COMPLETED && route.status != RouteStatus.CLOSED_WITH_PENDINGS) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val hasPendings = locationsList.any { it.status == LocationDeliveryStatus.PENDING || it.status == LocationDeliveryStatus.IN_TRANSIT }
                            if (hasPendings) {
                                showCloseConfirmModal = true
                            } else {
                                db.closeRoute(routeId, forceWithPendings = false)
                                refreshState()
                                Toast.makeText(context, "Ruta completada y cerrada exitosamente", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Cerrar Ruta", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal for Rejection Reason (US28)
    if (locationToReject != null) {
        RejectDeliveryModal(
            location = locationToReject!!,
            onDismissRequest = { locationToReject = null },
            onConfirmReject = { reason ->
                db.updateDeliveryStatus(locationToReject!!.id, LocationDeliveryStatus.REJECTED, reason)
                locationToReject = null
                refreshState()
                Toast.makeText(context, "Entrega rechazada registrada", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Modal Confirmation for Close with Pendings
    if (showCloseConfirmModal) {
        Dialog(onDismissRequest = { showCloseConfirmModal = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Cierre con Entregas Pendientes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = RutanaNavyDark)
                    Text("Existen entregas aún sin completar. ¿Deseas cerrar la ruta marcándolas como pendientes para reprogramación?")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCloseConfirmModal = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = RutanaNavyDark)
                        }

                        Button(
                            onClick = {
                                showCloseConfirmModal = false
                                db.closeRoute(routeId, forceWithPendings = true)
                                refreshState()
                                Toast.makeText(context, "Ruta cerrada con entregas pendientes", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cerrar Ruta", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryLocationCard(
    item: RouteLocationItem,
    onComplete: () -> Unit,
    onRejectClick: () -> Unit
) {
    val statusColor = when (item.status) {
        LocationDeliveryStatus.PENDING -> Color(0xFFF59E0B)
        LocationDeliveryStatus.IN_TRANSIT -> Color(0xFF3B82F6)
        LocationDeliveryStatus.COMPLETED -> Color(0xFF10B981)
        LocationDeliveryStatus.REJECTED -> Color(0xFFEF4444)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.locationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = RutanaNavyDark
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.status.displayName,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Text(item.clientName, fontWeight = FontWeight.Medium)
            Text(item.address, fontSize = 12.sp, color = Color.Gray)

            if (item.status == LocationDeliveryStatus.COMPLETED && item.confirmTime != null) {
                Text("Confirmado a las: ${item.confirmTime}", fontSize = 12.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
            }

            if (item.status == LocationDeliveryStatus.REJECTED && item.rejectionReason != null) {
                Text("Motivo de rechazo: ${item.rejectionReason}", fontSize = 12.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
            }

            if (item.status == LocationDeliveryStatus.PENDING || item.status == LocationDeliveryStatus.IN_TRANSIT) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRejectClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("Reject", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("Complete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RejectDeliveryModal(
    location: RouteLocationItem,
    onDismissRequest: () -> Unit,
    onConfirmReject: (reason: String) -> Unit
) {
    val options = listOf(
        "Destinatario ausente",
        "Dirección incorrecta",
        "Cliente rechazó entrega",
        "Mercadería dañada",
        "Otro motivo"
    )
    var selectedReason by remember { mutableStateOf(options[0]) }
    var extraDetail by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Rechazar Entrega", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = RutanaNavyDark)
                Text(location.locationName, fontWeight = FontWeight.Medium, color = Color.Gray)

                Column {
                    options.forEach { opt ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = selectedReason == opt,
                                onClick = { selectedReason = opt }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(opt, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                if (selectedReason == "Otro motivo") {
                    OutlinedTextField(
                        value = extraDetail,
                        onValueChange = { extraDetail = it },
                        placeholder = { Text("Escriba el detalle del motivo...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar", color = RutanaNavyDark)
                    }

                    Button(
                        onClick = {
                            val finalReason = if (selectedReason == "Otro motivo" && extraDetail.isNotBlank()) {
                                "Otro: $extraDetail"
                            } else {
                                selectedReason
                            }
                            onConfirmReject(finalReason)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirmar Rechazo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
