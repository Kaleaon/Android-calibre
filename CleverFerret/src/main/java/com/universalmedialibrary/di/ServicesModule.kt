package com.universalmedialibrary.di

import android.content.Context
import com.universalmedialibrary.services.tts.CoquiTTSService
import com.universalmedialibrary.services.metadata.MetadataApiService
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

    @Provides
    @Singleton
    fun provideMetadataApiService(): MetadataApiService {
        return MetadataApiService()
    }

    @Provides
    @Singleton
    fun provideWebFictionService(): com.universalmedialibrary.services.webfiction.WebFictionService {
        return com.universalmedialibrary.services.webfiction.WebFictionService()
    }

    @Provides
    @Singleton
    fun providePodcastService(@ApplicationContext context: Context): com.universalmedialibrary.services.podcast.PodcastService {
        return com.universalmedialibrary.services.podcast.PodcastService(context)
    }
}