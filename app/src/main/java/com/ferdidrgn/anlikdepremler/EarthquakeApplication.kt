package com.ferdidrgn.anlikdepremler

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EarthquakeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}