package com.universalmedialibrary.ui.metadata

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetadataEditorScreen(
    title: String = "",
    author: String = "",
    series: String = "",
    publisher: String = "",
    isbn: String = "",
    summary: String = "",
    onSave: (MetadataEditorData) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var titleState by remember { mutableStateOf(TextFieldValue(title)) }
    var authorState by remember { mutableStateOf(TextFieldValue(author)) }
    var seriesState by remember { mutableStateOf(TextFieldValue(series)) }
    var publisherState by remember { mutableStateOf(TextFieldValue(publisher)) }
    var isbnState by remember { mutableStateOf(TextFieldValue(isbn)) }
    var summaryState by remember { mutableStateOf(TextFieldValue(summary)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Metadata") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSave(
                                MetadataEditorData(
                                    title = titleState.text,
                                    author = authorState.text,
                                    series = seriesState.text,
                                    publisher = publisherState.text,
                                    isbn = isbnState.text,
                                    summary = summaryState.text
                                )
                            )
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titleState,
                onValueChange = { titleState = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = authorState,
                onValueChange = { authorState = it },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = seriesState,
                onValueChange = { seriesState = it },
                label = { Text("Series") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = publisherState,
                onValueChange = { publisherState = it },
                label = { Text("Publisher") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = isbnState,
                onValueChange = { isbnState = it },
                label = { Text("ISBN") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = summaryState,
                onValueChange = { summaryState = it },
                label = { Text("Summary") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Metadata Preview",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (titleState.text.isNotBlank()) {
                        Text(
                            text = "Title: ${titleState.text}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    if (authorState.text.isNotBlank()) {
                        Text(
                            text = "Author: ${authorState.text}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    if (seriesState.text.isNotBlank()) {
                        Text(
                            text = "Series: ${seriesState.text}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

data class MetadataEditorData(
    val title: String,
    val author: String,
    val series: String,
    val publisher: String,
    val isbn: String,
    val summary: String
)

@Preview(showBackground = true)
@Composable
fun MetadataEditorScreenPreview() {
    MaterialTheme {
        MetadataEditorScreen(
            title = "Sample Book Title",
            author = "Sample Author",
            series = "Sample Series",
            publisher = "Sample Publisher",
            isbn = "978-1234567890",
            summary = "This is a sample summary for the book metadata editor preview."
        )
    }
}