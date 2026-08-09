package com.ferdidrgn.anlikdepremler.di

import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyAfadEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyAllEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.TurkeyKandilliEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.remote.api.WorldUSGSEarthquakeApi
import com.ferdidrgn.anlikdepremler.data.repository.EarthquakeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideEarthquakeRepository(
        kandilliApi: TurkeyKandilliEarthquakeApi,
        afadApi: TurkeyAfadEarthquakeApi,
        turkeyAllApi: TurkeyAllEarthquakeApi,
        usgsApi: WorldUSGSEarthquakeApi
    ): EarthquakeRepository {
        return EarthquakeRepository(
            kandilliApi = kandilliApi,
            afadApi = afadApi,
            turkeyAllApi = turkeyAllApi,
            usgsApi = usgsApi
        )
    }
}