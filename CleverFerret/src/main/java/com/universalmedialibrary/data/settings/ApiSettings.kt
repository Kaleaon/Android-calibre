package com.universalmedialibrary.data.settings

import kotlinx.serialization.Serializable

/**
 * Data classes for API configuration settings
 */

@Serializable
data class ApiSettings(
    val bookApis: BookApiSettings = BookApiSettings(),
    val comicApis: ComicApiSettings = ComicApiSettings(),
    val audiobookApis: AudiobookApiSettings = AudiobookApiSettings(),
    val movieTvApis: MovieTvApiSettings = MovieTvApiSettings(),
    val musicApis: MusicApiSettings = MusicApiSettings()
)

@Serializable
data class BookApiSettings(
    val openLibraryEnabled: Boolean = true,
    val googleBooksEnabled: Boolean = true,
    val googleBooksApiKey: String = "",
    val hardcoverEnabled: Boolean = true,
    val priority: List<String> = listOf("Open Library", "Google Books", "Hardcover")
)

@Serializable
data class ComicApiSettings(
    val comicVineEnabled: Boolean = false,
    val comicVineApiKey: String = "",
    val priority: List<String> = listOf("ComicVine")
)

@Serializable
data class AudiobookApiSettings(
    val overDriveEnabled: Boolean = false,
    val overDriveApiKey: String = "",
    val priority: List<String> = listOf("OverDrive")
)

@Serializable
data class MovieTvApiSettings(
    val tmdbEnabled: Boolean = true,
    val tmdbApiKey: String = "",
    val omdbEnabled: Boolean = true,
    val omdbApiKey: String = "",
    val priority: List<String> = listOf("TMDB", "OMDb")
)

@Serializable
data class MusicApiSettings(
    val musicBrainzEnabled: Boolean = true,
    val spotifyEnabled: Boolean = false,
    val spotifyClientId: String = "",
    val spotifyClientSecret: String = "",
    val priority: List<String> = listOf("MusicBrainz", "Spotify")
)

enum class MediaType {
    BOOKS,
    COMICS,
    AUDIOBOOKS,
    MOVIES_TV,
    MUSIC
}

data class ApiProvider(
    val name: String,
    val description: String,
    val requiresApiKey: Boolean,
    val isEnabled: Boolean,
    val apiKey: String = "",
    val website: String = "",
    val mediaType: MediaType
)