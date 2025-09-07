package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_document",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetadataDocument(
    @PrimaryKey
    val itemId: Long,
    val documentType: String? = null,
    val author: String? = null,
    val creationDate: String? = null,
    val modificationDate: String? = null,
    val pageCount: Int? = null,
    val wordCount: Int? = null,
    val language: String? = null,
    val format: String? = null,
    val fileSize: Long? = null
)