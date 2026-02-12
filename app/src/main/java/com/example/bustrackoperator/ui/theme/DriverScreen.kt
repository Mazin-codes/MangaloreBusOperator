package com.example.bustrackoperator.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.annotation.SuppressLint

@Composable
fun DriverScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val database = remember { FirebaseDatabase.getInstance().reference }

    var isTripActive by remember { mutableStateOf(false) }
    val tripId = remember { "trip_${System.currentTimeMillis()}" }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op */ }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isTripActive) "Trip Active" else "Trip Inactive",
            style = MaterialTheme.typography.headlineMedium,
            color = if (isTripActive) Color(0xFF2E7D32) else Color(0xFFC62828)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isTripActive = true
                startLocationUpdates(context, fusedLocationClient, database, tripId)
            },
            enabled = !isTripActive,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("START TRIP")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                isTripActive = false
                database.child("liveTrips").child(tripId).removeValue()
            },
            enabled = isTripActive,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("END TRIP")
        }
    }
}

@SuppressLint("MissingPermission")
private fun startLocationUpdates(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    database: DatabaseReference,
    tripId: String
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5000
    ).build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            val data = mapOf(
                "lat" to location.latitude,
                "lng" to location.longitude,
                "timestamp" to System.currentTimeMillis()
            )
            database.child("liveTrips").child(tripId).setValue(data)
        }
    }

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) return

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}