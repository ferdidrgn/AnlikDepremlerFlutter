package com.ferdidrgn.anlikdepremler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ferdi.deprem.ui.screens.EarthquakeListScreen
import com.ferdidrgn.anlikdepremler.core.navigation.DeepLinkHelper
import com.ferdidrgn.anlikdepremler.navigation.Screen
import com.ferdidrgn.anlikdepremler.ui.components.CustomBottomNavigationBar
import com.ferdidrgn.anlikdepremler.ui.screen.EarthquakeDetailScreen
import com.ferdidrgn.anlikdepremler.ui.screen.HomeScreen
import com.ferdidrgn.anlikdepremler.ui.screen.MainViewModel
import com.ferdidrgn.anlikdepremler.ui.screen.MapScreen
import com.ferdidrgn.anlikdepremler.ui.screen.OnboardingScreen
import com.ferdidrgn.anlikdepremler.ui.screen.SettingsScreen
import com.ferdidrgn.anlikdepremler.ui.screen.SplashScreen
import com.ferdidrgn.anlikdepremler.ui.theme.DepremTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val uiState by mainViewModel.uiState.collectAsState()
            val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsState()

            var isSplashActive by remember { mutableStateOf(true) }

            DepremTheme(themeMode = uiState.currentTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isSplashActive) {
                        SplashScreen(onSplashFinished = { isSplashActive = false })
                    } else if (!isOnboardingCompleted) {
                        OnboardingScreen(onFinishOnboarding = { mainViewModel.completeOnboarding() })
                    } else {
                        MainAppScreen(mainViewModel = mainViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppScreen(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val uiState by mainViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Detay ekranına geçildiğinde bottom bar gizlenir
            if (currentRoute != null && !currentRoute.startsWith("detail/")) {
                CustomBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavItemClick = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. ANA SAYFA
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = mainViewModel,
                    onEarthquakeClick = { selectedEq ->
                        navController.navigate("detail/${selectedEq.id}")
                    },
                    onSeeAllClick = {
                        navController.navigate(Screen.Earthquakes.route)
                    }
                )
            }

            // 2. TÜM DEPREMLER LİSTESİ
            composable(Screen.Earthquakes.route) {
                EarthquakeListScreen(
                    viewModel = mainViewModel,
                    onEarthquakeClick = { selectedEq ->
                        navController.navigate("detail/${selectedEq.id}")
                    }
                )
            }

            // 3. HARİTA EKRANI
            composable(Screen.Map.route) {
                MapScreen(viewModel = mainViewModel)
            }

            // 4. AYARLAR EKRANI
            composable(Screen.Settings.route) {
                SettingsScreen(settingsViewModel = hiltViewModel())
            }

            // 5. DEPREM DETAY EKRANI (Deeplink Eklenmiş Hali)
            composable(
                route = "detail/{earthquakeId}",
                arguments = listOf(navArgument("earthquakeId") { type = NavType.StringType }),
                deepLinks = DeepLinkHelper.earthquakeDetailDeepLink
            ) { backStackEntry ->
                val eqId = backStackEntry.arguments?.getString("earthquakeId")
                val earthquake = uiState.earthquakes.find { it.id == eqId }

                if (earthquake != null) {
                    EarthquakeDetailScreen(
                        earthquake = earthquake,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}