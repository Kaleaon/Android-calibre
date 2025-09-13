package com.universalmedialibrary.ui.metadata

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.universalmedialibrary.services.metadata.MetadataApiService
import com.universalmedialibrary.services.metadata.MetadataSearchResult
import com.universalmedialibrary.ui.theme.PlexTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedMetadataEditorScreen(
    mediaId: Long,
    navController: NavController,
    metadataApiService: MetadataApiService = hiltViewModel<EnhancedMetadataViewModel>().metadataApiService
) {
    PlexTheme {
        var title by remember { mutableStateOf("") }
        var author by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var year by remember { mutableStateOf("") }
        var genre by remember { mutableStateOf("") }
        var rating by remember { mutableFloatStateOf(0f) }
        var coverUrl by remember { mutableStateOf("") }
        var isbn by remember { mutableStateOf("") }
        
        var searchQuery by remember { mutableStateOf("") }
        var searchResults by remember { mutableStateOf<List<MetadataSearchResult>>(emptyList()) }
        var isSearching by remember { mutableStateOf(false) }
        var showSearchDialog by remember { mutableStateOf(false) }
        var mediaType by remember { mutableStateOf("BOOK") }
        
        val coroutineScope = rememberCoroutineScope()

        // Load existing metadata on start
        LaunchedEffect(mediaId) {
            // In a real app, load existing metadata from database
            title = "Demo Media Item $mediaId"
            author = "Demo Author"
            description = "This is demonstration metadata that would be loaded from your database."
            year = "2024"
            genre = "Fiction"
            rating = 4.2f
            coverUrl = "https://via.placeholder.com/300x450/1a1a1a/e5a00d?text=Demo+Cover"
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Edit Metadata",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                // Save metadata
                                navController.navigateUp()
                            }
                        ) {
                            Text(
                                "Save",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Main content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Search section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Metadata Search",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Search for metadata") },
                                placeholder = { Text("Enter title, author, or keywords...") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (searchQuery.isNotBlank()) {
                                                isSearching = true
                                                coroutineScope.launch {
                                                    try {
                                                        searchResults = when (mediaType) {
                                                            "BOOK" -> metadataApiService.searchBooks(searchQuery)
                                                            "MOVIE" -> metadataApiService.searchMovies(searchQuery, null)
                                                            "MUSIC" -> metadataApiService.searchMusic(searchQuery)
                                                            else -> metadataApiService.searchBooks(searchQuery)
                                                        }
                                                        showSearchDialog = true
                                                    } catch (e: Exception) {
                                                        // Handle error
                                                    } finally {
                                                        isSearching = false
                                                    }
                                                }
                                            }
                                        }
                                    ) {
                                        if (isSearching) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(Icons.Default.Search, contentDescription = "Search")
                                        }
                                    }
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Media type selector
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("BOOK", "MOVIE", "MUSIC").forEach { type ->
                                    FilterChip(
                                        onClick = { mediaType = type },
                                        label = { Text(type) },
                                        selected = mediaType == type
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cover and basic info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Cover image
                        Card(
                            modifier = Modifier.width(120.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = coverUrl.ifEmpty { "https://via.placeholder.com/300x450/2d3136/e5a00d?text=No+Cover" },
                                    contentDescription = "Cover",
                                    modifier = Modifier
                                        .size(100.dp, 140.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = coverUrl,
                                    onValueChange = { coverUrl = it },
                                    label = { Text("Cover URL") },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // Basic metadata
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Title") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = author,
                                onValueChange = { author = it },
                                label = { 
                                    Text(when (mediaType) {
                                        "BOOK" -> "Author"
                                        "MOVIE" -> "Director"
                                        "MUSIC" -> "Artist"
                                        else -> "Creator"
                                    })
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = year,
                                    onValueChange = { year = it },
                                    label = { Text("Year") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                OutlinedTextField(
                                    value = genre,
                                    onValueChange = { genre = it },
                                    label = { Text("Genre") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rating
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Rating: ${String.format("%.1f", rating)} / 5.0",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable { rating = (index + 1).toFloat() },
                                        tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            Slider(
                                value = rating,
                                onValueChange = { rating = it },
                                valueRange = 0f..5f,
                                steps = 9, // 0.5 increments
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Book-specific fields
                    if (mediaType == "BOOK") {
                        OutlinedTextField(
                            value = isbn,
                            onValueChange = { isbn = it },
                            label = { Text("ISBN") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Search results dialog
        if (showSearchDialog) {
            AlertDialog(
                onDismissRequest = { showSearchDialog = false },
                title = { 
                    Text(
                        "Search Results for \"$searchQuery\"",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    LazyColumn(
                        modifier = Modifier.height(400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { result ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Apply search result to form
                                        title = result.title
                                        author = result.author ?: result.director ?: result.artist ?: ""
                                        year = result.year?.toString() ?: ""
                                        description = result.description ?: ""
                                        coverUrl = result.coverUrl ?: ""
                                        rating = result.rating ?: 0f
                                        if (result.isbn != null) isbn = result.isbn
                                        showSearchDialog = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AsyncImage(
                                        model = result.coverUrl ?: "https://via.placeholder.com/60x80/2d3136/e5a00d?text=?",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(40.dp, 60.dp)
                                            .clip(MaterialTheme.shapes.small),
                                        contentScale = ContentScale.Crop
                                    )
                                    
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = result.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = result.author ?: result.director ?: result.artist ?: "Unknown",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (result.year != null) {
                                            Text(
                                                text = result.year.toString(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "Source: ${result.source.name}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (searchResults.isEmpty() && !isSearching) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = "No results found. Try a different search term.",
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSearchDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}