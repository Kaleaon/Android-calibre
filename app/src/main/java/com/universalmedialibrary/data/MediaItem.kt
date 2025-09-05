package com.universalmedialibrary.data

enum class MediaType {
    BOOK,
    MOVIE,
    MUSIC_TRACK,
    PODCAST_EPISODE,
    COMIC
}

data class MediaItem(
    val id: Long,
    val title: String,
    val filePath: String,
    val mediaType: MediaType,
    val dateAdded: Long,
    val dateModified: Long
)
