package com.example.accesibilidad.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.accesibilidad.helpers.FirestoreHelper
import com.google.firebase.auth.FirebaseAuth
import java.util.*

data class SavedText(
    val id: String,
    val text: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadScreen(navController: NavController) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var ttsInitialized by remember { mutableStateOf(false) }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    // Obtén el userId del usuario actual
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: "unknown"

    // Historial de textos guardados (ahora como objetos SavedText)
    var history by remember { mutableStateOf(listOf<SavedText>()) }
    // Texto seleccionado para eliminar (null si no hay ninguno seleccionado)
    var textToDelete by remember { mutableStateOf<SavedText?>(null) }

    // Cargar el historial desde Firestore para el usuario actual
    LaunchedEffect(userId) {
        FirestoreHelper.getSavedTexts(userId) { texts ->
            history = texts // Aquí se espera que getSavedTexts devuelva List<SavedText>
        }
    }

    // Inicialización de TextToSpeech
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                ttsInitialized = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
    }

    // Diálogo de confirmación para eliminar un texto del historial
    if (textToDelete != null) {
        AlertDialog(
            onDismissRequest = { textToDelete = null },
            title = { Text("Eliminar elemento") },
            text = { Text("¿Estás seguro de eliminar este elemento?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Se envía el id para eliminar el documento
                        FirestoreHelper.deleteText(textToDelete!!.id)
                        history = history.filter { it.id != textToDelete!!.id }
                        textToDelete = null
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { textToDelete = null }) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escribir") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        // Sin fondo especial; se usa el color de fondo por defecto
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Instrucciones: Título e instrucciones
            Text(
                text = "Instrucciones",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Escribe el texto que deseas escuchar y presiona 'Leer en voz alta' para reproducirlo. Guarda el texto en tu historial con 'Guardar', y en el historial podrás reproducirlo (icono de play) o eliminarlo (icono de eliminar).",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Escribe aquí") },
                modifier = Modifier.fillMaxWidth()
            )
            // Botón para reproducir el texto ingresado
            Button(
                onClick = {
                    if (ttsInitialized) {
                        tts?.speak(inputText, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Leer en voz alta")
            }
            // Botón para guardar el texto en Firebase Firestore
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        FirestoreHelper.saveText(userId, inputText) { success, docId ->
                            if (success) {
                                // Actualiza el historial local agregando el nuevo objeto SavedText
                                val newText = SavedText(docId, inputText)
                                history = history + newText
                                inputText = ""
                            } else {
                                // Opcional: manejar el error (por ejemplo, mostrando un mensaje)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
            // Historial de textos guardados
            if (history.isNotEmpty()) {
                Text("Historial:", style = MaterialTheme.typography.titleMedium)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history) { savedText ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(savedText.text, modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    if (ttsInitialized) {
                                        tts?.speak(savedText.text, TextToSpeech.QUEUE_FLUSH, null, null)
                                    }
                                }) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Reproducir")
                                }
                                IconButton(onClick = {
                                    // Muestra el diálogo de confirmación para eliminar
                                    textToDelete = savedText
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
