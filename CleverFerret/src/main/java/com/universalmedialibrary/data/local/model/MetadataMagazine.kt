package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_magazine",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetadataMagazine(
    @PrimaryKey
    val itemId: Long,
    val issueNumber: String? = null,
    val volumeNumber: String? = null,
    val publicationDate: String? = null,
    val issn: String? = null,
    val magazineTitle: String? = null,
    val articleCount: Int? = null,
    val coverStory: String? = null,
    val editor: String? = null
)