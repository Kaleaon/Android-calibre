package com.universalmedialibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.universalmedialibrary.data.local.model.BookDetails
import com.universalmedialibrary.data.local.model.MediaItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [MediaItem] entities.
 *
 * This interface provides methods for interacting with the `media_items` table
 * and related metadata tables in the database.
 */
@Dao
interface MediaItemDao {

    /**
     * Inserts a new media item into the database or replaces it if it already exists.
     *
     * @param mediaItem The [MediaItem] object to insert or replace.
     * @return The row ID of the newly inserted item.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItem): Long

    /**
     * Retrieves all media items belonging to a specific library, ordered by the date they were added.
     *
     * @param libraryId The ID of the library.
     * @return A [Flow] that emits a list of [MediaItem] objects.
     */
    @Query("SELECT * FROM media_items WHERE libraryId = :libraryId ORDER BY dateAdded DESC")
    fun getMediaItemsForLibrary(libraryId: Long): Flow<List<MediaItem>>

    /**
     * Retrieves a single media item by its unique ID.
     *
     * @param itemId The ID of the media item to retrieve.
     * @return The [MediaItem] object if found, otherwise null.
     */
    @Query("SELECT * FROM media_items WHERE itemId = :itemId")
    suspend fun getMediaItemById(itemId: Long): MediaItem?

    /**
     * Retrieves a list of books with their detailed metadata for a specific library.
     *
     * This query joins the `media_items`, `metadata_common`, and `people` tables
     * to create a detailed view of each book.
     *
     * @param libraryId The ID of the library.
     * @return A [Flow] that emits a list of [BookDetails] objects.
     */
    @Query("""
        SELECT
            mi.itemId as media_itemId,
            mi.libraryId as media_libraryId,
            mi.filePath as media_filePath,
            mi.dateAdded as media_dateAdded,
            mi.lastScanned as media_lastScanned,
            mi.fileHash as media_fileHash,
            mc.itemId as meta_itemId,
            mc.title as meta_title,
            mc.sortTitle as meta_sortTitle,
            mc.year as meta_year,
            mc.releaseDate as meta_releaseDate,
            mc.rating as meta_rating,
            mc.summary as meta_summary,
            mc.coverImagePath as meta_coverImagePath,
            p.name as authorName
        FROM media_items mi
        JOIN metadata_common mc ON mi.itemId = mc.itemId
        LEFT JOIN item_person_role ipr ON mi.itemId = ipr.itemId AND ipr.role = 'AUTHOR'
        LEFT JOIN people p ON ipr.personId = p.personId
        WHERE mi.libraryId = :libraryId
    """)
    fun getBookDetailsForLibrary(libraryId: Long): Flow<List<BookDetails>>
}
