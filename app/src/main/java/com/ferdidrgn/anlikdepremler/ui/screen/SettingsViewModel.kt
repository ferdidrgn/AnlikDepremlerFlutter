package com.ferdidrgn.anlikdepremler.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferdidrgn.anlikdepremler.core.datastore.PreferencesManager
import com.ferdidrgn.anlikdepremler.core.language.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingsEvent {
    data class SendEmail(val email: String) : SettingsEvent()
    object OpenNotificationSettings : SettingsEvent()
    object OpenLocationSettings : SettingsEvent()
    object RequestReview : SettingsEvent()
    object ShareApp : SettingsEvent()
    data class NavigateToWeb(val url: String) : SettingsEvent()
    data class BuyCoffee(val productId: String) : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val currentLanguage = preferencesManager.selectedLanguage.map {
        AppLanguage.fromCode(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.TURKISH)

    private val _eventFlow = MutableSharedFlow<SettingsEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onLanguageSelected(language: AppLanguage) {
        viewModelScope.launch {
            preferencesManager.saveSelectedLanguage(language.code)
        }
    }

    fun onContactUsClick() {
        viewModelScope.launch {
            _eventFlow.emit(SettingsEvent.SendEmail("destek@sarsintitakip.com"))
        }
    }

    fun onNotificationPermissionClick() {
        viewModelScope.launch { _eventFlow.emit(SettingsEvent.OpenNotificationSettings) }
    }

    fun onLocationPermissionClick() {
        viewModelScope.launch { _eventFlow.emit(SettingsEvent.OpenLocationSettings) }
    }

    fun onRateAppClick() {
        viewModelScope.launch { _eventFlow.emit(SettingsEvent.RequestReview) }
    }

    fun onShareAppClick() {
        viewModelScope.launch { _eventFlow.emit(SettingsEvent.ShareApp) }
    }

    fun onPrivacyPolicyClick() {
        viewModelScope.launch {
            _eventFlow.emit(SettingsEvent.NavigateToWeb("https://sarsintitakip.com/privacy"))
        }
    }

    fun onTermsAndConditionsClick() {
        viewModelScope.launch {
            _eventFlow.emit(SettingsEvent.NavigateToWeb("https://sarsintitakip.com/terms"))
        }
    }

    fun onBuyCoffeeClick() {
        viewModelScope.launch {
            _eventFlow.emit(SettingsEvent.BuyCoffee("donation_small_coffee"))
        }
    }
}