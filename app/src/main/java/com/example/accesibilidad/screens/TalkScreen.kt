package com.example.accesibilidad.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkScreen(navController: NavController) {
    val context = LocalContext.current
    var performingSpeechSetup by remember { mutableStateOf(false) }
    val activity = context as? Activity
    var recognizedText by remember { mutableStateOf("Aquí aparecerá lo que digas") }
    var isListening by remember { mutableStateOf(false) }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                performingSpeechSetup = false
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onError(error: Int) {
                isListening = false
                when (error) {
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                        recognizedText = "Error de red: Timeout. Verifica tu conexión e inténtalo de nuevo."
                    }
                    else -> {
                        recognizedText = "Error en el reconocimiento, inténtalo de nuevo. (Error: $error)"
                    }
                }
                Log.e("SpeechRecognizer", "Error $error")
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        onDispose {
            speechRecognizer.destroy()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hablar") },
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = recognizedText, style = MaterialTheme.typography.bodyLarge)
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            activity?.requestPermissions(
                                arrayOf(Manifest.permission.RECORD_AUDIO),
                                101
                            )
                            return@Button
                        }
                        if (!isListening) {
                            isListening = true
                            recognizedText = "Escuchando..."
                            performingSpeechSetup = true
                            speechRecognizer.startListening(recognizerIntent)
                        }
                    }
                ) {
                    Text(if (isListening) "Escuchando..." else "Presiona para hablar")
                }
            }
        }
    }
}
