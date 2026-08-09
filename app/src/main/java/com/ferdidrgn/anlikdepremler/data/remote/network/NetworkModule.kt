package com.ferdidrgn.anlikdepremler.data.remote.network

import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyAfadEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyAllEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyKandilliEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.WorldUSGSEarthquakeApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val kandilliApi: TurkeyKandilliEarthquakeApi by lazy {
        createRetrofit("https://www.mertsenturk.net/").create(TurkeyKandilliEarthquakeApi::class.java)
    }

    val afadApi: TurkeyAfadEarthquakeApi by lazy {
        createRetrofit("https://deprem.afad.gov.tr/").create(TurkeyAfadEarthquakeApi::class.java)
    }

    val turkeyAllApi: TurkeyAllEarthquakeApi by lazy {
        createRetrofit("https://api.orhanaydogdu.com.tr/").create(TurkeyAllEarthquakeApi::class.java)
    }

    val usgsApi: WorldUSGSEarthquakeApi by lazy {
        createRetrofit("https://earthquake.usgs.gov/").create(WorldUSGSEarthquakeApi::class.java)
    }
}