package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Represents the many-to-many relationship between a [MediaItem] and a [Genre].
 *
 * This data class is a Room entity that corresponds to the `item_genre` join table.
 *
 * @property itemId The ID of the media item.
 * @property genreId The ID of the genre.
 */
@Entity(
    tableName = "item_genre",
    primaryKeys = ["itemId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Genre::class,
            parentColumns = ["genreId"],
            childColumns = ["genreId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["genreId"])]
)
data class ItemGenre(
    val itemId: Long,
    val genreId: Long
)
