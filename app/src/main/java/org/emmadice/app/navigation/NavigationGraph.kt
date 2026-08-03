package org.emmadice.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.emmadice.app.HomeScreen
import org.emmadice.app.screens.AboutScreen
import org.emmadice.app.screens.CategoriesScreen
import org.emmadice.app.screens.CommunicationScreen
import org.emmadice.app.screens.SettingsScreen

@Composable
fun NavigationGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.route
    ) {

        composable(AppScreen.Home.route) {
            HomeScreen(navController)
        }

        composable("categories") {
            CategoriesScreen(navController)
        }

        composable(AppScreen.Communication.route) {
            CommunicationScreen()
        }

        composable(AppScreen.Settings.route) {
            SettingsScreen()
        }

        composable(AppScreen.About.route) {
            AboutScreen()
        }
    }
}