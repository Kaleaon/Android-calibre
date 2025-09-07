package com.universalmedialibrary.ui.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.universalmedialibrary.data.MediaType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UniversalMediaLibraryScreen(
    navController: NavController,
    libraryId: Long = 1L,
    viewModel: UniversalMediaLibraryViewModel = hiltViewModel()
) {
    val mediaItems by viewModel.mediaItems.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMediaType by viewModel.selectedMediaType.collectAsState()
    val showFilters by viewModel.showFilters.collectAsState()
    
    var showSortMenu by remember { mutableStateOf(false) }
    var showViewModeMenu by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { MediaType.values().size })
    
    LaunchedEffect(libraryId) {
        viewModel.loadMediaItems(libraryId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    "Universal Media Library", 
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                ) 
            },
            actions = {
                // Search
                IconButton(onClick = { /* Open search */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                
                // View Mode Toggle
                IconButton(onClick = { showViewModeMenu = true }) {
                    Icon(
                        when (viewMode) {
                            ViewMode.GRID -> Icons.Default.GridView
                            ViewMode.LIST -> Icons.Default.ViewList
                            ViewMode.COVER_FLOW -> Icons.Default.ViewCarousel
                        },
                        contentDescription = "View Mode"
                    )
                }
                
                DropdownMenu(
                    expanded = showViewModeMenu,
                    onDismissRequest = { showViewModeMenu = false }
                ) {
                    ViewMode.values().forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.displayName) },
                            onClick = {
                                viewModel.setViewMode(mode)
                                showViewModeMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    when (mode) {
                                        ViewMode.GRID -> Icons.Default.GridView
                                        ViewMode.LIST -> Icons.Default.ViewList
                                        ViewMode.COVER_FLOW -> Icons.Default.ViewCarousel
                                    },
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
                
                // Sort Menu
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort")
                }
                
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                viewModel.setSortOption(option)
                                showSortMenu = false
                            },
                            trailingIcon = if (sortOption == option) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }
                
                // Filters
                IconButton(onClick = { viewModel.toggleFilters() }) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filters",
                        tint = if (showFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )

        // Media Type Tabs
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp
        ) {
            MediaType.values().forEachIndexed { index, mediaType ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                        viewModel.setSelectedMediaType(mediaType)
                    },
                    text = { 
                        Text(
                            mediaType.displayName(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) 
                    },
                    icon = { 
                        Icon(
                            mediaType.getIcon(),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        ) 
                    }
                )
            }
        }

        // Filters Row (if enabled)
        if (showFilters) {
            FilterRow(
                viewModel = viewModel,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Content Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val mediaType = MediaType.values()[page]
            val filteredItems = mediaItems.filter { it.mediaType == mediaType }
            
            MediaTypeContent(
                mediaType = mediaType,
                items = filteredItems,
                viewMode = viewMode,
                onItemClick = { item ->
                    // Navigate to appropriate viewer based on media type
                    when (item.mediaType) {
                        MediaType.BOOK, MediaType.EBOOK -> 
                            navController.navigate("ereader/${item.itemId}")
                        MediaType.AUDIOBOOK -> 
                            navController.navigate("audioplayer/${item.itemId}")
                        MediaType.MOVIE, MediaType.TV_SHOW, MediaType.DOCUMENTARY -> 
                            navController.navigate("videoplayer/${item.itemId}")
                        MediaType.MUSIC_TRACK, MediaType.MUSIC_ALBUM -> 
                            navController.navigate("musicplayer/${item.itemId}")
                        MediaType.PODCAST_EPISODE, MediaType.PODCAST_SERIES -> 
                            navController.navigate("podcastplayer/${item.itemId}")
                        MediaType.MAGAZINE, MediaType.NEWSPAPER -> 
                            navController.navigate("magazinereader/${item.itemId}")
                        else -> 
                            navController.navigate("documentviewer/${item.itemId}")
                    }
                }
            )
        }
    }
}

