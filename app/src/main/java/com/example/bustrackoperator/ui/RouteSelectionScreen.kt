package com.example.bustrackoperator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*

@Composable
fun RouteSelectionScreen(
    onRouteSelected: (String) -> Unit,
    onLogout: () -> Unit
) {

    val routes = remember { mutableStateListOf<Pair<String, String>>() }
    val database = FirebaseDatabase.getInstance().reference

    LaunchedEffect(Unit) {
        database.child("routes")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    routes.clear()
                    for (routeSnap in snapshot.children) {
                        val id = routeSnap.key ?: continue
                        val name = routeSnap.child("name")
                            .getValue(String::class.java) ?: "Route $id"
                        routes.add(id to name)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            "Select Route",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
            items(routes) { (id, name) ->
                Button(
                    onClick = { onRouteSelected(id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(name)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                com.google.firebase.auth.FirebaseAuth
                    .getInstance()
                    .signOut()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("LOGOUT")
        }
    }
}