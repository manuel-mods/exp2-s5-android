package com.example.accesibilidad.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.background
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.accesibilidad.R

@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("login")
                }) {
                    Text("Aceptar")
                }
            },
            title = { Text("Registro Exitoso") },
            text = { Text("El usuario ha sido registrado correctamente.") }
        )
    }
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
                modifier = Modifier.size(100.dp).padding(16.dp)

            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Registrate",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Text(
                "Ingresa tu correo y contraseña para registrarte.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(0.85f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.85f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Ingrese correo y contraseña."
                        return@Button
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        errorMessage = "El correo ingresado no es válido."
                        return@Button
                    }
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid ?: ""
                                val user = hashMapOf("email" to email, "userId" to userId)
                                db.collection("users").document(userId).set(user)
                                    .addOnSuccessListener {
                                        showDialog = true
                                    }
                                    .addOnFailureListener {
                                        errorMessage = "Error al guardar los datos del usuario."
                                    }
                            } else {
                                errorMessage = "Error al registrar usuario."
                            }
                        }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Text("Registrar", fontSize = 18.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Volver")
            }
        }
    }
}
