package com.example.accesibilidad.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadScreen(navController: NavController) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var ttsInitialized by remember { mutableStateOf(false) }
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                ttsInitialized = true  // Actualiza el estado para indicar que TTS está listo
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Escribe aquí") },
                    modifier = Modifier.fillMaxWidth()
                )
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
            }
        }
    }
}
