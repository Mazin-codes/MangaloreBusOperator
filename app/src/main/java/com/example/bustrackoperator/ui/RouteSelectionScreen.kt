package com.example.bustrackoperator.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RouteSelectionScreen(onRouteSelected: (String) -> Unit) {

    var routes by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance()
            .getReference("routes")
            .get()
            .addOnSuccessListener { snapshot ->

                val list = mutableListOf<Pair<String, String>>()

                for (routeSnap in snapshot.children) {
                    val id = routeSnap.key ?: continue
                    val name = routeSnap.child("name")
                        .getValue(String::class.java) ?: "Route $id"

                    list.add(Pair(id, name))
                }

                routes = list
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text("Select Route", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            routes.forEach { (id, name) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onRouteSelected(id) }
                ) {
                    Text(
                        text = name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}