# ===================================================================
# Deprem & Veri Modellerini Koruma
# ===================================================================
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Veri modellerinizin paket isimlerini korur
-keep class com.ferdidrgn.anlikdepremler.model.** { *; }
-keep class com.ferdidrgn.anlikdepremler.data.** { *; }

# ===================================================================
# Network (Retrofit, OkHttp, Gson)
# ===================================================================
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# ===================================================================
# Jetpack Compose & Navigation
# ===================================================================
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ===================================================================
# Dagger Hilt
# ===================================================================
-keep class * extends androidx.lifecycle.ViewModel
-keep class *_Factory { *; }
-keep class *_MembersInjector { *; }
-keep class **..*Dagger* { *; }
-keep class **..*Hilt* { *; }

# ===================================================================
# Firebase, Google Play & AdMob
# ===================================================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ===================================================================
# Coroutines & DataStore
# ===================================================================
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**