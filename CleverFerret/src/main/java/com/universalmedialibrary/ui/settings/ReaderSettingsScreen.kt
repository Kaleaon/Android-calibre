package com.universalmedialibrary.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.universalmedialibrary.data.settings.ReaderSettings
import com.universalmedialibrary.data.settings.AutoScrollMode
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsScreen(
    navController: NavController,
    settingsType: String,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val readerSettings by viewModel.readerSettings.collectAsState()
    
    val (title, description) = when (settingsType) {
        "visual" -> "Visual Controls" to "Customize fonts, colors, and layout"
        "theme" -> "Theming" to "Day/Night mode and color themes"
        "paging" -> "Paging & Navigation" to "Touch controls and gestures"
        "autoscroll" -> "Auto-Scroll" to "Hands-free reading settings"
        "tts" -> "Text-to-Speech" to "Voice settings and controls"
        else -> "Reader Settings" to "Configure reading experience"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when (settingsType) {
                "visual" -> {
                    item {
                        VisualControlsSection(
                            settings = readerSettings,
                            onSettingsChange = viewModel::updateReaderSettings
                        )
                    }
                }
                "theme" -> {
                    item {
                        ThemeSection(
                            settings = readerSettings,
                            onSettingsChange = viewModel::updateReaderSettings
                        )
                    }
                }
                "paging" -> {
                    item {
                        PagingSection(
                            settings = readerSettings,
                            onSettingsChange = viewModel::updateReaderSettings
                        )
                    }
                }
                "autoscroll" -> {
                    item {
                        AutoScrollSection(
                            settings = readerSettings,
                            onSettingsChange = viewModel::updateReaderSettings
                        )
                    }
                }
                "tts" -> {
                    item {
                        TTSSection(
                            settings = readerSettings,
                            onSettingsChange = viewModel::updateReaderSettings
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VisualControlsSection(
    settings: ReaderSettings,
    onSettingsChange: (ReaderSettings) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsGroupCard("Font Settings") {
            // Font Size
            SliderSetting(
                title = "Font Size",
                value = settings.fontSize,
                range = 8f..32f,
                onValueChange = { onSettingsChange(settings.copy(fontSize = it)) },
                valueDisplay = "${settings.fontSize.toInt()}sp"
            )
            
            // Line Spacing
            SliderSetting(
                title = "Line Spacing",
                value = settings.lineSpacing,
                range = 1.0f..3.0f,
                onValueChange = { onSettingsChange(settings.copy(lineSpacing = it)) },
                valueDisplay = "${String.format(Locale.getDefault(), "%.1f", settings.lineSpacing)}x"
            )
            
            // Margins
            SliderSetting(
                title = "Margins",
                value = settings.margins,
                range = 0f..32f,
                onValueChange = { onSettingsChange(settings.copy(margins = it)) },
                valueDisplay = "${settings.margins.toInt()}dp"
            )
        }
        
        SettingsGroupCard("Text Style") {
            SwitchSetting(
                title = "Bold Text",
                subtitle = "Make all text bold",
                checked = settings.isBold,
                onCheckedChange = { onSettingsChange(settings.copy(isBold = it)) }
            )
            
            SwitchSetting(
                title = "Italic Text", 
                subtitle = "Make all text italic",
                checked = settings.isItalic,
                onCheckedChange = { onSettingsChange(settings.copy(isItalic = it)) }
            )
            
            SwitchSetting(
                title = "Justified Text",
                subtitle = "Align text to both margins",
                checked = settings.isJustified,
                onCheckedChange = { onSettingsChange(settings.copy(isJustified = it)) }
            )
        }
        
        SettingsGroupCard("Layout") {
            SwitchSetting(
                title = "Fade Edge Effect",
                subtitle = "Smooth edge transitions",
                checked = settings.fadeEdgeEnabled,
                onCheckedChange = { onSettingsChange(settings.copy(fadeEdgeEnabled = it)) }
            )
            
            SliderSetting(
                title = "Columns",
                value = settings.columnsCount.toFloat(),
                range = 1f..3f,
                onValueChange = { onSettingsChange(settings.copy(columnsCount = it.toInt())) },
                valueDisplay = "${settings.columnsCount} column${if (settings.columnsCount > 1) "s" else ""}"
            )
        }
    }
}

@Composable
fun ThemeSection(
    settings: ReaderSettings,
    onSettingsChange: (ReaderSettings) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsGroupCard("Theme Mode") {
            val themes = listOf("Light", "Dark", "System")
            themes.forEach { theme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settings.theme == theme,
                        onClick = { onSettingsChange(settings.copy(theme = theme)) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = theme)
                }
            }
        }
        
        SettingsGroupCard("Custom Colors") {
            SwitchSetting(
                title = "Dark Mode",
                subtitle = "Use dark theme colors",
                checked = settings.isDarkMode,
                onCheckedChange = { onSettingsChange(settings.copy(isDarkMode = it)) }
            )
        }
        
        SettingsGroupCard("Accessibility") {
            SwitchSetting(
                title = "High Contrast",
                subtitle = "Increase text contrast",
                checked = settings.highContrast,
                onCheckedChange = { onSettingsChange(settings.copy(highContrast = it)) }
            )
            
            SwitchSetting(
                title = "Dyslexia-Friendly Font",
                subtitle = "Use OpenDyslexic font",
                checked = settings.dyslexiaFont,
                onCheckedChange = { onSettingsChange(settings.copy(dyslexiaFont = it)) }
            )
        }
    }
}

@Composable
fun PagingSection(
    settings: ReaderSettings,
    onSettingsChange: (ReaderSettings) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsGroupCard("Touch Controls") {
            SwitchSetting(
                title = "Tap to Page",
                subtitle = "Tap screen edges to turn pages",
                checked = settings.tapToPagingEnabled,
                onCheckedChange = { onSettingsChange(settings.copy(tapToPagingEnabled = it)) }
            )
            
            SwitchSetting(
                title = "Swipe to Page",
                subtitle = "Swipe horizontally to turn pages",
                checked = settings.swipeToPageEnabled,
                onCheckedChange = { onSettingsChange(settings.copy(swipeToPageEnabled = it)) }
            )
        }
        
        SettingsGroupCard("Hardware Controls") {
            SwitchSetting(
                title = "Volume Key Paging",
                subtitle = "Use volume keys to turn pages",
                checked = settings.volumeKeyPaging,
                onCheckedChange = { onSettingsChange(settings.copy(volumeKeyPaging = it)) }
            )
        }
        
        SettingsGroupCard("Animation") {
            val animations = listOf("Slide", "Fade", "None")
            Column {
                Text(
                    text = "Page Turn Animation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                animations.forEach { animation ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.pageAnimation == animation,
                            onClick = { onSettingsChange(settings.copy(pageAnimation = animation)) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = animation)
                    }
                }
            }
        }
    }
}

@Composable
fun AutoScrollSection(
    settings: ReaderSettings,
    onSettingsChange: (ReaderSettings) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsGroupCard("Auto-Scroll") {
            SwitchSetting(
                title = "Enable Auto-Scroll",
                subtitle = "Hands-free reading mode",
                checked = settings.autoScrollEnabled,
                onCheckedChange = { onSettingsChange(settings.copy(autoScrollEnabled = it)) }
            )
            
            if (settings.autoScrollEnabled) {
                SliderSetting(
                    title = "Scroll Speed",
                    value = settings.autoScrollSpeed,
                    range = 10f..100f,
                    onValueChange = { onSettingsChange(settings.copy(autoScrollSpeed = it)) },
                    valueDisplay = "${settings.autoScrollSpeed.toInt()} px/s"
                )
                
                // Scroll Mode
                Column {
                    Text(
                        text = "Scroll Mode",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    AutoScrollMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.autoScrollMode == mode,
                                onClick = { onSettingsChange(settings.copy(autoScrollMode = mode)) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = mode.name.replace("_", " ").lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } })
                                when (mode) {
                                    AutoScrollMode.ROLLING_BLIND -> Text(
                                        text = "Smooth continuous scrolling",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    AutoScrollMode.BY_PIXEL -> Text(
                                        text = "Small discrete steps",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    AutoScrollMode.BY_LINE -> Text(
                                        text = "Line by line scrolling",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TTSSection(
    settings: ReaderSettings,
    onSettingsChange: (ReaderSettings) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsGroupCard("Text-to-Speech") {
            SwitchSetting(
                title = "Enable TTS",
                subtitle = "Text-to-speech reading",
                checked = settings.ttsEnabled,
                onCheckedChange = { onSettingsChange(settings.copy(ttsEnabled = it)) }
            )
            
            if (settings.ttsEnabled) {
                SliderSetting(
                    title = "Speech Speed",
                    value = settings.ttsSpeed,
                    range = 0.5f..2.0f,
                    onValueChange = { onSettingsChange(settings.copy(ttsSpeed = it)) },
                    valueDisplay = "${String.format(Locale.getDefault(), "%.1f", settings.ttsSpeed)}x"
                )
                
                SliderSetting(
                    title = "Speech Pitch",
                    value = settings.ttsPitch,
                    range = 0.5f..2.0f,
                    onValueChange = { onSettingsChange(settings.copy(ttsPitch = it)) },
                    valueDisplay = "${String.format(Locale.getDefault(), "%.1f", settings.ttsPitch)}x"
                )
            }
        }
    }
}

@Composable
fun SettingsGroupCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun SwitchSetting(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SliderSetting(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueDisplay: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = valueDisplay,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth()
        )
    }
}