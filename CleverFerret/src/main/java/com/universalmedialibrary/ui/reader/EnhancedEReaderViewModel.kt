package com.universalmedialibrary.ui.reader

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EnhancedEReaderViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EReaderUiState())
    val uiState: StateFlow<EReaderUiState> = _uiState.asStateFlow()

    private val _readerSettings = MutableStateFlow(ReaderSettings())
    val readerSettings: StateFlow<ReaderSettings> = _readerSettings.asStateFlow()

    private val _ttsState = MutableStateFlow(TTSState())
    val ttsState: StateFlow<TTSState> = _ttsState.asStateFlow()

    private var textToSpeech: TextToSpeech? = null

    data class EReaderUiState(
        val isLoading: Boolean = false,
        val isLoaded: Boolean = false,
        val error: String? = null,
        val bookTitle: String = "",
        val currentChapterIndex: Int = 0,
        val totalChapters: Int = 0,
        val currentChapterContent: String = "",
        val isBookmarked: Boolean = false
    )

    fun loadBook(context: Context, filePath: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Simulate loading a book - in real implementation, this would parse EPUB
                kotlinx.coroutines.delay(1000) // Simulate loading time
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoaded = true,
                    bookTitle = "Sample Book Title",
                    totalChapters = 10,
                    currentChapterIndex = 0,
                    currentChapterContent = generateSampleContent()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load book: ${e.message}"
                )
            }
        }
    }

    fun nextChapter() {
        val currentState = _uiState.value
        if (currentState.currentChapterIndex < currentState.totalChapters - 1) {
            _uiState.value = currentState.copy(
                currentChapterIndex = currentState.currentChapterIndex + 1,
                currentChapterContent = generateSampleContent(currentState.currentChapterIndex + 1)
            )
        }
    }

    fun previousChapter() {
        val currentState = _uiState.value
        if (currentState.currentChapterIndex > 0) {
            _uiState.value = currentState.copy(
                currentChapterIndex = currentState.currentChapterIndex - 1,
                currentChapterContent = generateSampleContent(currentState.currentChapterIndex - 1)
            )
        }
    }

    fun toggleBookmark() {
        _uiState.value = _uiState.value.copy(
            isBookmarked = !_uiState.value.isBookmarked
        )
    }

    // Font and display settings
    fun increaseFontSize() {
        val currentSettings = _readerSettings.value
        if (currentSettings.fontSize < 24f) {
            _readerSettings.value = currentSettings.copy(
                fontSize = currentSettings.fontSize + 1f
            )
        }
    }

    fun decreaseFontSize() {
        val currentSettings = _readerSettings.value
        if (currentSettings.fontSize > 10f) {
            _readerSettings.value = currentSettings.copy(
                fontSize = currentSettings.fontSize - 1f
            )
        }
    }

    fun toggleDarkMode() {
        _readerSettings.value = _readerSettings.value.copy(
            isDarkMode = !_readerSettings.value.isDarkMode
        )
    }

    fun toggleJustified() {
        _readerSettings.value = _readerSettings.value.copy(
            isJustified = !_readerSettings.value.isJustified
        )
    }

    // Text-to-Speech functionality
    fun initializeTTS(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    _ttsState.value = _ttsState.value.copy(isInitialized = false)
                } else {
                    _ttsState.value = _ttsState.value.copy(isInitialized = true)
                    setupTTSListener()
                }
            }
        }
    }

    private fun setupTTSListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _ttsState.value = _ttsState.value.copy(isPlaying = true, isPaused = false)
            }

            override fun onDone(utteranceId: String?) {
                _ttsState.value = _ttsState.value.copy(isPlaying = false, isPaused = false)
            }

            override fun onError(utteranceId: String?) {
                _ttsState.value = _ttsState.value.copy(isPlaying = false, isPaused = false)
            }
        })
    }

    fun startTTS(text: String) {
        if (_ttsState.value.isInitialized && textToSpeech != null) {
            textToSpeech?.setSpeechRate(_ttsState.value.speed)
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "chapter_${_uiState.value.currentChapterIndex}")
        }
    }

    fun pauseTTS() {
        textToSpeech?.stop()
        _ttsState.value = _ttsState.value.copy(isPlaying = false, isPaused = true)
    }

    fun stopTTS() {
        textToSpeech?.stop()
        _ttsState.value = _ttsState.value.copy(isPlaying = false, isPaused = false)
    }

    fun adjustTTSSpeed(delta: Float) {
        val newSpeed = (_ttsState.value.speed + delta).coerceIn(0.5f, 2.0f)
        _ttsState.value = _ttsState.value.copy(speed = newSpeed)
        
        if (_ttsState.value.isPlaying) {
            textToSpeech?.setSpeechRate(newSpeed)
        }
    }

    private fun generateSampleContent(chapterIndex: Int = 0): String {
        return """
            Chapter ${chapterIndex + 1}
            
            This is a sample chapter content for the enhanced EPUB reader. In a real implementation, this would be parsed from the actual EPUB file using a library like EPUBLib or similar.
            
            The enhanced reader includes many features:
            
            • Text-to-speech functionality with speed control
            • Customizable font size and type
            • Dark mode and light mode
            • Justified text alignment
            • Adjustable line height and margins
            • Bookmarking capabilities
            • Progress tracking
            
            This reader also supports multimedia content in EPUB3 format, including:
            - Embedded images
            - Audio narration
            - Video content
            - Interactive elements
            
            The navigation allows you to move between chapters easily, and the reading progress is tracked automatically.
            
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
            
            Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
            
            Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.
            
            This is chapter ${chapterIndex + 1} of ${_uiState.value.totalChapters}.
        """.trimIndent()
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.shutdown()
    }
}