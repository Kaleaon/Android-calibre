package com.universalmedialibrary.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PDFReaderViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PDFReaderUiState())
    val uiState: StateFlow<PDFReaderUiState> = _uiState.asStateFlow()

    private val _annotationMode = MutableStateFlow(false)
    val annotationMode: StateFlow<Boolean> = _annotationMode.asStateFlow()

    private val _selectedAnnotationTool = MutableStateFlow(AnnotationTool.HIGHLIGHT)
    val selectedAnnotationTool: StateFlow<AnnotationTool> = _selectedAnnotationTool.asStateFlow()

    private val _annotations = MutableStateFlow<List<Annotation>>(emptyList())
    val annotations: StateFlow<List<Annotation>> = _annotations.asStateFlow()

    data class PDFReaderUiState(
        val isLoading: Boolean = false,
        val isLoaded: Boolean = false,
        val error: String? = null,
        val documentTitle: String = "",
        val currentPage: Int = 1,
        val totalPages: Int = 0,
        val currentPageContent: String = "",
        val showSearch: Boolean = false,
        val searchQuery: String = "",
        val searchResults: List<SearchResult> = emptyList(),
        val zoomLevel: Float = 1.0f,
        val zoomMode: ZoomMode = ZoomMode.FIT_WIDTH
    )

    fun loadPDF(filePath: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Simulate loading a PDF - in real implementation, this would use a PDF library
                kotlinx.coroutines.delay(1500) // Simulate loading time
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoaded = true,
                    documentTitle = "Sample PDF Document",
                    totalPages = 25,
                    currentPage = 1,
                    currentPageContent = generateSamplePDFContent(1)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load PDF: ${e.message}"
                )
            }
        }
    }

    fun nextPage() {
        val currentState = _uiState.value
        if (currentState.currentPage < currentState.totalPages) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage + 1,
                currentPageContent = generateSamplePDFContent(currentState.currentPage + 1)
            )
        }
    }

    fun previousPage() {
        val currentState = _uiState.value
        if (currentState.currentPage > 1) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage - 1,
                currentPageContent = generateSamplePDFContent(currentState.currentPage - 1)
            )
        }
    }

    fun goToPage(pageNumber: Int) {
        val currentState = _uiState.value
        if (pageNumber in 1..currentState.totalPages) {
            _uiState.value = currentState.copy(
                currentPage = pageNumber,
                currentPageContent = generateSamplePDFContent(pageNumber)
            )
        }
    }

    fun toggleAnnotationMode() {
        _annotationMode.value = !_annotationMode.value
    }

    fun setAnnotationTool(tool: AnnotationTool) {
        _selectedAnnotationTool.value = tool
    }

    fun createAnnotation(annotation: Annotation) {
        val currentAnnotations = _annotations.value.toMutableList()
        currentAnnotations.add(annotation.copy(pageNumber = _uiState.value.currentPage))
        _annotations.value = currentAnnotations
    }

    fun deleteAnnotation(annotationId: Long) {
        _annotations.value = _annotations.value.filter { it.id != annotationId }
    }

    fun toggleSearch() {
        _uiState.value = _uiState.value.copy(
            showSearch = !_uiState.value.showSearch,
            searchQuery = if (!_uiState.value.showSearch) "" else _uiState.value.searchQuery
        )
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun performSearch() {
        val query = _uiState.value.searchQuery
        if (query.isNotEmpty()) {
            // Simulate search - in real implementation, this would search the PDF content
            val mockResults = listOf(
                SearchResult(
                    pageNumber = 1,
                    context = "Sample text containing '$query' found here",
                    position = androidx.compose.ui.geometry.Offset(100f, 200f)
                ),
                SearchResult(
                    pageNumber = 3,
                    context = "Another occurrence of '$query' on this page",
                    position = androidx.compose.ui.geometry.Offset(150f, 300f)
                ),
                SearchResult(
                    pageNumber = 7,
                    context = "Third match for '$query' in the document",
                    position = androidx.compose.ui.geometry.Offset(200f, 400f)
                )
            )
            
            _uiState.value = _uiState.value.copy(searchResults = mockResults)
        }
    }

    fun setZoomMode(mode: ZoomMode) {
        _uiState.value = _uiState.value.copy(zoomMode = mode)
    }

    fun setZoomLevel(level: Float) {
        _uiState.value = _uiState.value.copy(zoomLevel = level)
    }

    fun showPageSelector() {
        // TODO: Implement page selector dialog
    }

    fun showBookmarks() {
        // TODO: Implement bookmarks dialog
    }

    private fun generateSamplePDFContent(pageNumber: Int): String {
        return """
            Page $pageNumber Content
            
            This is a sample PDF page with various types of content that demonstrates the advanced PDF reader capabilities:
            
            Enhanced PDF Reading Features:
            
            1. Annotation Support
               • Highlighting text passages
               • Adding text notes and comments
               • Drawing freehand annotations
               • Strikethrough text
               • Color-coded annotations
            
            2. Advanced Navigation
               • Page-by-page navigation
               • Jump to specific page numbers
               • Bookmark important pages
               • Table of contents navigation
               • Thumbnail page preview
            
            3. Search Capabilities
               • Full-text search across the document
               • Search result highlighting
               • Navigate between search results
               • Case-sensitive search options
               • Regular expression support
            
            4. Display Options
               • Zoom controls (fit width, fit page, custom zoom)
               • Night mode for comfortable reading
               • Adjustable brightness
               • Rotation support
               • Single/continuous page view
            
            5. Text Selection and Export
               • Select and copy text
               • Export annotations
               • Share selected content
               • Print pages or selections
            
            Sample Content for Page $pageNumber:
            
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
            
            Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
            
            Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.
            
            Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.
            
            This PDF reader supports modern PDF features including:
            - Interactive forms
            - Embedded multimedia
            - Hyperlinks and bookmarks
            - Digital signatures
            - Password protection
            - Accessibility features
            
            [End of Page $pageNumber]
        """.trimIndent()
    }
}