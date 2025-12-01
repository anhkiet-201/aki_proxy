package com.aki.app.navigation

/**
 * A sealed class that defines the available routes in the application.
 */
sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Settings : Routes("settings")
    object RecentsConfigs : Routes("recents_configs")
    object EditConfig : Routes("edit_config")
    object Greeting : Routes("greeting")
    // Add other routes here
}