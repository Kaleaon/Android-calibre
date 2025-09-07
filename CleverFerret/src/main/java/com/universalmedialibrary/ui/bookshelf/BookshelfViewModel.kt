package com.universalmedialibrary.ui.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.BookDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val metadataDao: MetadataDao
) : ViewModel() {

    private val _viewMode = MutableStateFlow(ViewMode.GRID)
    val viewMode = _viewMode.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    val sortOption = _sortOption.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre = _selectedGenre.asStateFlow()

    private val _showFilters = MutableStateFlow(false)
    val showFilters = _showFilters.asStateFlow()

    private val _searchActive = MutableStateFlow(false)
    val searchActive = _searchActive.asStateFlow()

    private val _allBooks = MutableStateFlow<List<BookDetails>>(emptyList())
    
    // Filtered and sorted books
    val books = combine(
        _allBooks,
        _searchQuery,
        _selectedGenre,
        _sortOption
    ) { allBooks, query, genre, sort ->
        var filteredBooks = allBooks
        
        // Apply search filter
        if (query.isNotEmpty()) {
            filteredBooks = filteredBooks.filter { book ->
                book.metadata.title.contains(query, ignoreCase = true) ||
                book.authorName?.contains(query, ignoreCase = true) == true
            }
        }
        
        // Apply genre filter
        if (genre != null) {
            // In a real implementation, you'd filter by actual genre data
            // For now, this is a placeholder
            filteredBooks = filteredBooks.filter { 
                // Placeholder genre filtering logic
                true 
            }
        }
        
        // Apply sorting
        when (sort) {
            SortOption.TITLE -> filteredBooks.sortedBy { it.metadata.title }
            SortOption.AUTHOR -> filteredBooks.sortedBy { it.authorName ?: "" }
            SortOption.DATE_ADDED -> filteredBooks.sortedByDescending { it.mediaItem.dateAdded }
            SortOption.RATING -> filteredBooks.sortedByDescending { it.metadata.rating ?: 0f }
            SortOption.RECENTLY_READ -> filteredBooks.sortedByDescending { it.mediaItem.lastScanned }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Favorites (books with isFavorite = true)
    val favorites = _allBooks.map { books ->
        books.filter { it.metadata.isFavorite }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun loadBooks(libraryId: Long) {
        viewModelScope.launch {
            mediaItemDao.getBookDetailsForLibrary(libraryId).collect { bookList ->
                _allBooks.value = bookList
            }
        }
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _searchActive.value = query.isNotEmpty()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchActive.value = false
    }

    fun toggleSearch() {
        _searchActive.value = !_searchActive.value
        if (!_searchActive.value) {
            _searchQuery.value = ""
        }
    }

    fun setSelectedGenre(genre: String?) {
        _selectedGenre.value = genre
    }

    fun toggleFilters() {
        _showFilters.value = !_showFilters.value
    }

    fun toggleFavorite(book: BookDetails) {
        viewModelScope.launch {
            try {
                // Update the metadata with toggled favorite status
                val updatedMetadata = book.metadata.copy(
                    isFavorite = !book.metadata.isFavorite
                )
                metadataDao.updateMetadata(updatedMetadata)
                
                // Update local state immediately for better UX
                val updatedBooks = _allBooks.value.map { existingBook ->
                    if (existingBook.metadata.itemId == book.metadata.itemId) {
                        existingBook.copy(metadata = updatedMetadata)
                    } else {
                        existingBook
                    }
                }
                _allBooks.value = updatedBooks
            } catch (e: Exception) {
                // Handle error - in a real implementation, you'd show a snackbar or toast
                // For now, just log it
                e.printStackTrace()
            }
        }
    }
}