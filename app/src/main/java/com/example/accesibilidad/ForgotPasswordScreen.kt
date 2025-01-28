package com.example.accesibilidad

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var recoveredPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Por favor ingresa tu correo para recuperar tu contraseña.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(0.8f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = "El correo ingresado no es valido."
                    return@Button
                }

                val user = UserData.users.find { it.username == email }
                if (user != null) {
                    recoveredPassword = user.password
                    showPasswordDialog = true
                } else {
                    recoveredPassword = "Usuario no encontrado"
                    showPasswordDialog = true
                }
            }, modifier = Modifier.fillMaxWidth(0.8f)) {
                Text("Recuperar Contraseña")
            }
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Volver al Inicio")
            }
        }
    }

    if (showPasswordDialog) {
        Dialog(onDismissRequest = { showPasswordDialog = false }) {
            Surface(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (recoveredPassword == "Usuario no encontrado") "Usuario no encontrado" else "Tu contraseña es: $recoveredPassword",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showPasswordDialog = false }) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
