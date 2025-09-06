package com.universalmedialibrary.di

import android.content.Context
import androidx.room.Room
import com.universalmedialibrary.data.local.AppDatabase
import com.universalmedialibrary.data.local.dao.LibraryDao
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies.
 *
 * This object provides singleton instances of the [AppDatabase] and its DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton instance of the [AppDatabase].
     *
     * @param context The application context.
     * @return A singleton [AppDatabase] instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    /**
     * Provides an instance of [LibraryDao].
     *
     * @param appDatabase The [AppDatabase] instance.
     * @return An instance of [LibraryDao].
     */
    @Provides
    fun provideLibraryDao(appDatabase: AppDatabase): LibraryDao {
        return appDatabase.libraryDao()
    }

    /**
     * Provides an instance of [MediaItemDao].
     *
     * @param appDatabase The [AppDatabase] instance.
     * @return An instance of [MediaItemDao].
     */
    @Provides
    fun provideMediaItemDao(appDatabase: AppDatabase): MediaItemDao {
        return appDatabase.mediaItemDao()
    }

    /**
     * Provides an instance of [MetadataDao].
     *
     * @param appDatabase The [AppDatabase] instance.
     * @return An instance of [MetadataDao].
     */
    @Provides
    fun provideMetadataDao(appDatabase: AppDatabase): MetadataDao {
        return appDatabase.metadataDao()
    }
}
