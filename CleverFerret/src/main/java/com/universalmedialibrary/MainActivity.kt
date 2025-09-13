package com.universalmedialibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.universalmedialibrary.data.local.model.BookDetails
import com.universalmedialibrary.data.local.model.Library
import com.universalmedialibrary.services.CalibreImportForegroundService
import com.universalmedialibrary.ui.bookshelf.EnhancedBookshelfScreen
import com.universalmedialibrary.ui.details.LibraryDetailsViewModel
import com.universalmedialibrary.ui.main.MainViewModel
import androidx.compose.material.icons.filled.Settings
import com.universalmedialibrary.ui.settings.SettingsScreen
import com.universalmedialibrary.ui.settings.ApiSettingsScreen
import com.universalmedialibrary.ui.settings.ReaderSettingsScreen
import com.universalmedialibrary.ui.settings.SecuritySettingsScreen
import com.universalmedialibrary.ui.settings.AboutScreen
import com.universalmedialibrary.ui.metadata.MetadataEditorScreen
import com.universalmedialibrary.ui.theme.PlexTheme
import kotlin.math.absoluteValue
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlexTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "library_list") {
        composable("library_list") {
            LibraryListScreen(navController = navController)
        }
        composable("library_details/{libraryId}") { backStackEntry ->
            val libraryId = backStackEntry.arguments?.getString("libraryId")?.toLong() ?: 1L
            EnhancedBookshelfScreen(navController = navController, libraryId = libraryId)
        }
        composable("book_details/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toLong() ?: 0L
            BookDetailsScreen(bookId = bookId, navController = navController)
        }
        composable("metadata_editor/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toLong() ?: 0L
            MetadataEditorScreenWrapper(bookId = bookId, navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("settings/apis/{mediaType}") { backStackEntry ->
            val mediaType = backStackEntry.arguments?.getString("mediaType") ?: "books"
            ApiSettingsScreen(navController = navController, mediaType = mediaType)
        }
        composable("settings/reader/{settingsType}") { backStackEntry ->
            val settingsType = backStackEntry.arguments?.getString("settingsType") ?: "visual"
            ReaderSettingsScreen(navController = navController, settingsType = settingsType)
        }
        composable("settings/security") {
            SecuritySettingsScreen(navController = navController)
        }
        composable("settings/about") {
            AboutScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryListScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val libraries by viewModel.libraries.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var dbFileUri by remember { mutableStateOf<Uri?>(null) }
    var importStatus by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val rootFolderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri ->
            if (uri != null && dbFileUri != null) {
                isImporting = true
                importStatus = "Starting import..."
                val intent = Intent(context, CalibreImportForegroundService::class.java).apply {
                    putExtra(CalibreImportForegroundService.EXTRA_DB_PATH, dbFileUri.toString())
                    putExtra(CalibreImportForegroundService.EXTRA_ROOT_PATH, uri.toString())
                    // Create a default library for import
                    putExtra(CalibreImportForegroundService.EXTRA_LIBRARY_ID, 1L)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
                dbFileUri = null // Reset for next time
                
                // Show completion after a delay (in real app, this would be event-driven)
                // Using rememberCoroutineScope for proper coroutine handling
                coroutineScope.launch {
                    kotlinx.coroutines.delay(3000)
                    importStatus = "Import completed!"
                    kotlinx.coroutines.delay(2000)
                    isImporting = false
                    importStatus = ""
                }
            }
        }
    )

    val dbFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                dbFileUri = uri
                showImportDialog = false
                rootFolderPicker.launch(null)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Libraries") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Import Calibre Library") },
                            onClick = {
                                showMenu = false
                                showImportDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Refresh Libraries") },
                            onClick = {
                                showMenu = false
                                // Refresh functionality would go here
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Library")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(libraries) { library ->
                    LibraryCard(
                        library = library,
                        onClick = {
                            navController.navigate("library_details/${library.libraryId}")
                        }
                    )
                }
            }

            // Import status overlay
            if (isImporting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = importStatus,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        // Import Dialog
        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("Import Calibre Library") },
                text = {
                    Column {
                        Text("To import your Calibre library, you'll need to:")
                        Text("1. Select your Calibre metadata.db file")
                        Text("2. Select your Calibre library folder")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("This process may take several minutes depending on the size of your library.")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            dbFilePicker.launch(arrayOf("application/x-sqlite3", "application/octet-stream", "*/*"))
                        }
                    ) {
                        Text("Choose Database File")
                    }
                },
                dismissButton = {
                    Button(onClick = { showImportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showDialog) {
            AddLibraryDialog(
                onDismiss = { showDialog = false },
                onAdd = { name ->
                    viewModel.addLibrary(name, "BOOK", "/path/to/library")
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryDetailsScreen(viewModel: LibraryDetailsViewModel = hiltViewModel()) {
    val bookDetails by viewModel.bookDetails.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library Contents") },
                navigationIcon = {
                    // In a real implementation, you'd use NavController here
                    IconButton(onClick = { /* Navigate back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (bookDetails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No books found",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Import a Calibre library to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bookDetails) { book ->
                    BookCard(
                        book = book,
                        onClick = {
                            // Navigate to book details - for now just a placeholder
                            // navController.navigate("book_details/${book.mediaItem.itemId}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookCard(book: BookDetails, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            PlaceholderCover(
                title = book.metadata.title,
                author = book.authorName
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = book.metadata.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (book.authorName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.authorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (book.metadata.rating != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < (book.metadata.rating?.toInt() ?: 0)) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color(0xFFFFC107)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderCover(title: String, author: String?) {
    val colors = listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.errorContainer,
        MaterialTheme.colorScheme.surfaceVariant
    )
    val color = colors[title.hashCode().absoluteValue % colors.size]
    val contentColor = if (color == MaterialTheme.colorScheme.primaryContainer) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else if (color == MaterialTheme.colorScheme.secondaryContainer) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else if (color == MaterialTheme.colorScheme.tertiaryContainer) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else if (color == MaterialTheme.colorScheme.errorContainer) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = contentColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                color = contentColor,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            if (author != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = contentColor.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AddLibraryDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Library") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Library Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onAdd(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LibraryCard(library: Library, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getIconForLibraryType(library.type),
                contentDescription = library.type,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = library.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = library.type.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

private fun getIconForLibraryType(type: String): ImageVector {
    return when (type.uppercase()) {
        "BOOK" -> Icons.Default.Book
        "MOVIE" -> Icons.Default.Movie
        "MUSIC" -> Icons.Default.MusicNote
        else -> Icons.Default.QuestionMark
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(bookId: Long, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            navController.navigate("metadata_editor/$bookId")
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Metadata")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Book Details for ID: $bookId",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "This is a placeholder for the book details screen. In a complete implementation, this would show full book metadata, cover image, and reading options.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun MetadataEditorScreenWrapper(bookId: Long, navController: NavController) {
    MetadataEditorScreen(
        itemId = bookId,
        onSave = {
            // In a real implementation, this would save to the database
            navController.navigateUp()
        },
        onCancel = {
            navController.navigateUp()
        }
    )
}
