package com.example.rutana.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.rutana.R
import com.example.rutana.ui.theme.RutanaNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutanaTopBar(
    titulo: String,
    onMenuClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(painterResource(R.drawable.ic_menu), contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = onPerfilClick) {
                Icon(painterResource(R.drawable.ic_person), contentDescription = "Profile")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = RutanaNavy,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}
