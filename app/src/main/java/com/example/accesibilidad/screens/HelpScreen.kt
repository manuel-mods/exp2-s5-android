package com.example.accesibilidad.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Módulo de Hablar",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "En la pantalla 'Hablar' puedes dictar comandos o mensajes. Al presionar el botón, se solicitará el permiso de micrófono si aún no lo has concedido. Una vez activado, la aplicación escuchará y transcribirá lo que digas, mostrando el resultado en pantalla. Asegúrate de tener una conexión estable para evitar errores de red.",
                style = MaterialTheme.typography.bodyLarge
            )
            Divider()
            Text(
                text = "Módulo de Escribir",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "En la pantalla 'Escribir' puedes ingresar texto manualmente. Al pulsar el botón 'Leer en voz alta', el sistema usará la función de Text-to-Speech para reproducir el contenido del campo de texto. Esto te permite escuchar el contenido que has escrito.",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Si tienes alguna duda adicional, consulta la documentación o contacta al soporte.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
