package com.ferdidrgn.anlikdepremler.data.remote.api

import com.ferdidrgn.anlikdepremler.data.remote.dto.WorldGeneralEarthquakeDto
import retrofit2.Response
import retrofit2.http.GET

interface WorldGeneralEarthquakeApi {

    @GET("distquake_download_automatic21.php?pro/")
    suspend fun getWorldGeneralEarthquake(): Response<ArrayList<WorldGeneralEarthquakeDto>>
}