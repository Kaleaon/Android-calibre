package com.universalmedialibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.universalmedialibrary.data.local.model.*

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

    @Query("SELECT * FROM metadata_common WHERE itemId = :itemId")
    suspend fun getMetadataCommonByItemId(itemId: Long): MetadataCommon?

    @Query("SELECT * FROM metadata_book WHERE itemId = :itemId")
    suspend fun getMetadataBookByItemId(itemId: Long): MetadataBook?

    @Query("SELECT * FROM metadata_movie WHERE itemId = :itemId")
    suspend fun getMetadataMovieByItemId(itemId: Long): MetadataMovie?
}
