package com.ferdidrgn.anlikdepremler

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ferdidrgn.anlikdepremler.ui.screen.EarthquakeListScreen
import com.ferdidrgn.anlikdepremler.core.ads.AdManager
import com.ferdidrgn.anlikdepremler.core.language.LocaleHelper
import com.ferdidrgn.anlikdepremler.core.navigation.DeepLinkHelper
import com.ferdidrgn.anlikdepremler.core.util.CrashlyticsLogger
import com.ferdidrgn.anlikdepremler.navigation.Screen
import com.ferdidrgn.anlikdepremler.ui.components.CustomBottomNavigationBar
import com.ferdidrgn.anlikdepremler.ui.components.OfflineBanner
import com.ferdidrgn.anlikdepremler.ui.screen.EarthquakeDetailScreen
import com.ferdidrgn.anlikdepremler.ui.screen.HomeScreen
import com.ferdidrgn.anlikdepremler.ui.screen.LegalDocumentScreen
import com.ferdidrgn.anlikdepremler.ui.screen.MainViewModel
import com.ferdidrgn.anlikdepremler.ui.screen.MapScreen
import com.ferdidrgn.anlikdepremler.ui.screen.OnboardingScreen
import com.ferdidrgn.anlikdepremler.ui.screen.SettingsScreen
import com.ferdidrgn.anlikdepremler.ui.screen.SplashScreen
import com.ferdidrgn.anlikdepremler.ui.theme.AppThemeMode
import com.ferdidrgn.anlikdepremler.ui.theme.DepremTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var adManager: AdManager

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedLanguage = prefs.getString("selected_language", "tr") ?: "tr"
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase, savedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupCoreHelpers()

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val uiState by mainViewModel.uiState.collectAsState()
            val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsState()

            var isSplashActive by remember { mutableStateOf(true) }

            DepremTheme(themeMode = uiState.currentTheme) {
                val isDarkTheme = when (uiState.currentTheme) {
                    AppThemeMode.DARK_NIGHT -> true
                    AppThemeMode.CREAM_LIGHT -> false
                    AppThemeMode.SYSTEM_DYNAMIC -> isSystemInDarkTheme()
                }

                SideEffect {
                    window.statusBarColor = Color.Transparent.toArgb()
                    window.navigationBarColor = Color.Transparent.toArgb()

                    val windowInsetsController =
                        WindowCompat.getInsetsController(window, window.decorView)
                    windowInsetsController.isAppearanceLightStatusBars = !isDarkTheme
                    windowInsetsController.isAppearanceLightNavigationBars = !isDarkTheme
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isSplashActive)
                            SplashScreen(onSplashFinished = { isSplashActive = false })
                        else if (!isOnboardingCompleted)
                            OnboardingScreen(onFinishOnboarding = { mainViewModel.completeOnboarding() })
                        else
                            MainAppScreen(
                                mainViewModel = mainViewModel,
                                adManager = adManager
                            )
                    }
                }
            }
        }
    }

    private fun setupCoreHelpers() {
        try {
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                CrashlyticsLogger.log("Uncaught Exception in Thread: ${thread.name}")
                CrashlyticsLogger.logException(throwable)
            }
        } catch (e: Exception) {
            CrashlyticsLogger.logException(e)
        }
    }
}

@Composable
fun MainAppScreen(
    mainViewModel: MainViewModel,
    adManager: AdManager
) {
    val navController = rememberNavController()
    val uiState by mainViewModel.uiState.collectAsState()
    val isConnected by mainViewModel.isConnected.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            OfflineBanner(isConnected = isConnected)
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val shouldShowBottomBar = currentRoute != null &&
                    !currentRoute.startsWith("detail/") &&
                    !currentRoute.startsWith("legal/") &&
                    currentRoute != Screen.Map.route

            if (shouldShowBottomBar) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. ANA SAYFA
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = mainViewModel,
                    onEarthquakeClick = { selectedEq ->
                        val activity = context.findActivity()
                        if (activity != null) {
                            adManager.showInterstitial(activity) {
                                navController.navigate("detail/${selectedEq.id}")
                            }
                        } else {
                            navController.navigate("detail/${selectedEq.id}")
                        }
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
                        val activity = context.findActivity()
                        if (activity != null) {
                            adManager.showInterstitial(activity) {
                                navController.navigate("detail/${selectedEq.id}")
                            }
                        } else {
                            navController.navigate("detail/${selectedEq.id}")
                        }
                    }
                )
            }

            // 3. HARİTA EKRANI
            composable(Screen.Map.route) {
                MapScreen(
                    viewModel = mainViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 4. AYARLAR EKRANI
            composable(Screen.Settings.route) {
                SettingsScreen(
                    settingsViewModel = hiltViewModel(),
                    onOpenLegalDocument = { docType ->
                        navController.navigate("legal/$docType")
                    }
                )
            }

            // 5. DEPREM DETAY EKRANI (Deeplink Destekli)
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

            // 6. YASAL METİNLER EKRANI
            composable("legal/{docType}") { backStackEntry ->
                val docType = backStackEntry.arguments?.getString("docType") ?: "privacy_policy"
                LegalDocumentScreen(
                    documentType = docType,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

// Activity bulucu Extension
fun Context.findActivity(): Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}