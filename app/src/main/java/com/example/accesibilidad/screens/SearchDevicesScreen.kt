package com.example.accesibilidad.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDevicesScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    // Función para actualizar la ubicación
    fun updateLocation() {
        // Verifica que se tenga al menos uno de los permisos requeridos.
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            return
        }

        isSearching = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            isSearching = false
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
            } else {
                latitude = null
                longitude = null
            }
        }.addOnFailureListener {
            isSearching = false

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Ubicación") },
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
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(
                onClick = { updateLocation() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar")
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isSearching) {
                Text("Buscando ubicación...", style = MaterialTheme.typography.bodyLarge)
            } else {
                if (latitude != null && longitude != null) {
                    Text(
                        text = "Latitud: $latitude",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Longitud: $longitude",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Utilizamos WebView para mostrar un iframe de Google Maps
                    val htmlData = """
                        <html>
                        <head>
                          <meta name="viewport" content="width=device-width, initial-scale=1.0">
                          <style>body, html { margin: 0; padding: 0; }</style>
                        </head>
                        <body>
                          <iframe 
                              width="100%" 
                              height="300" 
                              frameborder="0" 
                              style="border:0" 
                              src="https://maps.google.com/maps?q=$latitude,$longitude&hl=es;&output=embed" 
                              allowfullscreen>
                          </iframe>
                        </body>
                        </html>
                    """.trimIndent()

                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                } else {
                    Text("No se encontró ubicación.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
