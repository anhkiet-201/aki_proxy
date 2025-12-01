package com.aki.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aki.app.ui.edit_config.EditConfigScreen
import com.aki.app.ui.greeting.GreetingScreen
import com.aki.app.ui.home.HomeScreen
import com.aki.app.ui.recents.RecentsConfigsScreen
import com.aki.app.ui.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Home.route) {
        composable(Routes.Home.route) {
            HomeScreen(
                onNavigateToSettings = { navController.navigate(Routes.Settings.route) },
                onNavigateToRecents = { navController.navigate(Routes.RecentsConfigs.route) }
            )
        }
        composable(Routes.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Routes.RecentsConfigs.route) {
            RecentsConfigsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddConfig = { navController.navigate(Routes.EditConfig.route) },
                onConfigSelected = { navController.popBackStack() } // Quay lại khi một config được chọn
            )
        }
        composable(Routes.EditConfig.route) {
            EditConfigScreen(
                onNavigateBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() } // Quay lại sau khi lưu
            )
        }
        composable(Routes.Greeting.route) {
            GreetingScreen()
        }
    }
}
