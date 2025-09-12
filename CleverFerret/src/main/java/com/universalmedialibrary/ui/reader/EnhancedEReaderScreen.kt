package com.universalmedialibrary.ui.reader

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import java.util.Locale
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.*
import com.universalmedialibrary.data.settings.ReaderSettings

/**
 * Enhanced E-reader screen with advanced features including TTS
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedEReaderScreen(
    bookFilePath: String,
    onBack: () -> Unit,
    viewModel: EnhancedEReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val readerSettings by viewModel.readerSettings.collectAsState()
    val ttsState by viewModel.ttsState.collectAsState()
    val context = LocalContext.current
    
    var showSettingsMenu by remember { mutableStateOf(false) }
    var showFontMenu by remember { mutableStateOf(false) }

    LaunchedEffect(bookFilePath) {
        viewModel.loadBook(context, bookFilePath)
        viewModel.initializeTTS(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.bookTitle,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // TTS Control
                    IconButton(
                        onClick = { 
                            if (ttsState.isPlaying) {
                                viewModel.pauseTTS()
                            } else {
                                viewModel.startTTS(uiState.currentChapterContent)
                            }
                        }
                    ) {
                        Icon(
                            if (ttsState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (ttsState.isPlaying) "Pause TTS" else "Start TTS"
                        )
                    }
                    
                    // Font settings
                    IconButton(onClick = { showFontMenu = true }) {
                        Icon(Icons.Default.FormatSize, contentDescription = "Font Settings")
                    }
                    
                    DropdownMenu(
                        expanded = showFontMenu,
                        onDismissRequest = { showFontMenu = false }
                    ) {
                        Text(
                            "Font Size",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(16.dp, 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.decreaseFontSize() }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            
                            Text(
                                "${readerSettings.fontSize.toInt()}sp",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            
                            IconButton(onClick = { viewModel.increaseFontSize() }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                        
                        HorizontalDivider()
                        
                        DropdownMenuItem(
                            text = { Text("Dark Mode") },
                            onClick = { viewModel.toggleDarkMode() },
                            trailingIcon = {
                                Switch(
                                    checked = readerSettings.isDarkMode,
                                    onCheckedChange = { viewModel.toggleDarkMode() }
                                )
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Justified Text") },
                            onClick = { viewModel.toggleJustified() },
                            trailingIcon = {
                                Switch(
                                    checked = readerSettings.isJustified,
                                    onCheckedChange = { viewModel.toggleJustified() }
                                )
                            }
                        )
                    }
                    
                    // Settings
                    IconButton(onClick = { showSettingsMenu = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    
                    DropdownMenu(
                        expanded = showSettingsMenu,
                        onDismissRequest = { showSettingsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Line Height") },
                            onClick = { /* TODO: Implement line height adjustment */ }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Margins") },
                            onClick = { /* TODO: Implement margin adjustment */ }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Page Turn Animation") },
                            onClick = { /* TODO: Implement page turn settings */ }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Bookmark") },
                            onClick = { viewModel.toggleBookmark() },
                            leadingIcon = {
                                Icon(
                                    if (uiState.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark"
                                )
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // TTS Progress Bar
                AnimatedVisibility(visible = ttsState.isPlaying) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Text-to-Speech",
                                style = MaterialTheme.typography.titleSmall
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.pauseTTS() }) {
                                    Icon(Icons.Default.Pause, contentDescription = "Pause")
                                }
                                
                                IconButton(onClick = { viewModel.stopTTS() }) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Text(
                                    "Speed: ${String.format(Locale.getDefault(), "%.1f", ttsState.speed)}x",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Button(
                                    onClick = { viewModel.adjustTTSSpeed(-0.1f) },
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("-", fontSize = 12.sp)
                                }
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                Button(
                                    onClick = { viewModel.adjustTTSSpeed(0.1f) },
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                
                // Navigation Bar
                if (uiState.isLoaded && !uiState.isLoading) {
                    BottomAppBar {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.previousChapter() },
                                enabled = uiState.currentChapterIndex > 0
                            ) {
                                Icon(Icons.Default.NavigateBefore, contentDescription = "Previous Chapter")
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Chapter ${uiState.currentChapterIndex + 1} of ${uiState.totalChapters}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                
                                LinearProgressIndicator(
                                    progress = { (uiState.currentChapterIndex + 1f) / uiState.totalChapters.coerceAtLeast(1) },
                                    modifier = Modifier
                                        .width(120.dp)
                                        .padding(top = 4.dp),
                                )
                            }
                            
                            IconButton(
                                onClick = { viewModel.nextChapter() },
                                enabled = uiState.currentChapterIndex < uiState.totalChapters - 1
                            ) {
                                Icon(Icons.Default.NavigateNext, contentDescription = "Next Chapter")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading book",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                uiState.isLoaded -> {
                    EnhancedReaderContent(
                        content = uiState.currentChapterContent,
                        settings = readerSettings,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                else -> {
                    Text(
                        text = "No book loaded",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

/**
 * Enhanced reading content with customizable formatting
 */
@Composable
private fun EnhancedReaderContent(
    content: String,
    settings: ReaderSettings,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (settings.isDarkMode) Color(0xFF1A1A1A) else Color.White
    val textColor = if (settings.isDarkMode) Color(0xFFE0E0E0) else Color.Black
    
    Column(
        modifier = modifier
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = settings.margins.dp,
                vertical = settings.margins.dp
            )
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = settings.fontSize.sp,
                lineHeight = (settings.fontSize * settings.lineSpacing).sp,
                fontFamily = if (settings.dyslexiaFont) FontFamily.Monospace else FontFamily.Default,
                color = textColor,
                textAlign = if (settings.isJustified) TextAlign.Justify else TextAlign.Start,
                fontWeight = if (settings.isBold) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}