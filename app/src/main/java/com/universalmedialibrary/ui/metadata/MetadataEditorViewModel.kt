package com.universalmedialibrary.ui.metadata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the metadata editor screen
 */
@HiltViewModel
class MetadataEditorViewModel @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val metadataDao: MetadataDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetadataEditorUiState())
    val uiState: StateFlow<MetadataEditorUiState> = _uiState.asStateFlow()

    private var currentItemId: Long? = null

    /**
     * Load metadata for the specified item
     */
    fun loadMetadata(itemId: Long) {
        currentItemId = itemId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Load the media item and its metadata
                val mediaItem = mediaItemDao.getMediaItemById(itemId)
                val metadataCommon = metadataDao.getMetadataCommonByItemId(itemId)
                val metadataBook = metadataDao.getMetadataBookByItemId(itemId)
                
                if (mediaItem != null && metadataCommon != null) {
                    // Convert to editable format
                    val metadata = EditableMetadata(
                        title = metadataCommon.title,
                        subtitle = metadataBook?.subtitle ?: "",
                        sortTitle = metadataCommon.sortTitle ?: "",
                        summary = metadataCommon.summary ?: "",
                        authors = emptyList(), // TODO: Load from People table
                        series = "", // TODO: Load from Series table
                        seriesIndex = null, // TODO: Load from Series table
                        rating = metadataCommon.rating?.toInt(),
                        publisher = metadataBook?.publisher ?: "",
                        isbn = metadataBook?.isbn ?: "",
                        genres = emptyList(), // TODO: Load from Genre table
                        releaseDate = metadataCommon.releaseDate?.toString() ?: ""
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        metadata = metadata,
                        originalMetadata = metadata,
                        hasChanges = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Media item not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load metadata: ${e.message}"
                )
            }
        }
    }

    /**
     * Update a metadata field
     */
    fun updateMetadata(field: MetadataField) {
        val currentMetadata = _uiState.value.metadata
        val newMetadata = when (field) {
            is MetadataField.Title -> currentMetadata.copy(title = field.value)
            is MetadataField.Subtitle -> currentMetadata.copy(subtitle = field.value)
            is MetadataField.SortTitle -> currentMetadata.copy(sortTitle = field.value)
            is MetadataField.Summary -> currentMetadata.copy(summary = field.value)
            is MetadataField.Authors -> currentMetadata.copy(authors = field.value)
            is MetadataField.Series -> currentMetadata.copy(series = field.value)
            is MetadataField.SeriesIndex -> currentMetadata.copy(seriesIndex = field.value)
            is MetadataField.Rating -> currentMetadata.copy(rating = field.value)
            is MetadataField.Publisher -> currentMetadata.copy(publisher = field.value)
            is MetadataField.ISBN -> currentMetadata.copy(isbn = field.value)
            is MetadataField.Genres -> currentMetadata.copy(genres = field.value)
            is MetadataField.ReleaseDate -> currentMetadata.copy(releaseDate = field.value)
        }
        
        val hasChanges = newMetadata != _uiState.value.originalMetadata
        
        _uiState.value = _uiState.value.copy(
            metadata = newMetadata,
            hasChanges = hasChanges
        )
    }

    /**
     * Save the current metadata changes
     */
    fun saveMetadata() {
        val itemId = currentItemId ?: return
        val metadata = _uiState.value.metadata
        
        viewModelScope.launch {
            try {
                // Persist metadata changes to the database
                // This involves updating the metadata table for the current item
                metadataDao.updateMetadataForItem(itemId, metadata)
                
                _uiState.value = _uiState.value.copy(
                    originalMetadata = metadata,
                    hasChanges = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save metadata: ${e.message}"
                )
            }
        }
    }

    /**
     * Reset metadata to original values
     */
    fun resetMetadata() {
        val original = _uiState.value.originalMetadata
        if (original != null) {
            _uiState.value = _uiState.value.copy(
                metadata = original,
                hasChanges = false
            )
        }
    }
}