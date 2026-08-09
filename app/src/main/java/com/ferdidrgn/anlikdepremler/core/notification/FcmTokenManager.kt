package com.ferdidrgn.anlikdepremler.core.notification

import android.content.Context
import android.os.Build
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.util.UUID

object FcmTokenManager {

    fun syncFcmToken(context: Context) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener

            val token = task.result ?: return@addOnCompleteListener
            saveTokenToFirestore(context, token)
        }
    }

    private fun saveTokenToFirestore(context: Context, token: String) {
        val db = FirebaseFirestore.getInstance()

        // Cihaza özel kalıcı benzersiz ID (Login gerektirmeden)
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        var deviceId = sharedPrefs.getString("device_id", null)
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            sharedPrefs.edit().putString("device_id", deviceId).apply()
        }

        val deviceData = hashMapOf(
            "fcmToken" to token,
            "deviceId" to deviceId,
            "deviceModel" to Build.MODEL,
            "osVersion" to Build.VERSION.RELEASE,
            "lastActive" to System.currentTimeMillis()
        )

        db.collection("devices").document(deviceId)
            .set(deviceData)
    }
}