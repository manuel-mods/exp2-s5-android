package com.example.accesibilidad


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import NavigationComponent
import com.example.accesibilidad.helpers.LocationHelper


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationComponent()
        }
    }
}