@Composable
fun FilterRow(
    viewModel: UniversalMediaLibraryViewModel,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                onClick = { /* Show favorites only */ },
                label = { Text("Favorites") },
                selected = false,
                leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        
        item {
            FilterChip(
                onClick = { /* Show recent */ },
                label = { Text("Recent") },
                selected = false,
                leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        
        item {
            FilterChip(
                onClick = { /* Show downloaded */ },
                label = { Text("Downloaded") },
                selected = false,
                leadingIcon = { Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
    }
}

@Composable
fun MediaTypeContent(
    mediaType: MediaType,
    items: List<MediaItemWithMetadata>,
    viewMode: ViewMode,
    onItemClick: (MediaItemWithMetadata) -> Unit
) {
    when (viewMode) {
        ViewMode.GRID -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { item ->
                    MediaItemCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
        
        ViewMode.LIST -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    MediaItemListItem(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
        
        ViewMode.COVER_FLOW -> {
            // Implement cover flow view
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { item ->
                    MediaItemCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        modifier = Modifier.width(150.dp)
                    )
                }
            }
        }
    }
}

// Extension functions for MediaType
fun MediaType.displayName(): String {
    return when (this) {
        MediaType.BOOK -> "Books"
        MediaType.EBOOK -> "E-Books"
        MediaType.AUDIOBOOK -> "Audiobooks"
        MediaType.MOVIE -> "Movies"
        MediaType.TV_SHOW -> "TV Shows"
        MediaType.DOCUMENTARY -> "Documentaries"
        MediaType.MUSIC_TRACK -> "Music"
        MediaType.MUSIC_ALBUM -> "Albums"
        MediaType.PODCAST_EPISODE -> "Podcasts"
        MediaType.PODCAST_SERIES -> "Podcast Series"
        MediaType.COMIC -> "Comics"
        MediaType.MANGA -> "Manga"
        MediaType.MAGAZINE -> "Magazines"
        MediaType.NEWSPAPER -> "Newspapers"
        MediaType.JOURNAL -> "Journals"
        MediaType.NEWS_ARTICLE -> "News"
        MediaType.ACADEMIC_PAPER -> "Papers"
        MediaType.REPORT -> "Reports"
        MediaType.PRESENTATION -> "Presentations"
        MediaType.SPREADSHEET -> "Spreadsheets"
        MediaType.IMAGE -> "Images"
        MediaType.PHOTO_ALBUM -> "Photo Albums"
        MediaType.VIDEO_CLIP -> "Video Clips"
        MediaType.ANIMATION -> "Animations"
        MediaType.GAME -> "Games"
        MediaType.SOFTWARE -> "Software"
        MediaType.ARCHIVE -> "Archives"
        MediaType.DOCUMENT -> "Documents"
        MediaType.NOTE -> "Notes"
        MediaType.RECIPE -> "Recipes"
        MediaType.MANUAL -> "Manuals"
        MediaType.TUTORIAL -> "Tutorials"
    }
}

fun MediaType.getIcon(): ImageVector {
    return when (this) {
        MediaType.BOOK, MediaType.EBOOK -> Icons.Default.MenuBook
        MediaType.AUDIOBOOK -> Icons.Default.Headphones
        MediaType.MOVIE, MediaType.DOCUMENTARY -> Icons.Default.Movie
        MediaType.TV_SHOW -> Icons.Default.Tv
        MediaType.MUSIC_TRACK, MediaType.MUSIC_ALBUM -> Icons.Default.MusicNote
        MediaType.PODCAST_EPISODE, MediaType.PODCAST_SERIES -> Icons.Default.MusicNote // Use MusicNote as fallback for Podcast
        MediaType.COMIC, MediaType.MANGA -> Icons.Default.AutoStories
        MediaType.MAGAZINE, MediaType.NEWSPAPER -> Icons.Default.Article
        MediaType.JOURNAL -> Icons.Default.Book
        MediaType.NEWS_ARTICLE -> Icons.Default.Article // Use Article as fallback for Newspaper
        MediaType.ACADEMIC_PAPER -> Icons.Default.School
        MediaType.REPORT -> Icons.Default.Assessment
        MediaType.PRESENTATION -> Icons.Default.Slideshow
        MediaType.SPREADSHEET -> Icons.Default.TableChart
        MediaType.IMAGE -> Icons.Default.Image
        MediaType.PHOTO_ALBUM -> Icons.Default.PhotoAlbum
        MediaType.VIDEO_CLIP -> Icons.Default.VideoFile
        MediaType.ANIMATION -> Icons.Default.Movie // Use Movie as fallback for Animation
        MediaType.GAME -> Icons.Default.SportsEsports
        MediaType.SOFTWARE -> Icons.Default.Apps
        MediaType.ARCHIVE -> Icons.Default.FolderZip // Use FolderZip as fallback for Archive
        MediaType.DOCUMENT -> Icons.Default.Description
        MediaType.NOTE -> Icons.Default.Note
        MediaType.RECIPE -> Icons.Default.Restaurant
        MediaType.MANUAL -> Icons.Default.Help
        MediaType.TUTORIAL -> Icons.Default.School // Use School as fallback for PlayLesson
    }
}

enum class ViewMode(val displayName: String) {
    GRID("Grid"),
    LIST("List"),
    COVER_FLOW("Cover Flow")
}

enum class SortOption(val displayName: String) {
    TITLE("Title"),
    DATE_ADDED("Date Added"),
    DATE_MODIFIED("Date Modified"),
    AUTHOR("Author"),
    RATING("Rating"),
    RECENTLY_VIEWED("Recently Viewed")
}

// Placeholder data classes - these would be defined in your data layer
data class MediaItemWithMetadata(
    val itemId: Long,
    val title: String,
    val mediaType: MediaType,
    val coverImagePath: String? = null,
    val author: String? = null,
    val dateAdded: Long,
    val isFavorite: Boolean = false,
    val progress: Float = 0f
)

@Composable
fun MediaItemCard(
    item: MediaItemWithMetadata,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Placeholder for cover image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.mediaType.getIcon(),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
            
            if (item.author != null) {
                Text(
                    text = item.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MediaItemListItem(
    item: MediaItemWithMetadata,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                item.mediaType.getIcon(),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (item.author != null) {
                    Text(
                        text = item.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    text = item.mediaType.displayName(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Favorite and Actions
            if (item.isFavorite) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}