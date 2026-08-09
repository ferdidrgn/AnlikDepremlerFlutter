package com.ferdidrgn.anlikdepremler.core.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleUtils {

    /**
     * Uygulama dilini anında değiştirir. 
     * Activity restart gerektirmeden tüm Compose ekranları o dile döner.
     */
    fun setAppLanguage(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}