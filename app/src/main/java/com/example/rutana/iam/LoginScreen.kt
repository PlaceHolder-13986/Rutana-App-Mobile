package com.example.rutana.iam

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rutana.R
import com.example.rutana.data.DatabaseHelper
import com.example.rutana.ui.theme.RutanaNavyDark
import com.example.rutana.ui.theme.RutanaTheme

@Composable
fun LoginScreen(
    onLoginCorrecto: (nombre: String) -> Unit
) {
    val context = LocalContext.current
    val db = remember { DatabaseHelper(context) }

    // Estado de la pantalla (igual que en PantallaAlumnos)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ---------- Cabecera azul con el logo ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RutanaNavyDark)
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_rutana_logo),
                contentDescription = null,
                modifier = Modifier.size(width = 52.dp, height = 50.dp)
            )
            Text("Rutana", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(
                "The best platform for carriers and logistics in Peru",
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        // ---------- Formulario ----------
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Log In",
                style = MaterialTheme.typography.headlineMedium,
                color = RutanaNavyDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    error = ""
                },
                label = { Text("Email") },
                placeholder = { Text("ejemplo@empresa.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    error = ""
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { verPassword = !verPassword }) {
                        Icon(painterResource(R.drawable.ic_visibility), contentDescription = "Show password")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        error = "Enter your email and password"
                    } else {
                        val nombre = db.validarUsuario(email.trim(), password)
                        if (nombre != null) {
                            onLoginCorrecto(nombre)
                        } else {
                            error = "Incorrect email or password"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = RutanaNavyDark
                ),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Log in")
            }

            OutlinedButton(
                onClick = { error = "Coming soon" },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Create an account", color = RutanaNavyDark)
            }

            TextButton(
                onClick = { error = "Coming soon" },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Forgot your password?", color = RutanaNavyDark)
            }

            Text(
                "Demo user: admin@rutana.com / 123456",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    RutanaTheme {
        LoginScreen(onLoginCorrecto = {})
    }
}
