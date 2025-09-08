package com.universalmedialibrary.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.data.settings.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val apiSettings = settingsRepository.apiSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ApiSettings()
    )

    val readerSettings = settingsRepository.readerSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReaderSettings()
    )

    val securitySettings = settingsRepository.securitySettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SecuritySettings()
    )

    val generalSettings = settingsRepository.generalSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GeneralSettings()
    )

    fun updateApiSettings(settings: ApiSettings) {
        viewModelScope.launch {
            try {
                settingsRepository.updateApiSettings(settings)
                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save API settings: ${e.message}"
                )
            }
        }
    }

    fun updateReaderSettings(settings: ReaderSettings) {
        viewModelScope.launch {
            try {
                settingsRepository.updateReaderSettings(settings)
                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save reader settings: ${e.message}"
                )
            }
        }
    }

    fun updateSecuritySettings(settings: SecuritySettings) {
        viewModelScope.launch {
            try {
                settingsRepository.updateSecuritySettings(settings)
                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save security settings: ${e.message}"
                )
            }
        }
    }

    fun updateGeneralSettings(settings: GeneralSettings) {
        viewModelScope.launch {
            try {
                settingsRepository.updateGeneralSettings(settings)
                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save general settings: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // API-specific update methods
    fun updateBookApiSettings(bookApis: BookApiSettings) {
        val current = apiSettings.value
        updateApiSettings(current.copy(bookApis = bookApis))
    }

    fun updateComicApiSettings(comicApis: ComicApiSettings) {
        val current = apiSettings.value
        updateApiSettings(current.copy(comicApis = comicApis))
    }

    fun updateAudiobookApiSettings(audiobookApis: AudiobookApiSettings) {
        val current = apiSettings.value
        updateApiSettings(current.copy(audiobookApis = audiobookApis))
    }

    fun updateMovieTvApiSettings(movieTvApis: MovieTvApiSettings) {
        val current = apiSettings.value
        updateApiSettings(current.copy(movieTvApis = movieTvApis))
    }

    fun updateMusicApiSettings(musicApis: MusicApiSettings) {
        val current = apiSettings.value
        updateApiSettings(current.copy(musicApis = musicApis))
    }
}

data class SettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)