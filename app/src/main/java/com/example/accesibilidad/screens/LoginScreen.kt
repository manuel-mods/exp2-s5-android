package com.example.accesibilidad.screens

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.accesibilidad.R
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
//    if (auth.currentUser != null) {
//        navController.navigate("home")
//        return;
//    }
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
                text = "APP Accesibilidad",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
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
                        errorMessage = "Por favor, ingresa correo y contraseña."
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("home")
                                } else {
                                    errorMessage = "Usuario o contraseña inválidos."
                                }
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Text("Entrar", fontSize = 18.sp, color = Color.White)
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
            TextButton(onClick = { navController.navigate("register") }) {
                Text("Registrar")
            }
            TextButton(onClick = { navController.navigate("forgot_password") }) {
                Text("Recuperar contraseña")
            }
        }
    }
}
