package com.universalmedialibrary.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.data.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UniversalMediaLibraryViewModel @Inject constructor(
    // TODO: Inject repository when available
) : ViewModel() {

    private val _mediaItems = MutableStateFlow<List<MediaItemWithMetadata>>(emptyList())
    val mediaItems: StateFlow<List<MediaItemWithMetadata>> = _mediaItems.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.GRID)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMediaType = MutableStateFlow(MediaType.BOOK)
    val selectedMediaType: StateFlow<MediaType> = _selectedMediaType.asStateFlow()

    private val _showFilters = MutableStateFlow(false)
    val showFilters: StateFlow<Boolean> = _showFilters.asStateFlow()

    fun loadMediaItems(libraryId: Long) {
        viewModelScope.launch {
            // TODO: Load from repository
            // For now, create mock data to demonstrate the UI
            _mediaItems.value = createMockMediaItems()
        }
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
        // TODO: Re-sort media items based on the selected option
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        // TODO: Filter media items based on search query
    }

    fun setSelectedMediaType(mediaType: MediaType) {
        _selectedMediaType.value = mediaType
    }

    fun toggleFilters() {
        _showFilters.value = !_showFilters.value
    }

    private fun createMockMediaItems(): List<MediaItemWithMetadata> {
        return listOf(
            // Books
            MediaItemWithMetadata(
                itemId = 1L,
                title = "The Great Gatsby",
                mediaType = MediaType.BOOK,
                author = "F. Scott Fitzgerald",
                dateAdded = System.currentTimeMillis(),
                isFavorite = true,
                progress = 0.65f
            ),
            MediaItemWithMetadata(
                itemId = 2L,
                title = "Digital Fortress",
                mediaType = MediaType.EBOOK,
                author = "Dan Brown",
                dateAdded = System.currentTimeMillis() - 86400000,
                isFavorite = false,
                progress = 0.23f
            ),
            
            // Movies
            MediaItemWithMetadata(
                itemId = 3L,
                title = "Inception",
                mediaType = MediaType.MOVIE,
                author = "Christopher Nolan",
                dateAdded = System.currentTimeMillis() - 172800000,
                isFavorite = true,
                progress = 1.0f
            ),
            MediaItemWithMetadata(
                itemId = 4L,
                title = "The Office - Season 1",
                mediaType = MediaType.TV_SHOW,
                author = "NBC",
                dateAdded = System.currentTimeMillis() - 259200000,
                isFavorite = false,
                progress = 0.45f
            ),
            
            // Music
            MediaItemWithMetadata(
                itemId = 5L,
                title = "Bohemian Rhapsody",
                mediaType = MediaType.MUSIC_TRACK,
                author = "Queen",
                dateAdded = System.currentTimeMillis() - 345600000,
                isFavorite = true,
                progress = 1.0f
            ),
            MediaItemWithMetadata(
                itemId = 6L,
                title = "Abbey Road",
                mediaType = MediaType.MUSIC_ALBUM,
                author = "The Beatles",
                dateAdded = System.currentTimeMillis() - 432000000,
                isFavorite = true,
                progress = 0.8f
            ),
            
            // Podcasts
            MediaItemWithMetadata(
                itemId = 7L,
                title = "The Joe Rogan Experience #1234",
                mediaType = MediaType.PODCAST_EPISODE,
                author = "Joe Rogan",
                dateAdded = System.currentTimeMillis() - 518400000,
                isFavorite = false,
                progress = 0.33f
            ),
            
            // Magazines
            MediaItemWithMetadata(
                itemId = 8L,
                title = "National Geographic - March 2024",
                mediaType = MediaType.MAGAZINE,
                author = "National Geographic Society",
                dateAdded = System.currentTimeMillis() - 604800000,
                isFavorite = false,
                progress = 0.15f
            ),
            
            // Documents
            MediaItemWithMetadata(
                itemId = 9L,
                title = "Project Proposal Q2 2024",
                mediaType = MediaType.DOCUMENT,
                author = "Corporate Team",
                dateAdded = System.currentTimeMillis() - 691200000,
                isFavorite = false,
                progress = 0.95f
            ),
            
            // Academic Papers
            MediaItemWithMetadata(
                itemId = 10L,
                title = "Machine Learning in Healthcare",
                mediaType = MediaType.ACADEMIC_PAPER,
                author = "Dr. Jane Smith et al.",
                dateAdded = System.currentTimeMillis() - 777600000,
                isFavorite = true,
                progress = 0.7f
            )
        )
    }
}