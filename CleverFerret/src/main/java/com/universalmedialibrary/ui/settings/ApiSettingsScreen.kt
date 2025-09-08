package com.universalmedialibrary.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.universalmedialibrary.data.settings.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiSettingsScreen(
    navController: NavController,
    mediaType: String,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val apiSettings by viewModel.apiSettings.collectAsState()
    
    val (title, description, providers) = when (mediaType) {
        "books" -> Triple(
            "Books APIs",
            "Configure metadata sources for books and ebooks",
            getBooksProviders(apiSettings.bookApis)
        )
        "comics" -> Triple(
            "Comics APIs", 
            "Configure metadata sources for comics and graphic novels",
            getComicsProviders(apiSettings.comicApis)
        )
        "audiobooks" -> Triple(
            "Audiobooks APIs",
            "Configure metadata sources for audiobooks",
            getAudiobooksProviders(apiSettings.audiobookApis)
        )
        "movies" -> Triple(
            "Movies & TV APIs",
            "Configure metadata sources for movies and TV shows", 
            getMoviesProviders(apiSettings.movieTvApis)
        )
        "music" -> Triple(
            "Music APIs",
            "Configure metadata sources for music and albums",
            getMusicProviders(apiSettings.musicApis)
        )
        else -> Triple("Unknown", "", emptyList())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            providers.forEach { provider ->
                item {
                    ApiProviderCard(
                        provider = provider,
                        onToggle = { enabled ->
                            updateProviderSettings(viewModel, mediaType, provider.name, enabled, provider.apiKey)
                        },
                        onApiKeyChange = { apiKey ->
                            updateProviderSettings(viewModel, mediaType, provider.name, provider.isEnabled, apiKey)
                        }
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "API Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "APIs are checked in priority order. Free APIs are used first when available. API keys are stored securely on your device.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApiProviderCard(
    provider: ApiProvider,
    onToggle: (Boolean) -> Unit,
    onApiKeyChange: (String) -> Unit
) {
    var showApiKey by remember { mutableStateOf(false) }
    var apiKeyText by remember { mutableStateOf(provider.apiKey) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = provider.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = provider.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Switch(
                    checked = provider.isEnabled,
                    onCheckedChange = onToggle
                )
            }
            
            if (provider.requiresApiKey && provider.isEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = apiKeyText,
                    onValueChange = { 
                        apiKeyText = it
                        onApiKeyChange(it)
                    },
                    label = { Text("API Key") },
                    placeholder = { Text("Enter your ${provider.name} API key") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showApiKey) "Hide API key" else "Show API key"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                
                if (provider.website.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { /* Open website */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Get API Key")
                    }
                }
            }
        }
    }
}

private fun updateProviderSettings(
    viewModel: SettingsViewModel,
    mediaType: String,
    providerName: String,
    enabled: Boolean,
    apiKey: String
) {
    when (mediaType) {
        "books" -> {
            val current = viewModel.apiSettings.value.bookApis
            val updated = when (providerName) {
                "Open Library" -> current.copy(openLibraryEnabled = enabled)
                "Google Books" -> current.copy(googleBooksEnabled = enabled, googleBooksApiKey = apiKey)
                "Hardcover" -> current.copy(hardcoverEnabled = enabled)
                else -> current
            }
            viewModel.updateBookApiSettings(updated)
        }
        "comics" -> {
            val current = viewModel.apiSettings.value.comicApis
            val updated = current.copy(comicVineEnabled = enabled, comicVineApiKey = apiKey)
            viewModel.updateComicApiSettings(updated)
        }
        "audiobooks" -> {
            val current = viewModel.apiSettings.value.audiobookApis
            val updated = current.copy(overDriveEnabled = enabled, overDriveApiKey = apiKey)
            viewModel.updateAudiobookApiSettings(updated)
        }
        "movies" -> {
            val current = viewModel.apiSettings.value.movieTvApis
            val updated = when (providerName) {
                "TMDB" -> current.copy(tmdbEnabled = enabled, tmdbApiKey = apiKey)
                "OMDb" -> current.copy(omdbEnabled = enabled, omdbApiKey = apiKey)
                else -> current
            }
            viewModel.updateMovieTvApiSettings(updated)
        }
        "music" -> {
            val current = viewModel.apiSettings.value.musicApis
            val updated = when (providerName) {
                "MusicBrainz" -> current.copy(musicBrainzEnabled = enabled)
                "Spotify" -> current.copy(spotifyEnabled = enabled, spotifyClientId = apiKey)
                else -> current
            }
            viewModel.updateMusicApiSettings(updated)
        }
    }
}

private fun getBooksProviders(settings: BookApiSettings): List<ApiProvider> = listOf(
    ApiProvider(
        name = "Open Library",
        description = "Free bibliographic information",
        requiresApiKey = false,
        isEnabled = settings.openLibraryEnabled,
        website = "https://openlibrary.org/developers/api",
        mediaType = MediaType.BOOKS
    ),
    ApiProvider(
        name = "Google Books",
        description = "Full-text search and book information",
        requiresApiKey = true,
        isEnabled = settings.googleBooksEnabled,
        apiKey = settings.googleBooksApiKey,
        website = "https://developers.google.com/books",
        mediaType = MediaType.BOOKS
    ),
    ApiProvider(
        name = "Hardcover", 
        description = "Modern Goodreads alternative",
        requiresApiKey = false,
        isEnabled = settings.hardcoverEnabled,
        website = "https://hardcover.app/graphql",
        mediaType = MediaType.BOOKS
    )
)

private fun getComicsProviders(settings: ComicApiSettings): List<ApiProvider> = listOf(
    ApiProvider(
        name = "ComicVine",
        description = "Comprehensive comic book database",
        requiresApiKey = true,
        isEnabled = settings.comicVineEnabled,
        apiKey = settings.comicVineApiKey,
        website = "https://comicvine.gamespot.com/api/",
        mediaType = MediaType.COMICS
    )
)

private fun getAudiobooksProviders(settings: AudiobookApiSettings): List<ApiProvider> = listOf(
    ApiProvider(
        name = "OverDrive",
        description = "Audiobook metadata service",
        requiresApiKey = true,
        isEnabled = settings.overDriveEnabled,
        apiKey = settings.overDriveApiKey,
        website = "https://developer.overdrive.com/",
        mediaType = MediaType.AUDIOBOOKS
    )
)

private fun getMoviesProviders(settings: MovieTvApiSettings): List<ApiProvider> = listOf(
    ApiProvider(
        name = "TMDB",
        description = "The Movie Database - community-built",
        requiresApiKey = true,
        isEnabled = settings.tmdbEnabled,
        apiKey = settings.tmdbApiKey,
        website = "https://www.themoviedb.org/documentation/api",
        mediaType = MediaType.MOVIES_TV
    ),
    ApiProvider(
        name = "OMDb",
        description = "The Open Movie Database",
        requiresApiKey = true,
        isEnabled = settings.omdbEnabled,
        apiKey = settings.omdbApiKey,
        website = "https://www.omdbapi.com/",
        mediaType = MediaType.MOVIES_TV
    )
)

private fun getMusicProviders(settings: MusicApiSettings): List<ApiProvider> = listOf(
    ApiProvider(
        name = "MusicBrainz",
        description = "Open music encyclopedia",
        requiresApiKey = false,
        isEnabled = settings.musicBrainzEnabled,
        website = "https://musicbrainz.org/doc/Development",
        mediaType = MediaType.MUSIC
    ),
    ApiProvider(
        name = "Spotify",
        description = "Comprehensive music data",
        requiresApiKey = true,
        isEnabled = settings.spotifyEnabled,
        apiKey = settings.spotifyClientId,
        website = "https://developer.spotify.com/documentation/web-api",
        mediaType = MediaType.MUSIC
    )
)