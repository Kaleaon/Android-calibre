package com.universalmedialibrary

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.universalmedialibrary.data.local.model.BookDetails
import com.universalmedialibrary.data.local.model.Library
import com.universalmedialibrary.services.CalibreImportForegroundService
import com.universalmedialibrary.ui.details.LibraryDetailsViewModel
import com.universalmedialibrary.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

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
) {
    val libraries by viewModel.libraries.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var dbFileUri by remember { mutableStateOf<Uri?>(null) }

    val rootFolderPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree(),
            onResult = { uri ->
                if (uri != null && dbFileUri != null) {
                    val intent =
                        Intent(context, CalibreImportForegroundService::class.java).apply {
                            putExtra(CalibreImportForegroundService.EXTRA_DB_PATH, dbFileUri.toString())
                            putExtra(CalibreImportForegroundService.EXTRA_ROOT_PATH, uri.toString())
                            // For now, we'll import into a placeholder library. A real implementation
                            // would have the user select or create a library first.
                            putExtra(CalibreImportForegroundService.EXTRA_LIBRARY_ID, 1L)
                        }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                    dbFileUri = null // Reset for next time
                }
            },
        )

    val dbFilePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = { uri ->
                if (uri != null) {
                    dbFileUri = uri
                    rootFolderPicker.launch(null)
                }
            },
        )

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
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Import Calibre Library") },
                            onClick = {
                                showMenu = false
                                // For now, let's just launch the DB file picker
                                dbFilePicker.launch(arrayOf("application/x-sqlite3", "application/octet-stream"))
                            },
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Library")
            }
        },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(libraries) { library ->
                LibraryCard(
                    library = library,
                    onClick = {
                        navController.navigate("library_details/${library.libraryId}")
                    },
                )
            }
        }

        if (showDialog) {
            AddLibraryDialog(
                onDismiss = { showDialog = false },
                onAdd = { name ->
                    viewModel.addLibrary(name, "BOOK", "/path/to/library")
                    showDialog = false
                },
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(bookDetails) { book ->
            BookCard(book = book)
        }
    }
}

@Composable
fun BookCard(book: BookDetails) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            PlaceholderCover(
                title = book.metadata.title,
                author = book.authorName,
            )
            Text(
                text = book.metadata.title,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
fun PlaceholderCover(
    title: String,
    author: String?,
) {
    val colors =
        listOf(
            Color(0xFFE57373),
            Color(0xFF81C784),
            Color(0xFF64B5F6),
            Color(0xFFF06292),
            Color(0xFF4DB6AC),
            Color(0xFFFFD54F),
        )
    val color = colors[title.hashCode() % colors.size]

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = Color.White,
            )
            if (author != null) {
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
fun AddLibraryDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Library") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Library Name") },
            )
        },
        confirmButton = {
            Button(
                onClick = { onAdd(name) },
                enabled = name.isNotBlank(),
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun LibraryCard(
    library: Library,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = getIconForLibraryType(library.type),
                contentDescription = library.type,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = library.name,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

private fun getIconForLibraryType(type: String): ImageVector =
    when (type.uppercase()) {
        "BOOK" -> Icons.Default.Book
        "MOVIE" -> Icons.Default.Movie
        "MUSIC" -> Icons.Default.MusicNote
        else -> Icons.Default.QuestionMark
    }
