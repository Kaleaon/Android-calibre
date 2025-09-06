package com.universalmedialibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.universalmedialibrary.data.local.model.BookDetails
import com.universalmedialibrary.data.local.model.MediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItem): Long

    @Query("SELECT * FROM media_items WHERE libraryId = :libraryId ORDER BY dateAdded DESC")
    fun getMediaItemsForLibrary(libraryId: Long): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE itemId = :itemId")
    suspend fun getMediaItemById(itemId: Long): MediaItem?

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

    @Query("DELETE FROM media_items WHERE itemId = :itemId")
    suspend fun deleteMediaItemById(itemId: Long)

    // The following manual metadata deletion methods are no longer needed,
    // as Room will handle cascading deletes via foreign key constraints.

    @androidx.room.Transaction
    suspend fun deleteMediaItem(itemId: Long) {
        // Simply delete the media item; related metadata will be deleted automatically.
        deleteMediaItemById(itemId)
    }
}
