package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey

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
    ]
)
data class ItemGenre(
    val itemId: Long,
    val genreId: Long
)
