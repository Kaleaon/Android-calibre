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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).addMigrations(AppDatabase.MIGRATION_1_2)
         .build()
    }

    @Provides
    fun provideLibraryDao(appDatabase: AppDatabase): LibraryDao {
        return appDatabase.libraryDao()
    }

    @Provides
    fun provideMediaItemDao(appDatabase: AppDatabase): MediaItemDao {
        return appDatabase.mediaItemDao()
    }

    @Provides
    fun provideMetadataDao(appDatabase: AppDatabase): MetadataDao {
        return appDatabase.metadataDao()
    }
}
