package com.universalmedialibrary.ui.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.io.File

/**
 * ViewModel for the audio player
 */
@HiltViewModel
class AudioPlayerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AudioPlayerUiState())
    val uiState: StateFlow<AudioPlayerUiState> = _uiState.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var updatePositionJob: kotlinx.coroutines.Job? = null

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        duration = exoPlayer?.duration ?: 0L
                    )
                    startPositionUpdates()
                }
                Player.STATE_ENDED -> {
                    _uiState.value = _uiState.value.copy(isPlaying = false)
                    stopPositionUpdates()
                }
                Player.STATE_BUFFERING -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
            if (isPlaying) {
                startPositionUpdates()
            } else {
                stopPositionUpdates()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Playback error: ${error.message}"
            )
        }
    }

    /**
     * Load an audio file for playback
     */
    fun loadAudio(context: Context, filePath: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // Release any existing player
                exoPlayer?.release()
                
                // Create new ExoPlayer
                exoPlayer = ExoPlayer.Builder(context).build().apply {
                    addListener(playerListener)
                }
                
                // Extract basic metadata from file
                val file = File(filePath)
                val fileName = file.nameWithoutExtension
                
                // Create MediaItem
                val mediaItem = MediaItem.fromUri(file.toURI().toString())
                
                exoPlayer?.apply {
                    setMediaItem(mediaItem)
                    prepare()
                }
                
                _uiState.value = _uiState.value.copy(
                    title = fileName,
                    artist = "", // TODO: Extract from metadata
                    isLoaded = true
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load audio: ${e.message}"
                )
            }
        }
    }

    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
    }

    /**
     * Seek to specific position
     */
    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    /**
     * Skip to previous track (placeholder for future playlist support)
     */
    fun skipToPrevious() {
        // TODO: Implement playlist navigation
        exoPlayer?.seekTo(0)
    }

    /**
     * Skip to next track (placeholder for future playlist support)
     */
    fun skipToNext() {
        // TODO: Implement playlist navigation
        exoPlayer?.seekTo(exoPlayer?.duration ?: 0L)
    }

    /**
     * Start updating playback position
     */
    private fun startPositionUpdates() {
        stopPositionUpdates()
        updatePositionJob = viewModelScope.launch {
            while (exoPlayer?.isPlaying == true) {
                _uiState.value = _uiState.value.copy(
                    currentPosition = exoPlayer?.currentPosition ?: 0L
                )
                kotlinx.coroutines.delay(1000) // Update every second
            }
        }
    }

    /**
     * Stop updating playback position
     */
    private fun stopPositionUpdates() {
        updatePositionJob?.cancel()
        updatePositionJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPositionUpdates()
        exoPlayer?.release()
    }
}

/**
 * UI state for the audio player
 */
data class AudioPlayerUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val isPlaying: Boolean = false,
    val title: String = "Unknown Track",
    val artist: String = "",
    val duration: Long = 0L,
    val currentPosition: Long = 0L,
    val error: String? = null
)