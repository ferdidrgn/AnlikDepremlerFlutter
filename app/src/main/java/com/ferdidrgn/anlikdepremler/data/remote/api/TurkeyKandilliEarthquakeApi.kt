package com.ferdidrgn.anlikdepremler.data.remote.api

import com.ferdidrgn.anlikdepremler.data.remote.dto.TurkeyKandilliEarthquakeDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TurkeyKandilliEarthquakeApi {

    @GET("deprem/api/limit/800")
    suspend fun getTurkeyKandilliEarthquakes(): Response<ArrayList<TurkeyKandilliEarthquakeDto>>

    @GET("deprem/api/limit/10")
    suspend fun getTopTenTurkeyKandilliEarthquakeList(): Response<ArrayList<TurkeyKandilliEarthquakeDto>>

    @GET("deprem/api/location/{city}")
    suspend fun getLocationTurkeyKandilliEarthquakeList(
        @Path("city") city: String,
    ): Response<ArrayList<TurkeyKandilliEarthquakeDto>>

    @GET("deprem/api/location/{city}/limit/{limit}")
    suspend fun getTopTenLocationTurkeyKandilliEarthquakeList(
        @Path("city") city: String,
        @Path("limit") limit: Int
    ): Response<ArrayList<TurkeyKandilliEarthquakeDto>>

    @GET("deprem/api/date/{date}")
    suspend fun getOnlyDateTurkeyKandilliEarthquakeList(
        @Path("date") date: String
    ): Response<ArrayList<TurkeyKandilliEarthquakeDto>>

    @GET("deprem/api/between/{startDate}/{endDate}")
    suspend fun getDateBetweenTurkeyKandilliEarthquakeList(
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String
    ): Response<ArrayList<TurkeyKandilliEarthquakeDto>>
}