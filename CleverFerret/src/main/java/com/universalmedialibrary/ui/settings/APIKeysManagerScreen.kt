package com.universalmedialibrary.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.universalmedialibrary.ui.theme.PlexTheme

data class APIKeyConfig(
    val key: String,
    val displayName: String,
    val description: String,
    val websiteUrl: String,
    val isFree: Boolean,
    val category: APICategory,
    val isRequired: Boolean = false,
    val setupInstructions: String
)

enum class APICategory {
    BOOKS,
    PODCASTS,
    MOVIES_TV,
    COMICS_MANGA,
    MUSIC,
    AUDIOBOOKS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APIKeysManagerScreen(
    navController: NavController,
    viewModel: APIKeysViewModel = hiltViewModel()
) {
    PlexTheme {
        val uiState by viewModel.uiState.collectAsState()
        var selectedCategory by remember { mutableStateOf<APICategory?>(null) }
        var showInstructions by remember { mutableStateOf<APIKeyConfig?>(null) }

        val apiConfigs = remember {
            listOf(
                // Book APIs
                APIKeyConfig(
                    key = "google_books",
                    displayName = "Google Books API",
                    description = "Comprehensive book metadata from Google's database",
                    websiteUrl = "https://developers.google.com/books",
                    isFree = true,
                    category = APICategory.BOOKS,
                    setupInstructions = "1. Go to Google Cloud Console\\n2. Enable Books API\\n3. Create credentials\\n4. Copy API key"
                ),
                APIKeyConfig(
                    key = "goodreads",
                    displayName = "Goodreads API",
                    description = "Book ratings, reviews, and social data",
                    websiteUrl = "https://www.goodreads.com/api",
                    isFree = true,
                    category = APICategory.BOOKS,
                    setupInstructions = "1. Sign up for Goodreads developer account\\n2. Request API access\\n3. Get your API key"
                ),
                APIKeyConfig(
                    key = "hardcover",
                    displayName = "Hardcover API",
                    description = "Modern book discovery and metadata",
                    websiteUrl = "https://hardcover.app/api",
                    isFree = true,
                    category = APICategory.BOOKS,
                    setupInstructions = "1. Create Hardcover account\\n2. Generate API key in settings\\n3. Copy the key"
                ),
                APIKeyConfig(
                    key = "amazon_access_key",
                    displayName = "Amazon Product Advertising API",
                    description = "Official Amazon book data with prices",
                    websiteUrl = "https://webservices.amazon.com/paapi5/documentation/",
                    isFree = false,
                    category = APICategory.BOOKS,
                    setupInstructions = "1. Join Amazon Associates\\n2. Apply for Product Advertising API\\n3. Get Access Key and Secret Key"
                ),
                APIKeyConfig(
                    key = "amazon_secret_key",
                    displayName = "Amazon Secret Key",
                    description = "Secret key for Amazon Product Advertising API",
                    websiteUrl = "https://webservices.amazon.com/paapi5/documentation/",
                    isFree = false,
                    category = APICategory.BOOKS,
                    setupInstructions = "Use with Amazon Access Key for authentication"
                ),
                APIKeyConfig(
                    key = "isbn_db",
                    displayName = "ISBN-DB API",
                    description = "Comprehensive ISBN database",
                    websiteUrl = "https://isbndb.com/apidocs",
                    isFree = false,
                    category = APICategory.BOOKS,
                    setupInstructions = "1. Create ISBNdb account\\n2. Choose a plan\\n3. Get your API key"
                ),
                
                // Comic/Manga APIs
                APIKeyConfig(
                    key = "comicvine",
                    displayName = "ComicVine API",
                    description = "Comprehensive comic book database",
                    websiteUrl = "https://comicvine.gamespot.com/api/",
                    isFree = true,
                    category = APICategory.COMICS_MANGA,
                    isRequired = true,
                    setupInstructions = "1. Create GameSpot account\\n2. Request API access\\n3. Get your API key"
                ),
                
                // Podcast APIs
                APIKeyConfig(
                    key = "listen_notes",
                    displayName = "Listen Notes API",
                    description = "Most comprehensive podcast search API",
                    websiteUrl = "https://www.listennotes.com/api/",
                    isFree = true,
                    category = APICategory.PODCASTS,
                    setupInstructions = "1. Sign up for Listen Notes\\n2. Get free API key\\n3. Upgrade for more requests"
                ),
                APIKeyConfig(
                    key = "spotify_client_id",
                    displayName = "Spotify Client ID",
                    description = "Access Spotify podcast directory",
                    websiteUrl = "https://developer.spotify.com/",
                    isFree = true,
                    category = APICategory.PODCASTS,
                    setupInstructions = "1. Create Spotify Developer account\\n2. Create new app\\n3. Get Client ID and Secret"
                ),
                APIKeyConfig(
                    key = "spotify_client_secret",
                    displayName = "Spotify Client Secret",
                    description = "Secret for Spotify API authentication",
                    websiteUrl = "https://developer.spotify.com/",
                    isFree = true,
                    category = APICategory.PODCASTS,
                    setupInstructions = "Use with Spotify Client ID"
                ),
                APIKeyConfig(
                    key = "taddy",
                    displayName = "Taddy API",
                    description = "Podcast API with webhook notifications",
                    websiteUrl = "https://taddy.org/",
                    isFree = false,
                    category = APICategory.PODCASTS,
                    setupInstructions = "1. Sign up for Taddy\\n2. Choose a plan\\n3. Get API key"
                ),
                
                // Movie/TV APIs
                APIKeyConfig(
                    key = "tmdb",
                    displayName = "TMDB API",
                    description = "The Movie Database - comprehensive film/TV data",
                    websiteUrl = "https://www.themoviedb.org/settings/api",
                    isFree = true,
                    category = APICategory.MOVIES_TV,
                    setupInstructions = "1. Create TMDB account\\n2. Request API key\\n3. Get approved key"
                ),
                APIKeyConfig(
                    key = "omdb",
                    displayName = "OMDb API",
                    description = "Open Movie Database",
                    websiteUrl = "https://www.omdbapi.com/",
                    isFree = true,
                    category = APICategory.MOVIES_TV,
                    setupInstructions = "1. Visit OMDb website\\n2. Request free API key\\n3. Check email for key"
                ),
                APIKeyConfig(
                    key = "tvdb",
                    displayName = "TVDB API",
                    description = "TheTVDB for detailed TV show information",
                    websiteUrl = "https://thetvdb.com/api-information",
                    isFree = true,
                    category = APICategory.MOVIES_TV,
                    setupInstructions = "1. Create TVDB account\\n2. Subscribe to API\\n3. Get your API key"
                ),
                
                // Music APIs
                APIKeyConfig(
                    key = "lastfm",
                    displayName = "Last.fm API",
                    description = "Music metadata and scrobbling",
                    websiteUrl = "https://www.last.fm/api",
                    isFree = true,
                    category = APICategory.MUSIC,
                    setupInstructions = "1. Create Last.fm account\\n2. Create API application\\n3. Get API key"
                ),
                APIKeyConfig(
                    key = "discogs_token",
                    displayName = "Discogs API",
                    description = "Comprehensive music database",
                    websiteUrl = "https://www.discogs.com/developers/",
                    isFree = true,
                    category = APICategory.MUSIC,
                    setupInstructions = "1. Create Discogs account\\n2. Generate personal access token\\n3. Copy token"
                )
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "API Keys Manager",
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.saveAllKeys() }) {
                            Icon(Icons.Default.Save, contentDescription = "Save All")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Info card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Enhanced Metadata Collection",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Configure API keys to unlock comprehensive metadata from dozens of sources. Many APIs are free - just sign up and get your key!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Category filter
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Category headers and items
                    APICategory.values().forEach { category ->
                        val categoryApis = apiConfigs.filter { it.category == category }
                        
                        if (categoryApis.isNotEmpty()) {
                            item {
                                CategoryHeader(
                                    category = category,
                                    isExpanded = selectedCategory == category,
                                    onToggle = { 
                                        selectedCategory = if (selectedCategory == category) null else category
                                    }
                                )
                            }
                            
                            if (selectedCategory == category) {
                                items(categoryApis) { apiConfig ->
                                    APIKeyCard(
                                        config = apiConfig,
                                        currentValue = uiState.apiKeys[apiConfig.key] ?: "",
                                        onValueChange = { newValue ->
                                            viewModel.updateApiKey(apiConfig.key, newValue)
                                        },
                                        onShowInstructions = { showInstructions = apiConfig }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Instructions dialog
        showInstructions?.let { config ->
            AlertDialog(
                onDismissRequest = { showInstructions = null },
                title = { 
                    Text(
                        "${config.displayName} Setup",
                        fontWeight = FontWeight.Medium
                    )
                },
                text = {
                    Column {
                        Text(
                            text = config.setupInstructions.replace("\\n", "\n"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                // Open website URL
                                // In a real app, use Intent to open browser
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open ${config.displayName} Website")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showInstructions = null }) {
                        Text("Got it")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryHeader(
    category: APICategory,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val categoryInfo = when (category) {
        APICategory.BOOKS -> "📚" to "Book Metadata"
        APICategory.PODCASTS -> "🎙️" to "Podcast Discovery"
        APICategory.MOVIES_TV -> "🎬" to "Movies & TV Shows"
        APICategory.COMICS_MANGA -> "📖" to "Comics & Manga"
        APICategory.MUSIC -> "🎵" to "Music Metadata"
        APICategory.AUDIOBOOKS -> "🎧" to "Audiobook Sources"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryInfo.first,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = categoryInfo.second,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }
        }
    }
}

@Composable
fun APIKeyCard(
    config: APIKeyConfig,
    currentValue: String,
    onValueChange: (String) -> Unit,
    onShowInstructions: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = config.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        if (config.isFree) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "FREE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        
                        if (config.isRequired) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "REQUIRED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = config.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                IconButton(onClick = onShowInstructions) {
                    Icon(
                        Icons.Default.Help,
                        contentDescription = "Setup Instructions",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = currentValue,
                onValueChange = onValueChange,
                label = { Text("API Key") },
                placeholder = { Text("Enter your ${config.displayName} key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isPasswordVisible) "Hide key" else "Show key"
                        )
                    }
                },
                supportingText = if (currentValue.isNotEmpty()) {
                    { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Configured")
                        }
                    }
                } else null
            )
        }
    }
}