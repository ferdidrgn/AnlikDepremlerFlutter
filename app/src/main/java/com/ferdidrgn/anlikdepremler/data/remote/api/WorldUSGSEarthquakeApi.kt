package com.ferdidrgn.anlikdepremler.data.remote.api

import com.ferdidrgn.anlikdepremler.data.remote.dto.WorldUSGSEarthquakeDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WorldUSGSEarthquakeApi {
    @GET("earthquakes/feed/v1.0/summary/all_day.geojson")
    suspend fun getWorldUSGSEarthquakes(): Response<WorldUSGSEarthquakeDto>

    //Daha sonra yapılacak
    @GET("{api}")
    suspend fun getWorldUSGSDetailEarthquake(
        @Path("api") api: String
    ): Response<WorldUSGSEarthquakeDto>

}