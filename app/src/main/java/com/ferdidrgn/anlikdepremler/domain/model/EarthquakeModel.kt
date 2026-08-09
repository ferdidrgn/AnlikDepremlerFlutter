package com.ferdi.deprem.model

data class Earthquake(
    val id: String,
    val location: String,
    val region: String = "Marmara",
    val magnitude: Double,
    val depth: Double,
    val date: String,
    val time: String,
    val latitude: Double,
    val longitude: Double,
    val cityImageUrl: String,
    val isSignificant: Boolean = false,
    val intensity: String = "IV"
)

data class EarthquakeStatistics(
    val totalToday: Int,
    val totalWeek: Int,
    val totalMonth: Int,
    val avgMagnitude: Double,
    val maxMagnitude: Double,
    val mostActiveRegion: String,
    val magnitudeDistribution: Map<String, Int>
)

data class InfoCardItem(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val imageUrl: String
)

object MockData {
    val sampleEarthquakes = listOf(
        Earthquake(
            "1",
            "Kahramanmaraş",
            "Pazarcık",
            7.7,
            8.6,
            "06 Şub",
            "04:17",
            37.58,
            36.93,
            "https://picsum.photos/400/250?random=1",
            isSignificant = true,
            intensity = "XI"
        ),
        Earthquake(
            "2",
            "Hatay",
            "Defne",
            6.4,
            16.7,
            "20 Şub",
            "20:04",
            36.12,
            36.01,
            "https://picsum.photos/400/250?random=2",
            isSignificant = true,
            intensity = "VIII"
        ),
        Earthquake(
            "3",
            "İzmir",
            "Seferihisar",
            6.6,
            11.8,
            "30 Eki",
            "14:51",
            37.89,
            26.89,
            "https://picsum.photos/400/250?random=3",
            isSignificant = true,
            intensity = "VIII"
        ),
        Earthquake(
            "4",
            "Elazığ",
            "Sivrice",
            4.8,
            10.0,
            "24 Oca",
            "20:55",
            38.45,
            39.08,
            "https://picsum.photos/400/250?random=4",
            isSignificant = false,
            intensity = "V"
        ),
        Earthquake(
            "5",
            "Muğla",
            "Bodrum",
            3.2,
            5.0,
            "12 Ağu",
            "11:20",
            37.03,
            27.43,
            "https://picsum.photos/400/250?random=5",
            isSignificant = false,
            intensity = "III"
        )
    )

    val mockStatistics = EarthquakeStatistics(
        totalToday = 18,
        totalWeek = 142,
        totalMonth = 580,
        avgMagnitude = 2.4,
        maxMagnitude = 7.7,
        mostActiveRegion = "Ege / Marmara",
        magnitudeDistribution = mapOf(
            "1-2" to 12,
            "2-3" to 8,
            "3-4" to 5,
            "4+" to 2
        )
    )

    val infoCards = listOf(
        InfoCardItem(
            1,
            "Deprem Çantası Hazırlığı",
            "Hayatta kalma çantanızda bulunması gereken temel malzemeler.",
            "Hazırlık",
            "https://picsum.photos/600/400?random=10"
        ),
        InfoCardItem(
            2,
            "Çök - Kapan - Tutun",
            "Deprem anında doğru pozisyon hayat kurtarır.",
            "Güvenlik",
            "https://picsum.photos/600/400?random=11"
        ),
        InfoCardItem(
            3,
            "Bina Sağlamlığı Testi",
            "Oturduğunuz binanın risk analizini nasıl yaptırırsınız?",
            "Bilinç",
            "https://picsum.photos/600/400?random=12"
        )
    )
}