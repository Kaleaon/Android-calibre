package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libraries")
data class Library(
    @PrimaryKey(autoGenerate = true)
    val libraryId: Long = 0,
    val name: String,
    val type: String, // e.g., 'BOOK', 'MOVIE', 'MUSIC'
    val path: String,
)
