package com.universalmedialibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.universalmedialibrary.data.local.model.Library
import com.universalmedialibrary.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val libraries by viewModel.libraries.collectAsState()
            LibraryListScreen(libraries = libraries)
        }
    }
}

@Composable
fun LibraryListScreen(libraries: List<Library>) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(libraries) { library ->
                LibraryListItem(library = library)
            }
        }
    }
}

@Composable
fun LibraryListItem(library: Library) {
    Text(text = "${library.name} (${library.type})")
}
