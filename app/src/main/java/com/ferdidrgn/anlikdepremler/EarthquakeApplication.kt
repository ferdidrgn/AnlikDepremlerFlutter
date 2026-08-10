package com.ferdidrgn.anlikdepremler

import android.app.Application
import com.ferdidrgn.anlikdepremler.core.ads.AdManager
import com.ferdidrgn.anlikdepremler.core.notification.FcmTokenManager
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EarthquakeApplication : Application() {

    @Inject
    lateinit var adManager: AdManager

    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            FcmTokenManager.syncFcmToken(this)

            adManager.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}