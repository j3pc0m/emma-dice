package org.emmadice.app.navigation

sealed class AppScreen(val route: String) {

    object Home : AppScreen("home")

    object Communication : AppScreen("communication")

    object Settings : AppScreen("settings")

    object About : AppScreen("about")

}