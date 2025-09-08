package com.universalmedialibrary.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Configure your media library settings",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // API Settings Section
            item {
                SettingsCategoryHeader("API Settings")
            }

            items(getApiSettingsItems()) { item ->
                SettingsItem(
                    title = item.title,
                    subtitle = item.subtitle,
                    icon = item.icon,
                    onClick = { navController.navigate(item.route) }
                )
            }

            // Reader Settings Section
            item {
                SettingsCategoryHeader("Reader Settings")
            }

            items(getReaderSettingsItems()) { item ->
                SettingsItem(
                    title = item.title,
                    subtitle = item.subtitle,
                    icon = item.icon,
                    onClick = { navController.navigate(item.route) }
                )
            }

            // General Settings Section
            item {
                SettingsCategoryHeader("General")
            }

            items(getGeneralSettingsItems()) { item ->
                SettingsItem(
                    title = item.title,
                    subtitle = item.subtitle,
                    icon = item.icon,
                    onClick = { navController.navigate(item.route) }
                )
            }
        }

        // Error handling
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Show snackbar or toast
                // For now, we'll just clear the error after displaying
                viewModel.clearError()
            }
        }
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

data class SettingsMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)

private fun getApiSettingsItems(): List<SettingsMenuItem> = listOf(
    SettingsMenuItem(
        title = "Books APIs",
        subtitle = "Configure Open Library, Google Books, Hardcover",
        icon = Icons.Default.Book,
        route = "settings/apis/books"
    ),
    SettingsMenuItem(
        title = "Comics APIs",
        subtitle = "Configure ComicVine API",
        icon = Icons.Default.Collections,
        route = "settings/apis/comics"
    ),
    SettingsMenuItem(
        title = "Audiobooks APIs", 
        subtitle = "Configure OverDrive API",
        icon = Icons.Default.Headphones,
        route = "settings/apis/audiobooks"
    ),
    SettingsMenuItem(
        title = "Movies & TV APIs",
        subtitle = "Configure TMDB, OMDb APIs",
        icon = Icons.Default.Movie,
        route = "settings/apis/movies"
    ),
    SettingsMenuItem(
        title = "Music APIs",
        subtitle = "Configure MusicBrainz, Spotify APIs",
        icon = Icons.Default.MusicNote,
        route = "settings/apis/music"
    )
)

private fun getReaderSettingsItems(): List<SettingsMenuItem> = listOf(
    SettingsMenuItem(
        title = "Visual Controls",
        subtitle = "Font, colors, spacing, margins",
        icon = Icons.Default.Visibility,
        route = "settings/reader/visual"
    ),
    SettingsMenuItem(
        title = "Theming",
        subtitle = "Day/Night mode, custom themes",
        icon = Icons.Default.Palette,
        route = "settings/reader/theme"
    ),
    SettingsMenuItem(
        title = "Paging & Navigation",
        subtitle = "Touch controls, volume keys, gestures",
        icon = Icons.Default.TouchApp,
        route = "settings/reader/paging"
    ),
    SettingsMenuItem(
        title = "Auto-Scroll",
        subtitle = "Hands-free reading settings",
        icon = Icons.AutoMirrored.Filled.RotateRight,
        route = "settings/reader/autoscroll"
    ),
    SettingsMenuItem(
        title = "Text-to-Speech",
        subtitle = "Voice settings and controls",
        icon = Icons.Default.RecordVoiceOver,
        route = "settings/reader/tts"
    )
)

private fun getGeneralSettingsItems(): List<SettingsMenuItem> = listOf(
    SettingsMenuItem(
        title = "Security",
        subtitle = "Password protection, biometrics",
        icon = Icons.Default.Security,
        route = "settings/security"
    ),
    SettingsMenuItem(
        title = "Import & Export",
        subtitle = "Library management, backup settings",
        icon = Icons.Default.ImportExport,
        route = "settings/import"
    ),
    SettingsMenuItem(
        title = "Notifications",
        subtitle = "Reading reminders, updates",
        icon = Icons.Default.Notifications,
        route = "settings/notifications"
    ),
    SettingsMenuItem(
        title = "About",
        subtitle = "Version info, licenses, support",
        icon = Icons.Default.Info,
        route = "settings/about"
    )
)