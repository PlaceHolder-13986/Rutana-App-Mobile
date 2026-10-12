package com.example.rutana.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rutana.R
import com.example.rutana.components.RutanaTopBar
import com.example.rutana.ui.theme.RutanaNavyDark
import com.example.rutana.ui.theme.RutanaTheme
import kotlinx.coroutines.launch

/** Una opción del menú lateral: nombre e ícono. */
data class OpcionMenu(val titulo: String, val icono: Int)

val opcionesMenu = listOf(
    OpcionMenu("Home", R.drawable.ic_home),
    OpcionMenu("Routes", R.drawable.ic_route),
    OpcionMenu("Vehicles and teams", R.drawable.ic_truck),
    OpcionMenu("Clients", R.drawable.ic_clients),
    OpcionMenu("User management", R.drawable.ic_users),
    OpcionMenu("Incidents", R.drawable.ic_incident)
)

@Composable
fun HomeScreen(
    nombreUsuario: String,
    onCerrarSesion: () -> Unit
) {
    // Qué opción del menú está elegida
    var opcionActual by remember { mutableStateOf(opcionesMenu[0]) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Cabecera del menú
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RutanaNavyDark)
                        .statusBarsPadding()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_rutana_logo),
                        contentDescription = null,
                        modifier = Modifier.size(width = 40.dp, height = 38.dp)
                    )
                    Text(nombreUsuario, color = Color.White, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(8.dp))

                opcionesMenu.forEach { opcion ->
                    NavigationDrawerItem(
                        label = { Text(opcion.titulo) },
                        icon = { Icon(painterResource(opcion.icono), contentDescription = null) },
                        selected = opcion == opcionActual,
                        onClick = {
                            opcionActual = opcion
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Log out") },
                    icon = { Icon(painterResource(R.drawable.ic_logout), contentDescription = null) },
                    selected = false,
                    onClick = onCerrarSesion,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                RutanaTopBar(
                    titulo = opcionActual.titulo,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onPerfilClick = { }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            if (opcionActual == opcionesMenu[0]) {
                // ---------- Inicio: bienvenida + accesos rápidos ----------
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Welcome, $nombreUsuario", style = MaterialTheme.typography.titleLarge)
                                Text("What do you want to manage today?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    items(opcionesMenu.drop(1)) { opcion ->
                        Card(
                            onClick = { opcionActual = opcion },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(112.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    painterResource(opcion.icono),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(opcion.titulo, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            } else {
                // ---------- Otras secciones: por ahora "Coming soon" ----------
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(opcionActual.icono),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Coming soon", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    RutanaTheme {
        HomeScreen(nombreUsuario = "Juan Pérez", onCerrarSesion = {})
    }
}
