package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_movie",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetadataMovie(
    @PrimaryKey
    val itemId: Long,
    val tagline: String?,
    val runtime: Int? // in minutes
)
