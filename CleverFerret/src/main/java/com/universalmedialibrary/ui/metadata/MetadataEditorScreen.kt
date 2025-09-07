package com.universalmedialibrary.ui.metadata

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Main metadata editor screen that adapts to different screen sizes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetadataEditorScreen(
    itemId: Long,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    viewModel: MetadataEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    LaunchedEffect(itemId) {
        viewModel.loadMetadata(itemId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Metadata") },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveMetadata()
                            onSave()
                        }
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isTablet) {
            TabletLayout(
                uiState = uiState,
                onMetadataChange = viewModel::updateMetadata,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            PhoneLayout(
                uiState = uiState,
                onMetadataChange = viewModel::updateMetadata,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

/**
 * Layout for tablets - two column with static cover and scrollable fields
 */
@Composable
private fun TabletLayout(
    uiState: MetadataEditorUiState,
    onMetadataChange: (MetadataField) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(16.dp)) {
        // Left column - Cover and buttons
        Column(
            modifier = Modifier
                .width(200.dp)
                .padding(end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlaceholderCover(
                title = uiState.metadata.title,
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )
        }
        
        // Right column - Scrollable fields
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            MetadataFields(
                metadata = uiState.metadata,
                originalMetadata = uiState.originalMetadata,
                onMetadataChange = onMetadataChange
            )
        }
    }
}

/**
 * Layout for phones - single scrollable column
 */
@Composable
private fun PhoneLayout(
    uiState: MetadataEditorUiState,
    onMetadataChange: (MetadataField) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cover at top
        PlaceholderCover(
            title = uiState.metadata.title,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )
        
        // Metadata fields
        MetadataFields(
            metadata = uiState.metadata,
            originalMetadata = uiState.originalMetadata,
            onMetadataChange = onMetadataChange
        )
    }
}

/**
 * All metadata input fields
 */
@Composable
private fun MetadataFields(
    metadata: EditableMetadata,
    originalMetadata: EditableMetadata?,
    onMetadataChange: (MetadataField) -> Unit
) {
    // Title
    MetadataTextField(
        label = "Title",
        value = metadata.title,
        originalValue = originalMetadata?.title,
        onValueChange = { onMetadataChange(MetadataField.Title(it)) }
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Subtitle
    MetadataTextField(
        label = "Subtitle",
        value = metadata.subtitle,
        originalValue = originalMetadata?.subtitle,
        onValueChange = { onMetadataChange(MetadataField.Subtitle(it)) }
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Sort Title
    MetadataTextField(
        label = "Sort Title",
        value = metadata.sortTitle,
        originalValue = originalMetadata?.sortTitle,
        onValueChange = { onMetadataChange(MetadataField.SortTitle(it)) }
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Summary
    MetadataTextField(
        label = "Summary",
        value = metadata.summary,
        originalValue = originalMetadata?.summary,
        onValueChange = { onMetadataChange(MetadataField.Summary(it)) },
        singleLine = false,
        minLines = 3
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Authors (chip-based input)
    AuthorsChipInput(
        authors = metadata.authors,
        originalAuthors = originalMetadata?.authors,
        onAuthorsChange = { onMetadataChange(MetadataField.Authors(it)) }
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Series
    MetadataTextField(
        label = "Series",
        value = metadata.series,
        originalValue = originalMetadata?.series,
        onValueChange = { onMetadataChange(MetadataField.Series(it)) }
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Series Index
    MetadataTextField(
        label = "Series Index",
        value = metadata.seriesIndex?.toString() ?: "",
        originalValue = originalMetadata?.seriesIndex?.toString(),
        onValueChange = { 
            val index = it.toIntOrNull()
            onMetadataChange(MetadataField.SeriesIndex(index))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Rating
    RatingInput(
        rating = metadata.rating,
        originalRating = originalMetadata?.rating,
        onRatingChange = { onMetadataChange(MetadataField.Rating(it)) }
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Publisher
    MetadataTextField(
        label = "Publisher",
        value = metadata.publisher,
        originalValue = originalMetadata?.publisher,
        onValueChange = { onMetadataChange(MetadataField.Publisher(it)) }
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // ISBN
    MetadataTextField(
        label = "ISBN",
        value = metadata.isbn,
        originalValue = originalMetadata?.isbn,
        onValueChange = { onMetadataChange(MetadataField.ISBN(it)) }
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Genres/Tags (chip-based input)
    GenresChipInput(
        genres = metadata.genres,
        originalGenres = originalMetadata?.genres,
        onGenresChange = { onMetadataChange(MetadataField.Genres(it)) }
    )
}

/**
 * Standard text field with before/after display for changed values
 */
@Composable
private fun MetadataTextField(
    label: String,
    value: String,
    originalValue: String?,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Show original value if different
        if (originalValue != null && originalValue != value && originalValue.isNotBlank()) {
            Text(
                text = originalValue,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Placeholder cover component
 */
@Composable
private fun PlaceholderCover(
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title.take(3).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Star rating input component
 */
@Composable
private fun RatingInput(
    rating: Int?,
    originalRating: Int?,
    onRatingChange: (Int?) -> Unit
) {
    Column {
        Text(
            text = "Rating",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row {
            repeat(5) { index ->
                val starRating = index + 1
                IconButton(
                    onClick = {
                        // Toggle rating - if same rating clicked, clear it
                        onRatingChange(if (rating == starRating) null else starRating)
                    }
                ) {
                    Icon(
                        imageVector = if (rating != null && starRating <= rating) {
                            Icons.Filled.Star
                        } else {
                            Icons.Outlined.Star
                        },
                        contentDescription = "Rate $starRating stars",
                        tint = if (rating != null && starRating <= rating) {
                            Color(0xFFFFD700) // Gold
                        } else {
                            Color.Gray
                        }
                    )
                }
            }
        }
        
        // Show original rating if different
        if (originalRating != null && originalRating != rating) {
            Text(
                text = "Original: $originalRating stars",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}