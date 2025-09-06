package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents music-track-specific metadata for a media item.
 *
 * This data class is a Room entity that corresponds to the `metadata_music_track` table.
 * It stores details that are unique to music tracks.
 *
 * @property itemId The ID of the media item this metadata belongs to (Primary Key and Foreign Key).
 * @property albumId The ID of the album this track belongs to.
 * @property trackNumber The track number on the album.
 * @property discNumber The disc number for multi-disc albums.
 * @property duration The duration of the track in seconds.
 */
@Entity(
    tableName = "metadata_music_track",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Album::class,
            parentColumns = ["albumId"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.SET_NULL // If an album is deleted, we don't want to delete the track
        )
    ],
    indices = [androidx.room.Index(value = ["albumId"])]
)
data class MetadataMusicTrack(
    @PrimaryKey
    val itemId: Long,
    val albumId: Long?,
    val trackNumber: Int?,
    val discNumber: Int?,
    val duration: Int? // in seconds
)
