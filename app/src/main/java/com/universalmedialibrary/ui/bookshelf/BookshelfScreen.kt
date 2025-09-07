package com.universalmedialibrary.ui.bookshelf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.universalmedialibrary.data.local.model.BookDetails
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedBookshelfScreen(
    navController: NavController,
    libraryId: Long = 1L,
    viewModel: BookshelfViewModel = hiltViewModel()
) {
    val books by viewModel.books.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val showFilters by viewModel.showFilters.collectAsState()
    
    var showSortMenu by remember { mutableStateOf(false) }
    var showViewModeMenu by remember { mutableStateOf(false) }

    LaunchedEffect(libraryId) {
        viewModel.loadBooks(libraryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Bookshelf",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    // Search
                    IconButton(onClick = { viewModel.toggleSearch() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    
                    // View Mode
                    IconButton(onClick = { showViewModeMenu = true }) {
                        Icon(
                            when (viewMode) {
                                ViewMode.GRID -> Icons.Default.GridView
                                ViewMode.LIST -> Icons.Default.List
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
                                    Icon(mode.icon, contentDescription = null)
                                }
                            )
                        }
                    }
                    
                    // Sort
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
                                leadingIcon = {
                                    Icon(option.icon, contentDescription = null)
                                }
                            )
                        }
                    }
                    
                    // Filter
                    IconButton(onClick = { viewModel.toggleFilters() }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (showFilters) MaterialTheme.colorScheme.primary 
                                  else MaterialTheme.colorScheme.onSurface
                        )
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
            // Search Bar
            AnimatedVisibility(visible = searchQuery.isNotEmpty() || viewModel.searchActive.collectAsState().value) {
                SearchTextField(
                    query = searchQuery,
                    onQueryChange = viewModel::setSearchQuery,
                    onClearSearch = { viewModel.clearSearch() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Filters
            AnimatedVisibility(visible = showFilters) {
                FiltersRow(
                    selectedGenre = selectedGenre,
                    onGenreSelected = viewModel::setSelectedGenre,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Favorites Section (if any)
            if (favorites.isNotEmpty()) {
                FavoritesSection(
                    favorites = favorites,
                    onClick = { book ->
                        navController.navigate("book_details/${book.mediaItem.itemId}")
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Main Content
            when (viewMode) {
                ViewMode.GRID -> {
                    GridBookView(
                        books = books,
                        onClick = { book ->
                            navController.navigate("book_details/${book.mediaItem.itemId}")
                        },
                        onFavoriteToggle = viewModel::toggleFavorite,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ViewMode.LIST -> {
                    ListBookView(
                        books = books,
                        onClick = { book ->
                            navController.navigate("book_details/${book.mediaItem.itemId}")
                        },
                        onFavoriteToggle = viewModel::toggleFavorite,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ViewMode.COVER_FLOW -> {
                    CoverFlowView(
                        books = books,
                        onClick = { book ->
                            navController.navigate("book_details/${book.mediaItem.itemId}")
                        },
                        onFavoriteToggle = viewModel::toggleFavorite,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search books...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearSearch) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun FiltersRow(
    selectedGenre: String?,
    onGenreSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                onClick = { onGenreSelected(null) },
                label = { Text("All") },
                selected = selectedGenre == null
            )
        }
        
        // Sample genres - in real implementation, these would come from the database
        items(listOf("Fiction", "Non-Fiction", "Mystery", "Romance", "Sci-Fi", "Fantasy")) { genre ->
            FilterChip(
                onClick = { onGenreSelected(genre) },
                label = { Text(genre) },
                selected = selectedGenre == genre
            )
        }
    }
}

@Composable
fun FavoritesSection(
    favorites: List<BookDetails>,
    onClick: (BookDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Favorites",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favorites.take(10)) { book -> // Show max 10 favorites
                CompactBookCard(
                    book = book,
                    onClick = { onClick(book) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
fun GridBookView(
    books: List<BookDetails>,
    onClick: (BookDetails) -> Unit,
    onFavoriteToggle: (BookDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books) { book ->
            EnhancedBookCard(
                book = book,
                onClick = { onClick(book) },
                onFavoriteToggle = { onFavoriteToggle(book) }
            )
        }
    }
}

@Composable
fun ListBookView(
    books: List<BookDetails>,
    onClick: (BookDetails) -> Unit,
    onFavoriteToggle: (BookDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { book ->
            ListBookItem(
                book = book,
                onClick = { onClick(book) },
                onFavoriteToggle = { onFavoriteToggle(book) }
            )
        }
    }
}

@Composable
fun CoverFlowView(
    books: List<BookDetails>,
    onClick: (BookDetails) -> Unit,
    onFavoriteToggle: (BookDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books) { book ->
            CoverFlowCard(
                book = book,
                onClick = { onClick(book) },
                onFavoriteToggle = { onFavoriteToggle(book) }
            )
        }
    }
}

enum class ViewMode(val displayName: String, val icon: ImageVector) {
    GRID("Grid", Icons.Default.GridView),
    LIST("List", Icons.Default.List),
    COVER_FLOW("Cover Flow", Icons.Default.ViewCarousel)
}

enum class SortOption(val displayName: String, val icon: ImageVector) {
    TITLE("Title", Icons.Default.SortByAlpha),
    AUTHOR("Author", Icons.Default.Person),
    DATE_ADDED("Date Added", Icons.Default.Schedule),
    RATING("Rating", Icons.Default.Star),
    RECENTLY_READ("Recently Read", Icons.Default.History)
}