package com.universalmedialibrary.data

/**
 * Represents the book-specific metadata for a media item.
 *
 * This data class corresponds to the `Metadata_Book` table in the database schema.
 *
 * @property mediaItemId The unique identifier for the media item, linking it to the common metadata.
 * @property subtitle The subtitle of the book.
 * @property isbn The International Standard Book Number.
 * @property pageCount The total number of pages in the book.
 * @property publisher The name of the book's publisher.
 */
data class Book(
    val mediaItemId: Long,
    val subtitle: String?,
    val isbn: String?,
    val pageCount: Int?,
    val publisher: String?
)
