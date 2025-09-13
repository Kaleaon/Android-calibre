package com.universalmedialibrary.ui.webfiction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.universalmedialibrary.services.webfiction.WebFictionService
import com.universalmedialibrary.services.webfiction.WebFictionSite
import com.universalmedialibrary.services.webfiction.WebFictionStory
import com.universalmedialibrary.ui.theme.PlexTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebFictionManagerScreen(
    navController: NavController,
    viewModel: WebFictionViewModel = hiltViewModel()
) {
    PlexTheme {
        val uiState by viewModel.uiState.collectAsState()
        var showAddDialog by remember { mutableStateOf(false) }
        var showSiteInfoDialog by remember { mutableStateOf(false) }
        var selectedSite by remember { mutableStateOf<WebFictionSite?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Web Fiction Manager",
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSiteInfoDialog = true }) {
                            Icon(Icons.Default.Info, contentDescription = "Supported Sites")
                        }
                        IconButton(onClick = { viewModel.checkAllForUpdates() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Check for Updates")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Story")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Status bar
                if (uiState.isLoading || uiState.isCheckingUpdates) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Error message
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearError() }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss")
                            }
                        }
                    }
                }

                // Update notifications
                if (uiState.storiesWithUpdates.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.NewReleases,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Updates Available",
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${uiState.storiesWithUpdates.size} stories have new chapters",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            TextButton(
                                onClick = { viewModel.downloadAllUpdates() }
                            ) {
                                Text("Download All")
                            }
                        }
                    }
                }

                // Stories list
                if (uiState.stories.isEmpty() && !uiState.isLoading) {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Web Fiction Stories",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Add stories from fanfiction sites to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showAddDialog = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Your First Story")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.stories) { story ->
                            WebFictionStoryCard(
                                story = story,
                                hasUpdates = story.id in uiState.storiesWithUpdates.map { it.id },
                                onStoryClick = { 
                                    navController.navigate("webfiction_story/${story.id}")
                                },
                                onUpdateClick = {
                                    viewModel.checkForUpdates(story)
                                },
                                onDownloadClick = {
                                    viewModel.downloadStory(story)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Add story dialog
        if (showAddDialog) {
            AddWebFictionDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { url ->
                    viewModel.addStoryFromUrl(url)
                    showAddDialog = false
                }
            )
        }

        // Supported sites dialog
        if (showSiteInfoDialog) {
            SupportedSitesDialog(
                onDismiss = { showSiteInfoDialog = false },
                onSiteClick = { site ->
                    selectedSite = site
                }
            )
        }
    }
}

@Composable
fun WebFictionStoryCard(
    story: WebFictionStory,
    hasUpdates: Boolean,
    onStoryClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onStoryClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cover image
            AsyncImage(
                model = story.coverUrl ?: "https://via.placeholder.com/80x120/2d3136/e5a00d?text=📖",
                contentDescription = "Story Cover",
                modifier = Modifier
                    .size(60.dp, 90.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Story info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (hasUpdates) {
                        Icon(
                            Icons.Default.NewReleases,
                            contentDescription = "Has Updates",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = story.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Site badge
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = story.site.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Chapter count
                    Text(
                        text = "${story.chapters.size}/${story.totalChapters} chapters",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status
                    Text(
                        text = story.status,
                        style = MaterialTheme.typography.bodySmall,
                        color = when (story.status.lowercase()) {
                            "complete" -> MaterialTheme.colorScheme.primary
                            "in-progress" -> Color(0xFF4CAF50)
                            "hiatus" -> Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                if (story.tags.isNotEmpty()) {
                    Row {
                        story.tags.take(3).forEach { tag ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Row {
                    OutlinedButton(
                        onClick = onUpdateClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Update")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onDownloadClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download")
                    }
                }
            }
        }
    }
}

@Composable
fun AddWebFictionDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }
    var isValidUrl by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Add Web Fiction Story",
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter the URL of a story from a supported fanfiction site:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = url,
                    onValueChange = { 
                        url = it
                        isValidUrl = it.isBlank() || it.startsWith("http")
                    },
                    label = { Text("Story URL") },
                    placeholder = { Text("https://archiveofourown.org/works/12345") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isValidUrl,
                    supportingText = if (!isValidUrl) {
                        { Text("Please enter a valid URL") }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Supported sites: AO3, FFN, Royal Road, WebNovel, Wattpad, and more",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(url) },
                enabled = url.isNotBlank() && isValidUrl
            ) {
                Text("Add Story")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SupportedSitesDialog(
    onDismiss: () -> Unit,
    onSiteClick: (WebFictionSite) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Supported Web Fiction Sites",
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(WebFictionSite.values().filter { it != WebFictionSite.GENERIC }) { site ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSiteClick(site) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = site.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = site.baseUrl,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}