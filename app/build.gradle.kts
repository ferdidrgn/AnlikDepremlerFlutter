import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    // 📌 SÜRÜMÜ KÖK DİZİNDEN (ROOT) ALDIĞI İÇİN BURADA SADECE ID VEYA ALIAS İLE ÇAĞIRIYORUZ:
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

// --- local.properties okuma ---
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(FileInputStream(file))
}

fun secret(key: String, default: String = ""): String =
    localProperties.getProperty(key, default)

android {
    namespace = "com.ferdidrgn.anlikdepremler"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ferdidrgn.anlikdepremler"
        minSdk = 24
        targetSdk = 36
        versionCode = 32
        versionName = "1.32"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
        resourceConfigurations.addAll(
            listOf("tr", "en", "de", "es", "it", "ru", "uk", "el", "ky", "uz", "ar", "ko", "ja", "zh")
        )

        manifestPlaceholders["API_KEY_ADMOB"] = secret("API_KEY_ADMOB")
        manifestPlaceholders["API_KEY_LOCATION"] = secret("API_KEY_LOCATION")
    }

    // 🎯 AAB BUNDLE DİL VE MİMARİ BÖLÜNMESİNİ GÜVENLİ HALE GETİRME
    bundle {
        language.enableSplit = false
        density.enableSplit = true
        abi.enableSplit = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            // Debug için Google resmi Test ID'leri
            buildConfigField(
                "String",
                "ADMOB_APP_OPEN_ID",
                "\"ca-app-pub-3940256099942544/9257395921\""
            )
            buildConfigField(
                "String",
                "ADMOB_BANNER_ID",
                "\"ca-app-pub-3940256099942544/6300978111\""
            )
            buildConfigField(
                "String",
                "ADMOB_INTERSTITIAL_ID",
                "\"ca-app-pub-3940256099942544/1033173712\""
            )
            buildConfigField(
                "String",
                "ADMOB_NATIVE_ID",
                "\"ca-app-pub-3940256099942544/2247696110\""
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            // 📌 YERELDE (IDE ÜZERİNDEN) RELEASE TESTİ YAPABİLMEK İÇİN:
            signingConfig = signingConfigs.getByName("debug")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val appOpenId = secret("ADMOB_APP_OPEN_ID")
            val bannerId = secret("ADMOB_BANNER_ID")
            val interstitialId = secret("ADMOB_INTERSTITIAL_ID")
            val nativeId = secret("ADMOB_NATIVE_ID")

            buildConfigField("String", "ADMOB_APP_OPEN_ID", "\"$appOpenId\"")
            buildConfigField("String", "ADMOB_BANNER_ID", "\"$bannerId\"")
            buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"$interstitialId\"")
            buildConfigField("String", "ADMOB_NATIVE_ID", "\"$nativeId\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // --- Core & AndroidX ---
    implementation(libs.androidx.core.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")

    // --- Jetpack Compose ---
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.ui:ui-graphics:1.6.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation(libs.material)

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")

    // 📌 Firebase BoM (Bill of Materials) - Sürüm yönetimini BoM yapar
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Cloud Firestore (Hissettim bildirimleri, anonim istatistikler için)
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Firebase Cloud Messaging (Arka plan bildirimi için)
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // --- 🛒 Google Play Billing (Google Play Politikasına %100 Uyumlu) ---
    implementation("com.android.billingclient:billing-ktx:8.0.0")

    // --- 📢 Google AdMob ---
    implementation("com.google.android.gms:play-services-ads:23.3.0")

    // 🌟 Google Play In-App Review
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

    // --- Dagger Hilt ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // --- Network (Retrofit & OkHttp & Gson) ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- Coil ---
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- Preferences DataStore ---
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // --- Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // --- System UI Controller ---
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // 🗺️ Maps Compose & Clustering
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.maps.android:maps-compose-utils:4.3.3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // --- Testing & Debugging ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")
}