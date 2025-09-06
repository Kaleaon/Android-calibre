package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents a single media file in a library.
 *
 * This data class is a Room entity that corresponds to the `media_items` table.
 * It is the central table linking a file path to a library and its associated metadata.
 *
 * @property itemId The unique identifier for the media item.
 * @property libraryId The ID of the library this item belongs to.
 * @property filePath The full, unique path to the media file on the device.
 * @property dateAdded Timestamp of when the item was first added to the library.
 * @property lastScanned Timestamp of the last time the file was scanned for changes.
 * @property fileHash A hash of the file content to detect duplicates or changes.
 */
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
