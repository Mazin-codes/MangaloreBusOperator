package com.example.bustrackoperator.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DriverApp() {

    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    val startDestination =
        if (auth.currentUser == null) "login" else "route"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen {
                navController.navigate("route") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        composable("route") {
            RouteSelectionScreen(
                onRouteSelected = { routeId ->
                    navController.navigate("tracking/$routeId")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("route") { inclusive = true }
                    }
                }
            )
        }

        composable(
            "tracking/{routeId}",
            arguments = listOf(navArgument("routeId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val routeId =
                backStackEntry.arguments?.getString("routeId") ?: "1"

            DriverTrackingScreen(routeId)
        }
    }
}