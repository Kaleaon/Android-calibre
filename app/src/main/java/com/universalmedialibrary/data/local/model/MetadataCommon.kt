package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents metadata fields that are common across all media types.
 *
 * This data class is a Room entity that corresponds to the `metadata_common` table.
 *
 * @property itemId The ID of the media item this metadata belongs to (Primary Key and Foreign Key).
 * @property title The primary title of the media item.
 * @property sortTitle A version of the title used for sorting (e.g., "Avengers, The").
 * @property year The primary release year of the media.
 * @property releaseDate The full release date as a timestamp.
 * @property rating The user-defined rating (e.g., 1.0 to 5.0).
 * @property summary A plot summary or description.
 * @property coverImagePath The file path to a custom cover image.
 */
@Entity(
    tableName = "metadata_common",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetadataCommon(
    @PrimaryKey
    val itemId: Long,
    val title: String,
    val sortTitle: String?,
    val year: Int?,
    val releaseDate: Long?,
    val rating: Float?,
    val summary: String?,
    val coverImagePath: String?
)
