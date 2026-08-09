package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    @Singleton
    fun provideKandilliApi(builder: Retrofit.Builder): TurkeyKandilliEarthquakeApi =
        builder.baseUrl("https://www.mertsenturk.net/").build()
            .create(TurkeyKandilliEarthquakeApi::class.java)

    @Provides
    @Singleton
    fun provideAfadApi(builder: Retrofit.Builder): TurkeyAfadEarthquakeApi =
        builder.baseUrl("https://deprem.afad.gov.tr/").build()
            .create(TurkeyAfadEarthquakeApi::class.java)

    @Provides
    @Singleton
    fun provideTurkeyAllApi(builder: Retrofit.Builder): TurkeyAllEarthquakeApi =
        builder.baseUrl("https://api.orhanaydogdu.com.tr/").build()
            .create(TurkeyAllEarthquakeApi::class.java)

    @Provides
    @Singleton
    fun provideUsgsApi(builder: Retrofit.Builder): WorldUSGSEarthquakeApi =
        builder.baseUrl("https://earthquake.usgs.gov/").build()
            .create(WorldUSGSEarthquakeApi::class.java)

    @Provides
    @Singleton
    fun provideWorldIGPEarthquakeApi(builder: Retrofit.Builder): WorldIGPEarthquakeApi =
        builder.baseUrl("https://cache.earthquakenetwork.it/").build()
            .create(WorldIGPEarthquakeApi::class.java)
}