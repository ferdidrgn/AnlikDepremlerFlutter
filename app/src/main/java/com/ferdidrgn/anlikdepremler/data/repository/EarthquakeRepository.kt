package com.ferdidrgn.anlikdepremler.data.repository

import com.ferdi.deprem.model.Earthquake
import com.ferdidrgn.anlikdepremler.data.mapper.toDomain
import com.ferdidrgn.anlikdepremler.data.remote.EarthquakeSource
import com.ferdidrgn.anlikdepremler.data.remote.api.EmscEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyAfadEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyAllEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyKandilliEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.WorldIGPEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.WorldUSGSEarthquakeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EarthquakeRepository @Inject constructor(
    private val kandilliApi: TurkeyKandilliEarthquakeApi,
    private val afadApi: TurkeyAfadEarthquakeApi,
    private val turkeyAllApi: TurkeyAllEarthquakeApi,
    private val usgsApi: WorldUSGSEarthquakeApi,
    private val worldIgpApi: WorldIGPEarthquakeApi,
    private val emscApi: EmscEarthquakeApi
) {

    fun getEarthquakes(source: EarthquakeSource = EarthquakeSource.KANDILLI): Flow<List<Earthquake>> =
        flow {
            try {
                val list = when (source) {
                    EarthquakeSource.KANDILLI -> {
                        val response = kandilliApi.getTurkeyKandilliEarthquakes()
                        response.body()?.map { it.toDomain() } ?: emptyList()
                    }

                    EarthquakeSource.AFAD -> {
                        val today =
                            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                .format(java.util.Date())
                        val fourDaysAgo =
                            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                .format(java.util.Date(System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000L))

                        val response = afadApi.getTurkeyAfadEarthquakes(
                            beforeFourDays = fourDaysAgo,
                            tomarow = today
                        )

                        response.body()?.map { it.toDomain() } ?: emptyList()
                    }

                    EarthquakeSource.TURKEY_ALL -> {
                        val response = turkeyAllApi.getTurkeyAll()
                        response.body()?.result?.map { it.toDomain() } ?: emptyList()
                    }

                    EarthquakeSource.USGS -> {
                        val response = usgsApi.getWorldUSGSEarthquakes()
                        response.body()?.features?.map { it.toDomain() } ?: emptyList()
                    }

                    EarthquakeSource.WORLD_IGP -> {
                        val response = worldIgpApi.getWorldIGPEarthquakes()
                        response.body()?.map { it.toDomain() } ?: emptyList()
                    }

                    EarthquakeSource.EMSC -> {
                        val response = emscApi.getEmscEarthquakes()
                        response.body()?.features?.map { it.toDomain() } ?: emptyList()
                    }
                }
                emit(list)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(emptyList())
            }
        }
}