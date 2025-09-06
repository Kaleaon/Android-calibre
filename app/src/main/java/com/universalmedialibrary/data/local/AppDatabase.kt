package com.universalmedialibrary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.universalmedialibrary.data.local.dao.LibraryDao
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.Album
import com.universalmedialibrary.data.local.model.Genre
import com.universalmedialibrary.data.local.model.ItemGenre
import com.universalmedialibrary.data.local.model.ItemPersonRole
import com.universalmedialibrary.data.local.model.Library
import com.universalmedialibrary.data.local.model.MediaItem
import com.universalmedialibrary.data.local.model.MetadataBook
import com.universalmedialibrary.data.local.model.MetadataCommon
import com.universalmedialibrary.data.local.model.MetadataMovie
import com.universalmedialibrary.data.local.model.MetadataMusicTrack
import com.universalmedialibrary.data.local.model.People
import com.universalmedialibrary.data.local.model.Series

@Database(
    entities = [
        Library::class,
        MediaItem::class,
        MetadataCommon::class,
        MetadataBook::class,
        MetadataMovie::class,
        Album::class,
        MetadataMusicTrack::class,
        Genre::class,
        ItemGenre::class,
        People::class,
        ItemPersonRole::class,
        Series::class,
    ],
    version = 1,
    exportSchema = false, // For now, we can disable schema exporting
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao

    abstract fun mediaItemDao(): MediaItemDao

    abstract fun metadataDao(): MetadataDao

    companion object {
        const val DATABASE_NAME = "universal-media-library.db"
    }
}
