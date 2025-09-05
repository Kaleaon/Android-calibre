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

@Dao
interface MetadataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataCommon(metadataCommon: MetadataCommon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataBook(metadataBook: MetadataBook)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataMovie(metadataMovie: MetadataMovie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: Genre)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemGenre(itemGenre: ItemGenre)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(people: People)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemPersonRole(itemPersonRole: ItemPersonRole)
}
