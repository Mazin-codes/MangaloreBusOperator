package com.example.bustrackoperator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bustrackoperator.ui.DriverScreen
import com.example.bustrackoperator.ui.theme.BusTrackOperatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BusTrackOperatorTheme {
                DriverScreen()
            }
        }
    }
}