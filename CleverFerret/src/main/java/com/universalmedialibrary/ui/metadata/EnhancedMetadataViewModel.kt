package com.universalmedialibrary.ui.metadata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.services.metadata.MetadataApiService
import com.universalmedialibrary.services.metadata.MetadataSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnhancedMetadataViewModel @Inject constructor(
    val metadataApiService: MetadataApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnhancedMetadataUiState())
    val uiState: StateFlow<EnhancedMetadataUiState> = _uiState.asStateFlow()

    fun searchMetadata(query: String, mediaType: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            
            try {
                val results = when (mediaType.uppercase()) {
                    "BOOK" -> metadataApiService.searchBooks(query)
                    "MOVIE" -> metadataApiService.searchMovies(query, null) // API key would come from settings
                    "MUSIC" -> metadataApiService.searchMusic(query)
                    else -> metadataApiService.searchBooks(query)
                }
                
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    searchResults = results,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = "Search failed: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun loadMetadata(itemId: Long) {
        viewModelScope.launch {
            // In a real app, load from database
            // For now, just demo data
            _uiState.value = _uiState.value.copy(
                title = "Demo Item $itemId",
                author = "Demo Author",
                description = "Demo description loaded from database",
                year = 2024,
                rating = 4.2f,
                coverUrl = "https://via.placeholder.com/300x450/1a1a1a/e5a00d?text=Demo"
            )
        }
    }

    fun updateMetadata(
        title: String = _uiState.value.title,
        author: String = _uiState.value.author,
        description: String = _uiState.value.description,
        year: Int = _uiState.value.year,
        rating: Float = _uiState.value.rating,
        coverUrl: String = _uiState.value.coverUrl,
        genre: String = _uiState.value.genre,
        isbn: String = _uiState.value.isbn
    ) {
        _uiState.value = _uiState.value.copy(
            title = title,
            author = author,
            description = description,
            year = year,
            rating = rating,
            coverUrl = coverUrl,
            genre = genre,
            isbn = isbn
        )
    }

    fun saveMetadata(itemId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            try {
                // In a real app, save to database
                // For now, just simulate delay
                kotlinx.coroutines.delay(1000)
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Save failed: ${e.message}"
                )
            }
        }
    }
}

data class EnhancedMetadataUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val year: Int = 0,
    val rating: Float = 0f,
    val coverUrl: String = "",
    val genre: String = "",
    val isbn: String = "",
    val searchResults: List<MetadataSearchResult> = emptyList(),
    val error: String? = null
)