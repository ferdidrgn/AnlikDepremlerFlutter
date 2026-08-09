package com.ferdidrgn.anlikdepremler.data.remote.api

import com.ferdidrgn.anlikdepremler.data.remote.dto.TurkeyAfadEarthquakeDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TurkeyAfadEarthquakeApi {
    @GET("apiv2/event/filter")
    suspend fun getTurkeyAfadEarthquakes(
        @Query("start") beforeFourDays: String,
        @Query("end") tomarow: String,
        @Query("orderby") orderby: String = "timedesc",
        @Query("minmag") minmag: Int = 2,
        @Query("limit") limit: Int = 100
    ): Response<ArrayList<TurkeyAfadEarthquakeDto>>
}