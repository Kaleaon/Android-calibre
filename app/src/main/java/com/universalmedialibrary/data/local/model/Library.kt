package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user-defined media library.
 *
 * This data class is a Room entity that corresponds to the `libraries` table.
 * Each library is a top-level container for a specific type of media.
 *
 * @property libraryId The unique identifier for the library.
 * @property name The user-defined name for the library (e.g., "My Books").
 * @property type The type of media this library contains (e.g., 'BOOK', 'MOVIE').
 * @property path The absolute file system path to the library's root folder.
 */
@Entity(tableName = "libraries")
data class Library(
    @PrimaryKey(autoGenerate = true)
    val libraryId: Long = 0,
    val name: String,
    val type: String, // e.g., 'BOOK', 'MOVIE', 'MUSIC'
    val path: String
)
