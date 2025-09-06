package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_common",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MetadataCommon(
    @PrimaryKey
    val itemId: Long,
    val title: String,
    val sortTitle: String?,
    val year: Int?,
    val releaseDate: Long?,
    val rating: Float?,
    val summary: String?,
    val coverImagePath: String?,
)
