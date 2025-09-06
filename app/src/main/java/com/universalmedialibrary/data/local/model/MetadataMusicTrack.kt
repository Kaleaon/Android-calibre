package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_music_track",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Album::class,
            parentColumns = ["albumId"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.SET_NULL, // If an album is deleted, we don't want to delete the track
        ),
    ],
    indices = [androidx.room.Index(value = ["albumId"])],
)
data class MetadataMusicTrack(
    @PrimaryKey
    val itemId: Long,
    val albumId: Long?,
    val trackNumber: Int?,
    val discNumber: Int?,
    val duration: Int?, // in seconds
)
