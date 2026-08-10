package com.ferdidrgn.anlikdepremler.core.util

import android.location.Location

object LocationUtils {

    /**
     * İki koordinat arasındaki mesafeyi kilometre (KM) cinsinden hesaplar.
     */
    fun calculateDistanceInKm(
        userLat: Double,
        userLng: Double,
        eqLat: Double,
        eqLng: Double
    ): Double {
        val startPoint = Location("user").apply {
            latitude = userLat
            longitude = userLng
        }
        val endPoint = Location("earthquake").apply {
            latitude = eqLat
            longitude = eqLng
        }
        return (startPoint.distanceTo(endPoint) / 1000.0) // Metreyi KM'ye çevirir
    }
}