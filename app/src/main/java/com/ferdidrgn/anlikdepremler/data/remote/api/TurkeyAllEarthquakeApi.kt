package com.ferdidrgn.anlikdepremler.data.remote.api

import com.ferdidrgn.anlikdepremler.data.remote.dto.TurkeyAllEarthquakeDto
import retrofit2.http.GET
import retrofit2.Response

interface TurkeyAllEarthquakeApi {
    @GET("deprem/")
    suspend fun getTurkeyAll(): Response<TurkeyAllEarthquakeDto>
}