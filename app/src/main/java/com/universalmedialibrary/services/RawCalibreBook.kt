package com.universalmedialibrary.services

/**
 * A data class to temporarily hold raw book information read from a Calibre database.
 *
 * This class is not a Room entity and is only used during the import process.
 *
 * @property id The original book ID from the Calibre database.
 * @property title The raw title of the book.
 * @property path The relative path to the book's folder from the Calibre database.
 * @property authorName The raw name of the author.
 */
data class RawCalibreBook(
    val id: Long,
    val title: String,
    val path: String,
    val authorName: String?
)
