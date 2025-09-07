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

    // Methods for loading metadata relationships
    @Query("""
        SELECT p.name FROM people p 
        INNER JOIN item_person_role ipr ON p.personId = ipr.personId 
        WHERE ipr.itemId = :itemId AND ipr.role = 'AUTHOR'
    """)
    suspend fun getAuthorsByItemId(itemId: Long): List<String>

    @Query("""
        SELECT s.name FROM series s 
        INNER JOIN metadata_book mb ON s.seriesId = mb.seriesId 
        WHERE mb.itemId = :itemId
    """)
    suspend fun getSeriesByItemId(itemId: Long): String?

    @Query("""
        SELECT g.name FROM genres g 
        INNER JOIN item_genre ig ON g.genreId = ig.genreId 
        WHERE ig.itemId = :itemId
    """)
    suspend fun getGenresByItemId(itemId: Long): List<String>

    // Methods for updating metadata
    @Query("UPDATE metadata_common SET title = :title, sortTitle = :sortTitle, summary = :summary, rating = :rating WHERE itemId = :itemId")
    suspend fun updateMetadataCommon(itemId: Long, title: String, sortTitle: String?, summary: String?, rating: Float?)

    @Query("UPDATE metadata_book SET subtitle = :subtitle, publisher = :publisher, isbn = :isbn WHERE itemId = :itemId")
    suspend fun updateMetadataBook(itemId: Long, subtitle: String?, publisher: String?, isbn: String?)

    // Delete existing relationships before updating
    @Query("DELETE FROM item_person_role WHERE itemId = :itemId AND role = 'AUTHOR'")
    suspend fun deleteAuthorsByItemId(itemId: Long)

    @Query("DELETE FROM item_genre WHERE itemId = :itemId")
    suspend fun deleteGenresByItemId(itemId: Long)
}
