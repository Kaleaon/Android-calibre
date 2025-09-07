package com.universalmedialibrary.ui.reader

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.io.File

/**
 * ViewModel for the e-reader screen
 * Currently supports: TXT, MD files with basic text display
 * ePub support: Limited (displays basic message, full implementation pending)
 * 
 * TODO: Integrate epub4j or similar library for full ePub support with:
 * - Chapter navigation
 * - Proper formatting and styling  
 * - Image support
 * - Table of contents
 */
@HiltViewModel
class EReaderViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EReaderUiState())
    val uiState: StateFlow<EReaderUiState> = _uiState.asStateFlow()

    /**
     * Load a text file for reading
     */
    fun loadBook(context: Context, filePath: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val file = File(filePath)
                if (!file.exists()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "File not found: $filePath"
                    )
                    return@launch
                }
                
                val content = when (file.extension.lowercase()) {
                    "txt", "md" -> {
                        file.readText()
                    }
                    "epub" -> {
                        // Basic ePub handling - shows file info and guidance
                        """
                        ePub Reader - Limited Support
                        
                        File: ${file.name}
                        Size: ${file.length() / 1024} KB
                        
                        Current status: Basic ePub support is implemented but limited.
                        
                        Available features:
                        • File detection and basic info display
                        • Placeholder reading interface
                        
                        Planned features:
                        • Full ePub parsing and rendering
                        • Chapter navigation
                        • Proper text formatting
                        • Image and media support
                        • Bookmarks and reading progress
                        
                        To fully read this ePub file, use a dedicated ePub reader app or 
                        wait for the complete ePub implementation in a future update.
                        """.trimIndent()
                    }
                    else -> {
                        "Unsupported file format: ${file.extension}\n\nSupported formats:\n• TXT (Plain text)\n• MD (Markdown)\n• ePub (Limited support)"
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoaded = true,
                    bookTitle = file.nameWithoutExtension,
                    totalChapters = 1,
                    currentChapterIndex = 0,
                    currentChapterContent = content
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load book: ${e.message}"
                )
            }
        }
    }

    /**
     * Navigate to the next chapter
     */
    fun nextChapter() {
        // Placeholder for multi-chapter support
    }

    /**
     * Navigate to the previous chapter
     */
    fun previousChapter() {
        // Placeholder for multi-chapter support
    }
}

/**
 * UI state for the e-reader
 */
data class EReaderUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val bookTitle: String = "",
    val totalChapters: Int = 0,
    val currentChapterIndex: Int = 0,
    val currentChapterContent: String = "",
    val error: String? = null
)