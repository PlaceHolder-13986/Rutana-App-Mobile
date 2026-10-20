package com.example.rutana.planning

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.rutana.data.planning.AvailableLocation
import com.example.rutana.data.planning.AvailableTeam
import com.example.rutana.data.planning.PlanningDatabaseHelper
import com.example.rutana.data.planning.Route
import com.example.rutana.data.planning.RouteLocationItem
import com.example.rutana.ui.theme.RutanaNavy
import com.example.rutana.ui.theme.RutanaNavyDark

@Composable
fun RoutePlanningScreen(
    routeId: Long,
    onBack: () -> Unit,
    onPublishedSuccess: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { PlanningDatabaseHelper(context) }

    var currentRoute by remember { mutableStateOf<Route?>(null) }
    var routeLocations by remember { mutableStateOf<List<RouteLocationItem>>(emptyList()) }
    var availableLocations by remember { mutableStateOf<List<AvailableLocation>>(emptyList()) }
    var availableTeams by remember { mutableStateOf<List<AvailableTeam>>(emptyList()) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    fun refreshState() {
        currentRoute = db.getRouteById(routeId)
        routeLocations = db.getRouteLocations(routeId)
        availableLocations = db.getAvailableLocations()
        availableTeams = db.getAvailableTeams()
    }

    LaunchedEffect(routeId) {
        refreshState()
    }

    val route = currentRoute ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Custom Top Header
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
                        text = "Route planning - ${route.code}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${route.name} (${route.date})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Publish CTA Button
            Button(
                onClick = {
                    val result = db.publishRoute(routeId)
                    if (result.first) {
                        Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                        onPublishedSuccess()
                    } else {
                        errorMessage = result.second
                        Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = RutanaNavyDark
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("PUBLISH", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Tabs: Locations / Team
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = RutanaNavyDark
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Locations (${routeLocations.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Team (${if (route.vehiclePlate != null) "1" else "0"})", fontWeight = FontWeight.Bold) }
            )
        }

        if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEE2E2))
                    .padding(12.dp)
            ) {
                Text(errorMessage, color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
            }
        }

        when (selectedTabIndex) {
            0 -> TabLocationsContent(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                routeLocations = routeLocations,
                availableLocations = availableLocations.filter {
                    searchQuery.isEmpty() ||
                            it.clientName.contains(searchQuery, ignoreCase = true) ||
                            it.locationName.contains(searchQuery, ignoreCase = true)
                },
                onAddLocation = { loc ->
                    db.addLocationToRoute(routeId, loc)
                    refreshState()
                },
                onRemoveLocation = { locId ->
                    db.removeLocationFromRoute(routeId, locId)
                    refreshState()
                }
            )
            1 -> TabTeamContent(
                assignedTeamId = route.vehicleTeamId,
                availableTeams = availableTeams,
                onSelectTeam = { team ->
                    db.assignTeamToRoute(routeId, team)
                    refreshState()
                }
            )
        }
    }
}

@Composable
fun TabLocationsContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    routeLocations: List<RouteLocationItem>,
    availableLocations: List<AvailableLocation>,
    onAddLocation: (AvailableLocation) -> Unit,
    onRemoveLocation: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search client or warehouse...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Interactive Map Placeholder Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RutanaNavyDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
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
                            painter = painterResource(R.drawable.ic_clients),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Interactive Map",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${routeLocations.size} locations pinned on route",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Selected Locations Summary Block
        item {
            Text(
                text = "Selected locations (${routeLocations.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RutanaNavyDark
            )
        }

        if (routeLocations.isEmpty()) {
            item {
                Text(
                    text = "No locations added yet. Select from available list below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(routeLocations) { loc ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(loc.locationName, fontWeight = FontWeight.Bold, color = RutanaNavyDark)
                            Text(loc.clientName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(loc.address, fontSize = 11.sp, color = Color.Gray)
                        }

                        IconButton(onClick = { onRemoveLocation(loc.locationId) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_logout),
                                contentDescription = "Quitar",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
        }

        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

        // Available Locations Section
        item {
            Text(
                text = "Available locations in CRM",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RutanaNavyDark
            )
        }

        items(availableLocations) { avail ->
            val isAlreadyAdded = routeLocations.any { it.locationId == avail.id }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(avail.locationName, fontWeight = FontWeight.Bold)
                        Text(avail.clientName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(avail.address, fontSize = 11.sp, color = Color.Gray)
                    }

                    if (isAlreadyAdded) {
                        OutlinedButton(
                            onClick = { onRemoveLocation(avail.id) },
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Remove", fontSize = 12.sp, color = Color(0xFFEF4444))
                        }
                    } else {
                        Button(
                            onClick = { onAddLocation(avail) },
                            colors = ButtonDefaults.buttonColors(containerColor = RutanaNavyDark),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("+ Select", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabTeamContent(
    assignedTeamId: Long?,
    availableTeams: List<AvailableTeam>,
    onSelectTeam: (AvailableTeam) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Available Fleet Teams",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RutanaNavyDark
            )
        }

        items(availableTeams) { team ->
            val isAssigned = assignedTeamId == team.id

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isAssigned) Modifier.border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp)) else Modifier
                    )
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_truck),
                                contentDescription = null,
                                tint = RutanaNavyDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = team.vehiclePlate,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = RutanaNavyDark
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Cap: ${team.capacityKg.toInt()} kg",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0369A1)
                            )
                        }
                    }

                    Text("Driver: ${team.driverName}", fontWeight = FontWeight.Medium)
                    Text("Members: ${team.members}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(4.dp))

                    if (isAssigned) {
                        Button(
                            onClick = {},
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF10B981), disabledContentColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("✓ Assigned Team", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { onSelectTeam(team) },
                            colors = ButtonDefaults.buttonColors(containerColor = RutanaNavyDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Select Team", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
