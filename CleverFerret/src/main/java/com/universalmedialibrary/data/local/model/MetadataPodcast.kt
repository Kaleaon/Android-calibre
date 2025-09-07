package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_podcast",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetadataPodcast(
    @PrimaryKey
    val itemId: Long,
    val showName: String? = null,
    val episodeNumber: Int? = null,
    val seasonNumber: Int? = null,
    val durationSeconds: Int? = null,
    val publicationDate: String? = null,
    val description: String? = null,
    val transcriptPath: String? = null,
    val rssUrl: String? = null,
    val podcastGuid: String? = null
)