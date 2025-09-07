package com.universalmedialibrary.data

enum class MediaType {
    BOOK,
    EBOOK,
    AUDIOBOOK,
    MOVIE,
    TV_SHOW,
    DOCUMENTARY,
    MUSIC_TRACK,
    MUSIC_ALBUM,
    PODCAST_EPISODE,
    PODCAST_SERIES,
    COMIC,
    MANGA,
    MAGAZINE,
    NEWSPAPER,
    JOURNAL,
    NEWS_ARTICLE,
    ACADEMIC_PAPER,
    REPORT,
    PRESENTATION,
    SPREADSHEET,
    IMAGE,
    PHOTO_ALBUM,
    VIDEO_CLIP,
    ANIMATION,
    GAME,
    SOFTWARE,
    ARCHIVE,
    DOCUMENT,
    NOTE,
    RECIPE,
    MANUAL,
    TUTORIAL
}

data class MediaItem(
    val id: Long,
    val title: String,
    val filePath: String,
    val mediaType: MediaType,
    val dateAdded: Long,
    val dateModified: Long
)
