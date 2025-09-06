package com.universalmedialibrary.data.local.model

import androidx.room.Embedded

/**
 * A data class that represents the detailed view of a book.
 *
 * This class is used to hold the result of a JOIN query that combines data from
 * the `MediaItem`, `MetadataCommon`, and `People` tables. The `@Embedded` annotation
 * allows Room to map columns from the query result to the properties of the
 * nested objects.
 *
 * @property mediaItem The core [MediaItem] entity.
 * @property metadata The common metadata [MetadataCommon] associated with the media item.
 * @property authorName The name of the book's primary author.
 */
data class BookDetails(
    @Embedded(prefix = "media_") val mediaItem: MediaItem,
    @Embedded(prefix = "meta_") val metadata: MetadataCommon,
    val authorName: String?
)
