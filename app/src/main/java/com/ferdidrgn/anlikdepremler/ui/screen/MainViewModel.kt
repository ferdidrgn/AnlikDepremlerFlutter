package com.ferdidrgn.anlikdepremler.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferdi.deprem.model.Earthquake
import com.ferdi.deprem.model.EarthquakeStatistics
import com.ferdidrgn.anlikdepremler.core.datastore.PreferencesManager
import com.ferdidrgn.anlikdepremler.core.network.NetworkMonitor
import com.ferdidrgn.anlikdepremler.data.remote.EarthquakeSource
import com.ferdidrgn.anlikdepremler.domain.usecase.*
import com.ferdidrgn.anlikdepremler.domain.util.filterByTimeSpan
import com.ferdidrgn.anlikdepremler.ui.theme.AppThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

data class HomeUiState(
    val isLoading: Boolean = false,
    val earthquakes: List<Earthquake> = emptyList(),
    val rawEarthquakes: List<Earthquake> = emptyList(),
    val statistics: EarthquakeStatistics = EarthquakeStatistics(0, 0, 0, 0.0, 0.0, "-", emptyMap()),
    val selectedSource: EarthquakeSource = EarthquakeSource.KANDILLI,
    val currentTheme: AppThemeMode = AppThemeMode.CREAM_LIGHT,
    val selectedTimeFilter: String = "24s",
    val searchQuery: String = "",
    val locationSearchQuery: String = "",
    val isSearchingLocation: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getEarthquakesUseCase: GetEarthquakesUseCase,
    private val calculateStatisticsUseCase: CalculateStatisticsUseCase,
    private val saveUserPreferencesUseCase: SaveUserPreferencesUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val preferencesManager: PreferencesManager,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _locationQueryState = MutableStateFlow("")

    val isOnboardingCompleted = preferencesManager.isOnboardingCompleted.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val isConnected: StateFlow<Boolean> = networkMonitor.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    init {
        observeUserPreferences()
        setupDebouncedSearch()
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.saveOnboardingCompleted(true)
        }
    }

    private fun observeUserPreferences() {
        viewModelScope.launch {
            getUserPreferencesUseCase().collect { prefs ->
                _uiState.update {
                    it.copy(
                        selectedSource = prefs.selectedSource,
                        currentTheme = prefs.themeMode
                    )
                }
                loadEarthquakes()
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            _locationQueryState
                .debounce(3000L.milliseconds)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update {
                        it.copy(
                            isSearchingLocation = false,
                            locationSearchQuery = query
                        )
                    }
                    loadEarthquakes()
                }
        }
    }

    fun loadEarthquakes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val activeQuery =
                _uiState.value.locationSearchQuery.ifEmpty { _uiState.value.searchQuery }

            getEarthquakesUseCase(
                source = _uiState.value.selectedSource,
                query = activeQuery
            ).catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }.collect { list ->
                // 🎯 1. Analiz UseCase'i çalışıyor
                val stats = calculateStatisticsUseCase(list)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rawEarthquakes = list,
                        earthquakes = list.filterByTimeSpan(it.selectedTimeFilter),
                        statistics = stats,
                        errorMessage = null
                    )
                }
            }
        }
    }

    // 📌 Zaman Filtreleri Seçildiğinde
    fun onTimeFilterSelected(filter: String) {
        _uiState.update { current ->
            current.copy(
                selectedTimeFilter = filter,
                earthquakes = current.rawEarthquakes.filterByTimeSpan(filter)
            )
        }
    }

    private fun filterListByTime(list: List<Earthquake>, filter: String): List<Earthquake> {
        return when (filter) {
            "1s" -> list.take((list.size * 0.2).toInt().coerceAtLeast(2))
            "6s" -> list.take((list.size * 0.5).toInt().coerceAtLeast(5))
            "24s" -> list
            "7g" -> list
            "30g" -> list
            else -> list
        }
    }

    fun onSourceChanged(newSource: EarthquakeSource) {
        viewModelScope.launch {
            saveUserPreferencesUseCase.saveSource(newSource)
        }
    }

    fun onThemeChanged(newTheme: AppThemeMode) {
        viewModelScope.launch {
            saveUserPreferencesUseCase.saveTheme(newTheme)
        }
    }

    fun onLocationQueryTyped(newText: String) {
        _uiState.update { it.copy(isSearchingLocation = true) }
        _locationQueryState.value = newText
    }
}