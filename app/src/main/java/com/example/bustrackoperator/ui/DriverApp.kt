package com.example.bustrackoperator.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument


@Composable
fun DriverApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen {
                navController.navigate("route")
            }
        }

        composable("route") {
            RouteSelectionScreen { routeId ->
                navController.navigate("tracking/$routeId")
            }
        }

        composable(
            "tracking/{routeId}",
            arguments = listOf(navArgument("routeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: "1"
            DriverTrackingScreen(routeId)
        }
    }
}