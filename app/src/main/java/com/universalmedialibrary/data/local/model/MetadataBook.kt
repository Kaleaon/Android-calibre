package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_book",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE,
        ),
        // We will add a foreign key to a 'Series' table later
    ],
)
data class MetadataBook(
    @PrimaryKey
    val itemId: Long,
    val subtitle: String?,
    val publisher: String?,
    val isbn: String?,
    val pageCount: Int?,
    val seriesId: Long?,
)
