package com.universalmedialibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.universalmedialibrary.data.local.model.Genre
import com.universalmedialibrary.data.local.model.ItemGenre
import com.universalmedialibrary.data.local.model.ItemPersonRole
import com.universalmedialibrary.data.local.model.MetadataBook
import com.universalmedialibrary.data.local.model.MetadataCommon
import com.universalmedialibrary.data.local.model.MetadataMovie
import com.universalmedialibrary.data.local.model.People

main

@Dao
interface MetadataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataCommon(metadataCommon: MetadataCommon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataBook(metadataBook: MetadataBook)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadataMovie(metadataMovie: MetadataMovie)

    @Query("SELECT genreId FROM genres WHERE name = :name")
    suspend fun findGenreByName(name: String): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGenre(genre: Genre): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemGenre(itemGenre: ItemGenre)

    @Query("SELECT personId FROM people WHERE name = :name")
    suspend fun findPersonByName(name: String): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPerson(people: People): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemPersonRole(itemPersonRole: ItemPersonRole)

    @Query("SELECT seriesId FROM series WHERE name = :name")
    suspend fun findSeriesByName(name: String): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeries(series: Series): Long

    @Query("UPDATE metadata_book SET seriesId = :seriesId WHERE itemId = :itemId")
    suspend fun updateBookWithSeries(itemId: Long, seriesId: Long)
 main
}
