package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_movie",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetadataMovie(
    @PrimaryKey
    val itemId: Long,
    val tagline: String? = null,
    val runtime: Int? = null, // in minutes
    val director: String? = null,
    val rating: String? = null,
    val releaseYear: Int? = null,
    val productionCompany: String? = null,
    val budget: Long? = null,
    val boxOffice: Long? = null,
    val imdbId: String? = null,
    val tmdbId: String? = null
)
