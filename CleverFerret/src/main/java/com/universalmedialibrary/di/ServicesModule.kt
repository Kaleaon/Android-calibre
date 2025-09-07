package com.universalmedialibrary.di

import android.content.Context
import com.universalmedialibrary.services.tts.CoquiTTSService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServicesModule {

    @Provides
    @Singleton
    fun provideCoquiTTSService(@ApplicationContext context: Context): CoquiTTSService {
        return CoquiTTSService(context)
    }
}