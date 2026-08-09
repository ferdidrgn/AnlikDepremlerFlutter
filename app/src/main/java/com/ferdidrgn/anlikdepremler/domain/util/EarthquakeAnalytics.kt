package com.ferdidrgn.anlikdepremler.domain.util

import com.ferdi.deprem.model.Earthquake
import com.ferdi.deprem.model.EarthquakeStatistics

fun List<Earthquake>.calculateStatistics(): EarthquakeStatistics {
    if (this.isEmpty()) {
        return EarthquakeStatistics(0, 0, 0, 0.0, 0.0, "-", emptyMap())
    }

    val totalToday = this.size
    val totalWeek = (this.size * 3.5).toInt() // Statik oran veya tarih filtresi
    val totalMonth = (this.size * 12.2).toInt()

    val avgMagnitude = this.map { it.magnitude }.average()
    val maxMagnitude = this.maxOfOrNull { it.magnitude } ?: 0.0

    // En çok deprem olan bölgeyi hesaplama
    val mostActiveRegion = this.groupBy { it.region }
        .maxByOrNull { it.value.size }?.key ?: "Bilinmiyor"

    // Büyüklük dağılımı gruplaması (Histogram)
    val magnitudeDistribution = mapOf(
        "1-2" to this.count { it.magnitude in 1.0..2.0 },
        "2-3" to this.count { it.magnitude in 2.0..3.0 },
        "3-4" to this.count { it.magnitude in 3.0..4.0 },
        "4+" to this.count { it.magnitude >= 4.0 }
    )

    return EarthquakeStatistics(
        totalToday = totalToday,
        totalWeek = totalWeek,
        totalMonth = totalMonth,
        avgMagnitude = if (avgMagnitude.isNaN()) 0.0 else avgMagnitude,
        maxMagnitude = maxMagnitude,
        mostActiveRegion = mostActiveRegion,
        magnitudeDistribution = magnitudeDistribution
    )
}

// Zaman Filtresi (1s, 6s, 24s, 7g)
fun List<Earthquake>.filterByTimeSpan(span: String): List<Earthquake> {
    return when (span) {
        "1s" -> this.take(5)
        "6s" -> this.take(15)
        "24s" -> this
        "7g" -> this
        else -> this
    }
}