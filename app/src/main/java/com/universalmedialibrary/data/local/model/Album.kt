package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey(autoGenerate = true)
    val albumId: Long = 0,
    val title: String,
    val albumArtist: String?, // For simplicity, we'll use a string for now. This can be a foreign key to a 'People' table later.
    val releaseYear: Int?,
    val albumArtPath: String?,
)
