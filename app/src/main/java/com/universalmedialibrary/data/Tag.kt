package com.universalmedialibrary.data

/**
 * Represents a tag that can be applied to a media item.
 *
 * This data class corresponds to the `Tags` table in the database schema.
 *
 * @property id The unique identifier for the tag.
 * @property name The name of the tag (e.g., "Science Fiction", "Favorite").
 */
data class Tag(
    val id: Long,
    val name: String
)
