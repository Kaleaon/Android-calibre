package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a music album in the database.
 *
 * This data class is a Room entity that corresponds to the `albums` table.
 *
 * @property albumId The unique identifier for the album.
 * @property title The title of the album.
 * @property albumArtist The primary artist for the album.
 * @property releaseYear The year the album was released.
 * @property albumArtPath The file path to the album's cover art.
 */
@Entity(tableName = "albums")
data class Album(
    @PrimaryKey(autoGenerate = true)
    val albumId: Long = 0,
    val title: String,
    val albumArtist: String?, // For simplicity, we'll use a string for now. This can be a foreign key to a 'People' table later.
    val releaseYear: Int?,
    val albumArtPath: String?
)
