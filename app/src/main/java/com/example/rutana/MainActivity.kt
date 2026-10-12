package com.example.rutana

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.rutana.home.HomeScreen
import com.example.rutana.iam.LoginScreen
import com.example.rutana.ui.theme.RutanaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            RutanaTheme {
                var pantalla by rememberSaveable { mutableStateOf("login") }
                var nombreUsuario by rememberSaveable { mutableStateOf("") }

                when (pantalla) {
                    "login" -> LoginScreen(
                        onLoginCorrecto = { nombre ->
                            nombreUsuario = nombre
                            pantalla = "home"
                        }
                    )
                    "home" -> HomeScreen(
                        nombreUsuario = nombreUsuario,
                        onCerrarSesion = { pantalla = "login" }
                    )
                }
            }
        }
    }
}
