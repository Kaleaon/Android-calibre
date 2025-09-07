package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_tv_show",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetadataTvShow(
    @PrimaryKey
    val itemId: Long,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val seriesName: String? = null,
    val network: String? = null,
    val airDate: String? = null,
    val episodeRuntime: Int? = null,
    val tvRating: String? = null,
    val tvdbId: String? = null,
    val imdbId: String? = null
)