// Project level: build.gradle.kts
plugins {
    id("com.android.application") version "8.7.2" apply false

    // 📌 Kotlin & Compose derleyicisini 2.1.0 yapıyoruz
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false

    // 📌 KSP sürümünü Kotlin 2.1.0 ile uyumluürüme çekiyoruz
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.1" apply false
}