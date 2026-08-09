package com.ferdi.deprem.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Ana Sayfa", Icons.Default.Home)
    object Earthquakes : Screen("earthquakes", "Depremler", Icons.Default.List)
    object Map : Screen("map", "Harita", Icons.Default.Map)
    object Settings : Screen("settings", "Ayarlar", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Earthquakes,
    Screen.Map,
    Screen.Settings
)