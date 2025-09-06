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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.universalmedialibrary.data.local.model.BookDetails
import com.universalmedialibrary.data.local.model.Library
import com.universalmedialibrary.ui.details.LibraryDetailsViewModel
import com.universalmedialibrary.ui.main.ContentImportViewModel
import com.universalmedialibrary.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.platform.LocalContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
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
        composable("library_details/{libraryId}") {
            LibraryDetailsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryListScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
    importViewModel: ContentImportViewModel = hiltViewModel()
) {
    val libraries by viewModel.libraries.collectAsState()
    var showAddLibraryDialog by remember { mutableStateOf(false) }
    var showUrlImportDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Libraries") },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Import from URL") },
                            onClick = {
                                showMenu = false
                                showUrlImportDialog = true
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddLibraryDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Library")
            }
        }
    ) { paddingValues ->
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

        if (showAddLibraryDialog) {
            AddLibraryDialog(
                onDismiss = { showAddLibraryDialog = false },
                onAdd = { name ->
                    viewModel.addLibrary(name, "BOOK", "/path/to/library")
                    showAddLibraryDialog = false
                }
            )
        }

        if (showUrlImportDialog) {
            ContentImportDialog(
                onDismiss = { showUrlImportDialog = false },
                onImport = { url ->
                    val targetLibraryId = libraries.firstOrNull()?.libraryId ?: 1L
                    importViewModel.importFromUrl(url, targetLibraryId)
                    showUrlImportDialog = false
                }
            )
        }
    }
}

@Composable
fun LibraryDetailsScreen(viewModel: LibraryDetailsViewModel = hiltViewModel()) {
    val bookDetails by viewModel.bookDetails.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(bookDetails) { book ->
            BookCard(book = book)
        }
    }
}

@Composable
fun BookCard(book: BookDetails) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            PlaceholderCover(
                title = book.metadata.title,
                author = book.authorName
            )
            Text(
                text = book.metadata.title,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun PlaceholderCover(title: String, author: String?) {
    val colors = listOf(
        Color(0xFFE57373), Color(0xFF81C784), Color(0xFF64B5F6),
        Color(0xFFF06292), Color(0xFF4DB6AC), Color(0xFFFFD54F)
    )
    val color = colors[title.hashCode() % colors.size]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            if (author != null) {
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.White
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
fun ContentImportDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import from URL") },
        text = {
            TextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Enter URL") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onImport(url) },
                enabled = url.isNotBlank()
            ) {
                Text("Import")
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getIconForLibraryType(library.type),
                contentDescription = library.type,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = library.name,
                style = MaterialTheme.typography.titleMedium
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
