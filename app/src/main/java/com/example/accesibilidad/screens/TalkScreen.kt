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
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.accesibilidad.helpers.FirestoreHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkScreen(navController: NavController) {
    val context = LocalContext.current
    var performingSpeechSetup by remember { mutableStateOf(false) }
    val activity = context as? Activity
    var recognizedText by remember { mutableStateOf("Aquí aparecerá lo que digas") }
    var isListening by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Obtén el userId del usuario actual
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: "unknown"

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
                Log.d("TalkScreen", "onReadyForSpeech")
            }
            override fun onBeginningOfSpeech() {
                Log.d("TalkScreen", "onBeginningOfSpeech")
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
                Log.d("TalkScreen", "onEndOfSpeech")
            }
            override fun onError(error: Int) {
                isListening = false
                Log.e("SpeechRecognizer", "Error $error")
                when (error) {
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                        recognizedText = "Error de red: Timeout. Verifica tu conexión e inténtalo de nuevo."
                    }
                    else -> {
                        recognizedText = "Error en el reconocimiento, inténtalo de nuevo. (Error: $error)"
                    }
                }
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                }
                Log.d("TalkScreen", "onResults: $recognizedText")
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

    // Función para iniciar la escucha y forzar detenerla a los 30 segundos
    fun startListeningWithTimeout() {
        isListening = true
        recognizedText = "Escuchando..."
        performingSpeechSetup = true
        speechRecognizer.startListening(recognizerIntent)
        scope.launch {
            delay(30_000L) // 30 segundos
            if (isListening) {
                isListening = false
                speechRecognizer.stopListening()
            }
        }
    }

    // Snackbar para mostrar confirmación al guardar
    fun showSavedSnackbar() {
        scope.launch {
            snackbarHostState.showSnackbar("El texto se guardó")
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Instrucciones",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Presiona 'Presiona para hablar' para iniciar el reconocimiento de voz. Puedes detenerlo manualmente con 'Detener' o se detendrá automáticamente a los 30 segundos. Una vez finalizado, el texto se mostrará en pantalla y podrás guardarlo para escucharlo después.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = recognizedText,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(8.dp)
                )

                Button(
                    onClick = {
                        if (activity == null) {
                            Log.e("TalkScreen", "Activity is null, cannot request permissions.")
                            return@Button
                        }
                        // Verificar permiso RECORD_AUDIO
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d("TalkScreen", "Permission RECORD_AUDIO not granted, requesting...")
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(Manifest.permission.RECORD_AUDIO),
                                101
                            )
                            return@Button
                        }
                        if (!isListening) {
                            startListeningWithTimeout()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isListening) "Escuchando..." else "Presiona para hablar")
                }

                if (isListening) {
                    Button(
                        onClick = {
                            isListening = false
                            speechRecognizer.stopListening()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Detener")
                    }
                }

                Button(
                    onClick = {
                        if (recognizedText.isNotBlank() && recognizedText != "Escuchando...") {
                            FirestoreHelper.saveText(userId, recognizedText) { success, _ ->
                                if (success) {
                                    showSavedSnackbar()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
