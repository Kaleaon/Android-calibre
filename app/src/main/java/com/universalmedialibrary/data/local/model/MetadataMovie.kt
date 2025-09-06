package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents movie-specific metadata for a media item.
 *
 * This data class is a Room entity that corresponds to the `metadata_movie` table.
 * It stores details that are unique to movies.
 *
 * @property itemId The ID of the media item this metadata belongs to (Primary Key and Foreign Key).
 * @property tagline The movie's tagline.
 * @property runtime The runtime of the movie in minutes.
 */
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
    val tagline: String?,
    val runtime: Int? // in minutes
)
