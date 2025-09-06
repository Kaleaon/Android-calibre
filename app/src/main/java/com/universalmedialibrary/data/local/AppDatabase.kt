package com.universalmedialibrary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.universalmedialibrary.data.local.dao.LibraryDao
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.*

/**
 * The main Room database for the application.
 *
 * This class defines the database configuration and serves as the main access point
 * to the persisted data. It lists all the entities and provides abstract methods
 * to get the Data Access Objects (DAOs).
 *
 * @see RoomDatabase
 */
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
        Series::class
    ],
    version = 1,
    exportSchema = false // For now, we can disable schema exporting
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Returns the Data Access Object for [Library] entities.
     */
    abstract fun libraryDao(): LibraryDao
    /**
     * Returns the Data Access Object for [MediaItem] entities.
     */
    abstract fun mediaItemDao(): MediaItemDao
    /**
     * Returns the Data Access Object for metadata entities.
     */
    abstract fun metadataDao(): MetadataDao

    companion object {
        /**
         * The name of the database file.
         */
        const val DATABASE_NAME = "universal-media-library.db"
    }
}
