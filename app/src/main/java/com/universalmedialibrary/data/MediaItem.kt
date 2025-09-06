package com.universalmedialibrary.data

/**
 * Defines the different types of media that can exist in the library.
 */
enum class MediaType {
    /** A book, typically in e-book format like EPUB. */
    BOOK,
    /** A feature film or short film. */
    MOVIE,
    /** A single music track. */
    MUSIC_TRACK,
    /** A single episode of a podcast. */
    PODCAST_EPISODE,
    /** A comic book or graphic novel. */
    COMIC
}

/**
 * Represents a single item in the media library.
 *
 * This is the central data class that corresponds to the `MediaItems` table in the database schema.
 * It holds the common information for all types of media.
 *
 * @property id The unique identifier for the media item.
 * @property title The primary title of the media item.
 * @property filePath The absolute path to the media file on the device.
 * @property mediaType The type of media, as defined by the [MediaType] enum.
 * @property dateAdded The timestamp (in milliseconds) when the item was first added to the library.
 * @property dateModified The timestamp (in milliseconds) of when the file was last modified.
 */
data class MediaItem(
    val id: Long,
    val title: String,
    val filePath: String,
    val mediaType: MediaType,
    val dateAdded: Long,
    val dateModified: Long
)
