# ===================================================================
# Deprem Model Sınıflarını R8/ProGuard Karartmasından Koru (KRİTİK!)
# ===================================================================
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Data modellerinizin paket isimlerini koru
-keep class com.ferdi.deprem.model.** { *; }
-keep class com.ferdidrgn.anlikdepremler.model.** { *; }
-keep class com.ferdidrgn.anlikdepremler.data.** { *; }

# Retrofit, OkHttp & Gson
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# Firebase & Google Play Services
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Hilt / Dagger
-keep class * extends androidx.lifecycle.ViewModel
-keep class *_Factory { *; }
-keep class *_MembersInjector { *; }