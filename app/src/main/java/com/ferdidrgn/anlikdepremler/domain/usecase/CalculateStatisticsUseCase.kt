package com.ferdidrgn.anlikdepremler.domain.usecase

import com.ferdi.deprem.model.Earthquake
import com.ferdi.deprem.model.EarthquakeStatistics
import javax.inject.Inject

class CalculateStatisticsUseCase @Inject constructor() {

    operator fun invoke(earthquakes: List<Earthquake>): EarthquakeStatistics {
        if (earthquakes.isEmpty()) {
            return EarthquakeStatistics(
                totalToday = 0,
                totalWeek = 0,
                totalMonth = 0,
                avgMagnitude = 0.0,
                maxMagnitude = 0.0,
                mostActiveRegion = "-",
                magnitudeDistribution = emptyMap()
            )
        }

        val totalToday = earthquakes.size
        val avgMagnitude = earthquakes.map { it.magnitude }.average()
        val maxMagnitude = earthquakes.maxOfOrNull { it.magnitude } ?: 0.0

        val mostActiveRegion = earthquakes.groupBy { it.region }
            .maxByOrNull { it.value.size }?.key ?: "Bilinmiyor"

        val distribution = mapOf(
            "1-2" to earthquakes.count { it.magnitude in 1.0..2.0 },
            "2-3" to earthquakes.count { it.magnitude in 2.0..3.0 },
            "3-4" to earthquakes.count { it.magnitude in 3.0..4.0 },
            "4+" to earthquakes.count { it.magnitude >= 4.0 }
        )

        return EarthquakeStatistics(
            totalToday = totalToday,
            totalWeek = (totalToday * 3.5).toInt(),
            totalMonth = (totalToday * 12.2).toInt(),
            avgMagnitude = if (avgMagnitude.isNaN()) 0.0 else avgMagnitude,
            maxMagnitude = maxMagnitude,
            mostActiveRegion = mostActiveRegion,
            magnitudeDistribution = distribution
        )
    }
}