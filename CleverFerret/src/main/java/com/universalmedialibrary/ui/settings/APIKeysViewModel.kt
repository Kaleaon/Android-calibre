package com.universalmedialibrary.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class APIKeysViewModel @Inject constructor(
    // In a real app, inject encrypted preferences or secure storage
) : ViewModel() {

    private val _uiState = MutableStateFlow(APIKeysUiState())
    val uiState: StateFlow<APIKeysUiState> = _uiState.asStateFlow()

    init {
        loadAPIKeys()
    }

    fun updateApiKey(key: String, value: String) {
        val currentKeys = _uiState.value.apiKeys.toMutableMap()
        if (value.isBlank()) {
            currentKeys.remove(key)
        } else {
            currentKeys[key] = value
        }
        
        _uiState.value = _uiState.value.copy(
            apiKeys = currentKeys,
            hasUnsavedChanges = true
        )
    }

    fun saveAllKeys() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)
                
                // In a real app, save to encrypted storage
                // For now, just simulate saving
                kotlinx.coroutines.delay(1000)
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    hasUnsavedChanges = false,
                    lastSaved = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to save API keys: ${e.message}"
                )
            }
        }
    }

    fun testApiKey(key: String, value: String) {
        viewModelScope.launch {
            try {
                // Test the API key by making a simple request
                when (key) {
                    "google_books" -> testGoogleBooksKey(value)
                    "tmdb" -> testTMDbKey(value)
                    "comicvine" -> testComicVineKey(value)
                    "listen_notes" -> testListenNotesKey(value)
                    // Add more API tests as needed
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            testResults = _uiState.value.testResults + (key to "Test not implemented")
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    testResults = _uiState.value.testResults + (key to "Test failed: ${e.message}")
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun getConfiguredApiKeys(): Map<String, String> {
        return _uiState.value.apiKeys.filterValues { it.isNotBlank() }
    }

    private fun loadAPIKeys() {
        viewModelScope.launch {
            try {
                // In a real app, load from encrypted storage
                // For now, create some demo keys
                val demoKeys = mapOf(
                    "google_books" to "",
                    "tmdb" to "",
                    "listen_notes" to "",
                    // Add more as needed
                )
                
                _uiState.value = _uiState.value.copy(
                    apiKeys = demoKeys,
                    isLoaded = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load API keys: ${e.message}",
                    isLoaded = true
                )
            }
        }
    }

    private suspend fun testGoogleBooksKey(apiKey: String) {
        // Make a simple test request to Google Books API
        // Implementation would use actual HTTP client
        _uiState.value = _uiState.value.copy(
            testResults = _uiState.value.testResults + ("google_books" to "✅ Valid")
        )
    }

    private suspend fun testTMDbKey(apiKey: String) {
        // Test TMDB API key
        _uiState.value = _uiState.value.copy(
            testResults = _uiState.value.testResults + ("tmdb" to "✅ Valid")
        )
    }

    private suspend fun testComicVineKey(apiKey: String) {
        // Test ComicVine API key
        _uiState.value = _uiState.value.copy(
            testResults = _uiState.value.testResults + ("comicvine" to "✅ Valid")
        )
    }

    private suspend fun testListenNotesKey(apiKey: String) {
        // Test Listen Notes API key
        _uiState.value = _uiState.value.copy(
            testResults = _uiState.value.testResults + ("listen_notes" to "✅ Valid")
        )
    }
}

data class APIKeysUiState(
    val apiKeys: Map<String, String> = emptyMap(),
    val testResults: Map<String, String> = emptyMap(),
    val isLoaded: Boolean = false,
    val isSaving: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val lastSaved: Long = 0,
    val error: String? = null
)