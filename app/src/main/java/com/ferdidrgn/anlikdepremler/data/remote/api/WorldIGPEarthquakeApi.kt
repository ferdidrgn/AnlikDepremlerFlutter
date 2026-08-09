package com.ferdidrgn.anlikdepremler.data.remote.api

import com.ferdidrgn.anlikdepremler.data.remote.dto.WorldIGPEarthquakeDto
import retrofit2.Response
import retrofit2.http.GET

interface WorldIGPEarthquakeApi {

    @GET("distquake_download_automatic21.php?pro")
    suspend fun getWorldIGPEarthquakes(): Response<ArrayList<WorldIGPEarthquakeDto>>
}