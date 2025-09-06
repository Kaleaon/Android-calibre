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
import com.universalmedialibrary.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point of the application.
 * This activity hosts the Jetpack Compose UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

/**
 * Sets up the navigation graph for the application.
 */
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

/**
 * A screen that displays a list of all media libraries.
 *
 * @param navController The [NavController] for handling navigation events.
 * @param viewModel The [MainViewModel] for this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryListScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {
    val libraries by viewModel.libraries.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
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

        if (showDialog) {
            AddLibraryDialog(
                onDismiss = { showDialog = false },
                onAdd = { name ->
                    // TODO: Allow user to select type and path
                    viewModel.addLibrary(name, "BOOK", "/path/to/library")
                    showDialog = false
                }
            )
        }
    }
}

/**
 * A screen that displays the details of a selected library.
 *
 * @param viewModel The [LibraryDetailsViewModel] for this screen.
 */
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

/**
 * A card composable to display a single book.
 *
 * @param book The [BookDetails] to display.
 */
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

/**
 * A placeholder for a book cover, displaying the title and author on a colored background.
 *
 * @param title The title of the book.
 * @param author The author of the book.
 */
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

/**
 * A dialog for adding a new library.
 *
 * @param onDismiss Callback invoked when the dialog is dismissed.
 * @param onAdd Callback invoked when the user confirms adding the library, passing the name.
 */
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

/**
 * A card composable to display a single library.
 *
 * @param library The [Library] to display.
 * @param onClick Callback invoked when the card is clicked.
 */
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

/**
 * Returns an appropriate icon for a given library type.
 *
 * @param type The library type string (e.g., "BOOK").
 * @return An [ImageVector] for the icon.
 */
private fun getIconForLibraryType(type: String): ImageVector {
    return when (type.uppercase()) {
        "BOOK" -> Icons.Default.Book
        "MOVIE" -> Icons.Default.Movie
        "MUSIC" -> Icons.Default.MusicNote
        else -> Icons.Default.QuestionMark
    }
}
