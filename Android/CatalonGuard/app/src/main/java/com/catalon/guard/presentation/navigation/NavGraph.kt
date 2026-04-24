package com.catalon.guard.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.catalon.guard.presentation.ui.chat.ChatScreen
import com.catalon.guard.presentation.ui.providers.ProvidersScreen
import com.catalon.guard.presentation.ui.projects.ProjectsScreen
import com.catalon.guard.presentation.ui.quota.QuotaDashboardScreen
import com.catalon.guard.presentation.ui.settings.SettingsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Chat : Screen("chat", "Chat", Icons.AutoMirrored.Filled.Chat)
    object Providers : Screen("providers", "Providers", Icons.Default.Tune)
    object Projects : Screen("projects", "Projects", Icons.Default.Folder)
    object Quota : Screen("quota", "Quota", Icons.Default.BarChart)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val bottomNavScreens = listOf(
    Screen.Chat, Screen.Providers, Screen.Projects, Screen.Quota, Screen.Settings
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Screen.Chat.route) {
            composable(Screen.Chat.route) { ChatScreen(innerPadding) }
            composable(Screen.Providers.route) { ProvidersScreen(innerPadding) }
            composable(Screen.Projects.route) { ProjectsScreen(innerPadding) }
            composable(Screen.Quota.route) { QuotaDashboardScreen(innerPadding) }
            composable(Screen.Settings.route) { SettingsScreen(innerPadding) }
        }
    }
}
