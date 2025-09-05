package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_items",
    foreignKeys = [
        ForeignKey(
            entity = Library::class,
            parentColumns = ["libraryId"],
            childColumns = ["libraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["libraryId"])]
)
data class MediaItem(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0,
    val libraryId: Long,
    val filePath: String,
    val dateAdded: Long,
    val lastScanned: Long,
    val fileHash: String
)
