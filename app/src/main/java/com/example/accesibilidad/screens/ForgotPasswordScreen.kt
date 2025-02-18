package com.example.accesibilidad.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.paint
import androidx.navigation.NavController
import com.example.accesibilidad.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.background),
                contentScale = ContentScale.Crop
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp)
            )
            Text(
                text = "Recuperar Contraseña",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ingresa tu correo electrónico para recibir un enlace de restablecimiento de contraseña.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(0.85f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    errorMessage = ""
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        errorMessage = "El correo ingresado no es válido."
                        return@Button
                    }
                    isLoading = true
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                showSuccessDialog = true
                            } else {
                                errorMessage = task.exception?.message ?: "Error al enviar el correo de recuperación."
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(0.85f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(
                    text = if (isLoading) "Enviando..." else "Enviar",
                    color = Color.White,
                    fontSize = 18.sp
                )
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

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Correo Enviado") },
            text = { Text("Se ha enviado un enlace para restablecer tu contraseña a $email. Revisa tu bandeja de entrada.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    navController.navigate("login")
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
