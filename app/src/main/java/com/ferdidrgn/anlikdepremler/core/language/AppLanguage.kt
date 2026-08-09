package com.ferdidrgn.anlikdepremler.core.language

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    TURKISH("tr", "Türkçe", "🇹🇷"),
    ENGLISH("en", "English", "🇬🇧"),
    GERMAN("de", "Deutsch", "🇩🇪"),
    ITALIAN("it", "Italiano", "🇮🇹"),
    RUSSIAN("ru", "Русский", "🇷🇺"),
    UKRAINIAN("uk", "Українська", "🇺🇦"),
    GREEK("el", "Ελληνικά", "🇬🇷"),
    KYRGYZ("ky", "Kırgızça / Кыргызча", "🇰🇬"),
    UZBEK("uz", "O'zbekcha", "🇺🇿"),
    ARABIC("ar", "العربية", "🇸🇦");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().find { it.code == code } ?: TURKISH
        }
    }
}