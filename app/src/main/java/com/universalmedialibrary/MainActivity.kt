package com.universalmedialibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.universalmedialibrary.data.local.model.Library
import com.universalmedialibrary.services.CalibreImportService
import com.universalmedialibrary.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var calibreImportService: CalibreImportService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val libraries by viewModel.libraries.collectAsState()
            MainScreen(
                libraries = libraries,
                onImportClick = {
                    // For this POC, we will use the metadata.db file in the root of the repo
                    // And we will assume the library root is the repo root as well.
                    // In a real implementation, the user would select these paths.
                    val calibreDbPath = "metadata.db"
                    val libraryRootPath = "" // Empty string means repo root

                    // We need to create a dummy library to associate the imported books with.
                    // In a real app, the user would select a library.
                    val dummyLibraryId = 1L

                    calibreImportService.importCalibreDatabase(calibreDbPath, libraryRootPath, dummyLibraryId)
                }
            )
        }
    }
}

@Composable
fun MainScreen(libraries: List<Library>, onImportClick: suspend () -> Unit) {
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(libraries) { library ->
                LibraryListItem(library = library)
            }
        }
        Button(
            onClick = {
                scope.launch {
                    onImportClick()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Import Calibre DB")
        }
    }
}

@Composable
fun LibraryListItem(library: Library) {
    Text(text = "${library.name} (${library.type})")
}
