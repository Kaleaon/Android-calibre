package com.universalmedialibrary.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Enhanced PDF reader with annotation capabilities
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedPDFReaderScreen(
    pdfFilePath: String,
    onBack: () -> Unit,
    viewModel: PDFReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val annotationMode by viewModel.annotationMode.collectAsState()
    val selectedAnnotationTool by viewModel.selectedAnnotationTool.collectAsState()
    val annotations by viewModel.annotations.collectAsState()
    
    var showAnnotationMenu by remember { mutableStateOf(false) }
    var showPageMenu by remember { mutableStateOf(false) }

    LaunchedEffect(pdfFilePath) {
        viewModel.loadPDF(pdfFilePath)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.documentTitle,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Annotation mode toggle
                    IconButton(
                        onClick = { viewModel.toggleAnnotationMode() }
                    ) {
                        Icon(
                            if (annotationMode) Icons.Default.EditOff else Icons.Default.Edit,
                            contentDescription = if (annotationMode) "Exit Annotation Mode" else "Enter Annotation Mode",
                            tint = if (annotationMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Annotation tools
                    if (annotationMode) {
                        IconButton(onClick = { showAnnotationMenu = true }) {
                            Icon(
                                when (selectedAnnotationTool) {
                                    AnnotationTool.HIGHLIGHT -> Icons.Default.FormatColorFill
                                    AnnotationTool.NOTE -> Icons.Default.Note
                                    AnnotationTool.DRAW -> Icons.Default.Draw
                                    AnnotationTool.STRIKETHROUGH -> Icons.Default.FormatStrikethrough
                                },
                                contentDescription = "Annotation Tools"
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showAnnotationMenu,
                            onDismissRequest = { showAnnotationMenu = false }
                        ) {
                            AnnotationTool.values().forEach { tool ->
                                DropdownMenuItem(
                                    text = { Text(tool.displayName) },
                                    onClick = {
                                        viewModel.setAnnotationTool(tool)
                                        showAnnotationMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(tool.icon, contentDescription = null)
                                    }
                                )
                            }
                        }
                    }
                    
                    // Search
                    IconButton(onClick = { viewModel.toggleSearch() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    
                    // Page navigation
                    IconButton(onClick = { showPageMenu = true }) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Page Navigation")
                    }
                    
                    DropdownMenu(
                        expanded = showPageMenu,
                        onDismissRequest = { showPageMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Go to Page...") },
                            onClick = { 
                                viewModel.showPageSelector()
                                showPageMenu = false 
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Pages, contentDescription = null)
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Bookmarks") },
                            onClick = { 
                                viewModel.showBookmarks()
                                showPageMenu = false 
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Bookmark, contentDescription = null)
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Zoom Fit Width") },
                            onClick = { 
                                viewModel.setZoomMode(ZoomMode.FIT_WIDTH)
                                showPageMenu = false 
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Zoom Fit Page") },
                            onClick = { 
                                viewModel.setZoomMode(ZoomMode.FIT_PAGE)
                                showPageMenu = false 
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // Search bar
                AnimatedVisibility(visible = uiState.showSearch) {
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::setSearchQuery,
                        onSearch = viewModel::performSearch,
                        onDismiss = { viewModel.toggleSearch() },
                        searchResults = uiState.searchResults
                    )
                }
                
                // Page navigation
                if (uiState.isLoaded && !uiState.isLoading) {
                    BottomAppBar {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.previousPage() },
                                enabled = uiState.currentPage > 1
                            ) {
                                Icon(Icons.Default.NavigateBefore, contentDescription = "Previous Page")
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Page ${uiState.currentPage} of ${uiState.totalPages}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                
                                LinearProgressIndicator(
                                    progress = { uiState.currentPage.toFloat() / uiState.totalPages.coerceAtLeast(1) },
                                    modifier = Modifier
                                        .width(120.dp)
                                        .padding(top = 4.dp),
                                )
                            }
                            
                            IconButton(
                                onClick = { viewModel.nextPage() },
                                enabled = uiState.currentPage < uiState.totalPages
                            ) {
                                Icon(Icons.Default.NavigateNext, contentDescription = "Next Page")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading PDF",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                uiState.isLoaded -> {
                    PDFPageView(
                        pageContent = uiState.currentPageContent,
                        annotations = annotations.filter { it.pageNumber == uiState.currentPage },
                        annotationMode = annotationMode,
                        annotationTool = selectedAnnotationTool,
                        onAnnotationCreated = viewModel::createAnnotation,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                else -> {
                    Text(
                        text = "No PDF loaded",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            // Floating annotation panel
            if (annotationMode && annotations.isNotEmpty()) {
                FloatingAnnotationPanel(
                    annotations = annotations.filter { it.pageNumber == uiState.currentPage },
                    onAnnotationClick = { annotation -> 
                        // Handle annotation click
                    },
                    onAnnotationDelete = viewModel::deleteAnnotation,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onDismiss: () -> Unit,
    searchResults: List<SearchResult>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search in PDF...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (searchResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${searchResults.size} results found",
                    style = MaterialTheme.typography.bodySmall
                )
                
                // Show first few results
                searchResults.take(3).forEach { result ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Page ${result.pageNumber}: ${result.context}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { 
                            // Navigate to search result
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PDFPageView(
    pageContent: String,
    annotations: List<Annotation>,
    annotationMode: Boolean,
    annotationTool: AnnotationTool,
    onAnnotationCreated: (Annotation) -> Unit,
    modifier: Modifier = Modifier
) {
    var drawingPath by remember { mutableStateOf<Path?>(null) }
    var currentPath by remember { mutableStateOf(Path()) }
    
    Box(
        modifier = modifier
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // PDF page content (placeholder)
        Column {
            Text(
                text = "PDF Page Content",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = pageContent,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }
        
        // Annotation overlay
        if (annotationMode) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(annotationTool) {
                        if (annotationTool == AnnotationTool.DRAW) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = Path()
                                    currentPath.moveTo(offset.x, offset.y)
                                },
                                onDrag = { change, _ ->
                                    currentPath.lineTo(change.position.x, change.position.y)
                                    drawingPath = Path().apply { addPath(currentPath) }
                                },
                                onDragEnd = {
                                    // Save annotation
                                    onAnnotationCreated(
                                        Annotation(
                                            id = System.currentTimeMillis(),
                                            type = AnnotationType.DRAWING,
                                            pageNumber = 1, // Would get actual page number
                                            content = "",
                                            position = Offset.Zero,
                                            path = drawingPath
                                        )
                                    )
                                    drawingPath = null
                                }
                            )
                        }
                    }
            ) {
                // Draw existing annotations
                annotations.forEach { annotation ->
                    when (annotation.type) {
                        AnnotationType.HIGHLIGHT -> {
                            drawRect(
                                color = Color.Yellow.copy(alpha = 0.3f),
                                topLeft = annotation.position,
                                size = annotation.size ?: androidx.compose.ui.geometry.Size(100f, 20f)
                            )
                        }
                        AnnotationType.DRAWING -> {
                            annotation.path?.let { path ->
                                drawPath(
                                    path = path,
                                    color = Color.Red,
                                    style = Stroke(width = 3.dp.toPx())
                                )
                            }
                        }
                        AnnotationType.NOTE -> {
                            drawCircle(
                                color = Color.Blue,
                                radius = 10.dp.toPx(),
                                center = annotation.position
                            )
                        }
                        AnnotationType.STRIKETHROUGH -> {
                            drawLine(
                                color = Color.Red,
                                start = annotation.position,
                                end = annotation.position.copy(x = annotation.position.x + 100f),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }
                }
                
                // Draw current drawing
                drawingPath?.let { path ->
                    drawPath(
                        path = path,
                        color = Color.Red,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingAnnotationPanel(
    annotations: List<Annotation>,
    onAnnotationClick: (Annotation) -> Unit,
    onAnnotationDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Annotations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            annotations.forEach { annotation ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAnnotationClick(annotation) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        when (annotation.type) {
                            AnnotationType.HIGHLIGHT -> Icons.Default.FormatColorFill
                            AnnotationType.NOTE -> Icons.Default.Note
                            AnnotationType.DRAWING -> Icons.Default.Draw
                            AnnotationType.STRIKETHROUGH -> Icons.Default.FormatStrikethrough
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = annotation.content.take(20) + if (annotation.content.length > 20) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = { onAnnotationDelete(annotation.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// Data classes and enums for PDF reader
enum class AnnotationTool(val displayName: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HIGHLIGHT("Highlight", Icons.Default.FormatColorFill),
    NOTE("Note", Icons.Default.Note),
    DRAW("Draw", Icons.Default.Draw),
    STRIKETHROUGH("Strikethrough", Icons.Default.FormatStrikethrough)
}

enum class AnnotationType {
    HIGHLIGHT, NOTE, DRAWING, STRIKETHROUGH
}

enum class ZoomMode {
    FIT_WIDTH, FIT_PAGE, CUSTOM
}

data class Annotation(
    val id: Long,
    val type: AnnotationType,
    val pageNumber: Int,
    val content: String,
    val position: Offset,
    val size: androidx.compose.ui.geometry.Size? = null,
    val path: Path? = null,
    val color: Color = Color.Yellow,
    val timestamp: Long = System.currentTimeMillis()
)

data class SearchResult(
    val pageNumber: Int,
    val context: String,
    val position: Offset
)