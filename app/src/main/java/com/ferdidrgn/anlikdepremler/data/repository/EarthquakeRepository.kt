package com.ferdidrgn.anlikdepremler.data.repository

import com.ferdi.deprem.model.Earthquake
import com.ferdidrgn.anlikdepremler.data.mapper.toDomain
import com.ferdidrgn.anlikdepremler.data.remote.EarthquakeSource
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyAfadEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyAllEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyKandilliEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.WorldUSGSEarthquakeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EarthquakeRepository(
    private val kandilliApi: TurkeyKandilliEarthquakeApi,
    private val afadApi: TurkeyAfadEarthquakeApi,
    private val turkeyAllApi: TurkeyAllEarthquakeApi,
    private val usgsApi: WorldUSGSEarthquakeApi
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
                        val response = afadApi.getTurkeyAfadEarthquakes(
                            beforeFourDays = "2026-08-01",
                            tomarow = "2026-08-09"
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
                }
                emit(list)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(emptyList())
            }
        }
}