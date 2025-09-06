package com.universalmedialibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.universalmedialibrary.data.local.model.Genre
import com.universalmedialibrary.data.local.model.ItemGenre
import com.universalmedialibrary.data.local.model.ItemPersonRole
import com.universalmedialibrary.data.local.model.MetadataBook
import com.universalmedialibrary.data.local.model.MetadataCommon
import com.universalmedialibrary.data.local.model.MetadataMovie
import com.universalmedialibrary.data.local.model.People

/**
 * Data Access Object (DAO) for various metadata entities.
 *
 * This interface provides methods for inserting data into the various metadata
 * and relational tables in the database.
 */
@Dao
interface MetadataDao {

    /** Inserts or replaces common metadata for a media item. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataCommon(metadataCommon: MetadataCommon)

    /** Inserts or replaces book-specific metadata for a media item. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataBook(metadataBook: MetadataBook)

    /** Inserts or replaces movie-specific metadata for a media item. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataMovie(metadataMovie: MetadataMovie)

    /** Inserts or replaces a genre. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: Genre)

    /** Inserts or replaces the link between a media item and a genre. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemGenre(itemGenre: ItemGenre)

    /** Inserts or replaces a person (e.g., author, actor). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(people: People): Long

    /** Inserts or replaces the link between a media item, a person, and their role. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemPersonRole(itemPersonRole: ItemPersonRole)
}
