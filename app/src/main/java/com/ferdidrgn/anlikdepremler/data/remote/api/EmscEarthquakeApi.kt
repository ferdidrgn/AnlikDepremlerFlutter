package com.ferdidrgn.anlikdepremler.data.remote.api

import com.ferdidrgn.anlikdepremler.data.remote.dto.EmscEarthquakeDto
import retrofit2.Response
import retrofit2.http.GET

interface EmscEarthquakeApi {

    @GET("fdsnws/event/1/query?limit=100&format=json")
    suspend fun getEmscEarthquakes(): Response<EmscEarthquakeDto>
}