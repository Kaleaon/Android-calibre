package com.universalmedialibrary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.universalmedialibrary.data.local.dao.LibraryDao
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.*

@Database(
    entities = [
        Library::class,
        MediaItem::class,
        MetadataCommon::class,
        MetadataBook::class,
        MetadataMovie::class,
        MetadataTvShow::class,
        Album::class,
        MetadataMusicTrack::class,
        MetadataPodcast::class,
        MetadataMagazine::class,
        MetadataDocument::class,
        MetadataAcademicPaper::class,
        Genre::class,
        ItemGenre::class,
        People::class,
        ItemPersonRole::class,
        Series::class
    ],
    version = 3,
    exportSchema = false // For now, we can disable schema exporting
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun libraryDao(): LibraryDao
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun metadataDao(): MetadataDao

    companion object {
        const val DATABASE_NAME = "universal-media-library.db"
        
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to metadata_common table
                database.execSQL("ALTER TABLE metadata_common ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE metadata_common ADD COLUMN isDownloaded INTEGER NOT NULL DEFAULT 1")
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new metadata tables for additional media types
                database.execSQL("""
                    CREATE TABLE metadata_tv_show (
                        itemId INTEGER PRIMARY KEY,
                        seasonNumber INTEGER,
                        episodeNumber INTEGER,
                        seriesName TEXT,
                        network TEXT,
                        airDate TEXT,
                        episodeRuntime INTEGER,
                        tvRating TEXT,
                        tvdbId TEXT,
                        imdbId TEXT,
                        FOREIGN KEY (itemId) REFERENCES media_items(itemId) ON DELETE CASCADE
                    )
                """)
                
                database.execSQL("""
                    CREATE TABLE metadata_podcast (
                        itemId INTEGER PRIMARY KEY,
                        showName TEXT,
                        episodeNumber INTEGER,
                        seasonNumber INTEGER,
                        durationSeconds INTEGER,
                        publicationDate TEXT,
                        description TEXT,
                        transcriptPath TEXT,
                        rssUrl TEXT,
                        podcastGuid TEXT,
                        FOREIGN KEY (itemId) REFERENCES media_items(itemId) ON DELETE CASCADE
                    )
                """)
                
                database.execSQL("""
                    CREATE TABLE metadata_magazine (
                        itemId INTEGER PRIMARY KEY,
                        issueNumber TEXT,
                        volumeNumber TEXT,
                        publicationDate TEXT,
                        issn TEXT,
                        magazineTitle TEXT,
                        articleCount INTEGER,
                        coverStory TEXT,
                        editor TEXT,
                        FOREIGN KEY (itemId) REFERENCES media_items(itemId) ON DELETE CASCADE
                    )
                """)
                
                database.execSQL("""
                    CREATE TABLE metadata_document (
                        itemId INTEGER PRIMARY KEY,
                        documentType TEXT,
                        author TEXT,
                        creationDate TEXT,
                        modificationDate TEXT,
                        pageCount INTEGER,
                        wordCount INTEGER,
                        language TEXT,
                        format TEXT,
                        fileSize INTEGER,
                        FOREIGN KEY (itemId) REFERENCES media_items(itemId) ON DELETE CASCADE
                    )
                """)
                
                database.execSQL("""
                    CREATE TABLE metadata_academic_paper (
                        itemId INTEGER PRIMARY KEY,
                        journalName TEXT,
                        doi TEXT,
                        abstractText TEXT,
                        publicationYear INTEGER,
                        citationCount INTEGER,
                        keywords TEXT,
                        researchField TEXT,
                        peerReviewed INTEGER DEFAULT 0,
                        arxivId TEXT,
                        FOREIGN KEY (itemId) REFERENCES media_items(itemId) ON DELETE CASCADE
                    )
                """)
                
                // Update existing metadata_movie table with new fields
                database.execSQL("ALTER TABLE metadata_movie ADD COLUMN director TEXT")
                database.execSQL("ALTER TABLE metadata_movie ADD COLUMN rating TEXT")
                database.execSQL("ALTER TABLE metadata_movie ADD COLUMN releaseYear INTEGER")
                database.execSQL("ALTER TABLE metadata_movie ADD COLUMN productionCompany TEXT")
                database.execSQL("ALTER TABLE metadata_movie ADD COLUMN budget INTEGER")
                database.execSQL("ALTER TABLE metadata_movie ADD COLUMN boxOffice INTEGER")
                database.execSQL("ALTER TABLE metadata_movie ADD COLUMN imdbId TEXT")
                database.execSQL("ALTER TABLE metadata_movie ADD COLUMN tmdbId TEXT")
            }
        }
    }
}
