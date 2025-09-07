package com.universalmedialibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.universalmedialibrary.data.local.model.Library
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibrary(library: Library)

    @Query("SELECT * FROM libraries ORDER BY name ASC")
    fun getAllLibraries(): Flow<List<Library>>
}
