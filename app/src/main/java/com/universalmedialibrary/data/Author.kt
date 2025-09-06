package com.universalmedialibrary.data

/**
 * Represents an author or creator of a media item.
 *
 * This data class corresponds to the `People` table in the database schema.
 *
 * @property id The unique identifier for the author.
 * @property name The full name of the author.
 * @property sortName The name formatted for sorting, typically "LastName, FirstName".
 */
data class Author(
    val id: Long,
    val name: String,
    val sortName: String?
)
