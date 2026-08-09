package com.ferdidrgn.anlikdepremler.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val SELECTED_SOURCE_KEY = stringPreferencesKey("selected_source")
    private val APP_THEME_KEY = stringPreferencesKey("app_theme")
    private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    private val SELECTED_LANGUAGE_KEY = stringPreferencesKey("selected_language")

    // --- DEPREM VERİ KAYNAĞI ---
    val selectedSource: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SELECTED_SOURCE_KEY] ?: "KANDILLI"
    }

    suspend fun saveSelectedSource(source: String) {
        context.dataStore.edit { prefs -> prefs[SELECTED_SOURCE_KEY] = source }
    }

    // --- DİL SEÇİMİ ---
    val selectedLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SELECTED_LANGUAGE_KEY] ?: "tr"
    }

    suspend fun saveSelectedLanguage(languageCode: String) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_LANGUAGE_KEY] = languageCode
        }
    }

    // --- TEMA SEÇİMİ ---
    val appTheme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[APP_THEME_KEY] ?: "CREAM_LIGHT"
    }

    suspend fun saveAppTheme(theme: String) {
        context.dataStore.edit { prefs -> prefs[APP_THEME_KEY] = theme }
    }

    // SettingsViewModel ile geriye dönük uyumluluk (Eski kodların patlamaması için)
    val selectedThemeMode: Flow<String> get() = appTheme

    suspend fun saveSelectedThemeMode(themeKey: String) {
        saveAppTheme(themeKey)
    }

    // --- ONBOARDING TAMAMLANMA DURUMU ---
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED_KEY] ?: false
    }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETED_KEY] = completed }
    }
}