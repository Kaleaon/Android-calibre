package com.universalmedialibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.universalmedialibrary.data.local.model.Library
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [Library] entities.
 *
 * This interface provides methods for interacting with the `libraries` table in the database.
 */
@Dao
interface LibraryDao {

    /**
     * Inserts a new library into the database or replaces it if it already exists.
     *
     * @param library The [Library] object to insert or replace.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibrary(library: Library)

    /**
     * Retrieves all libraries from the database, ordered alphabetically by name.
     *
     * @return A [Flow] that emits a list of all [Library] objects.
     */
    @Query("SELECT * FROM libraries ORDER BY name ASC")
    fun getAllLibraries(): Flow<List<Library>>
}
