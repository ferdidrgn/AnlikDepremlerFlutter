package com.ferdidrgn.anlikdepremler.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // PreferencesManager Hilt tarafından otomatik enjekte edilir!
}