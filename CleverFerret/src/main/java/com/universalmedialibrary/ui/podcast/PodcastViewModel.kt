package com.universalmedialibrary.ui.podcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.services.podcast.Podcast
import com.universalmedialibrary.services.podcast.PodcastEpisode
import com.universalmedialibrary.services.podcast.PodcastSearchResult
import com.universalmedialibrary.services.podcast.PodcastService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastViewModel @Inject constructor(
    private val podcastService: PodcastService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PodcastUiState())
    val uiState: StateFlow<PodcastUiState> = _uiState.asStateFlow()

    init {
        loadPodcasts()
    }

    fun searchPodcasts(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, searchResults = emptyList())
            
            try {
                val results = podcastService.searchPodcasts(query)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = "Search failed: ${e.message}"
                )
            }
        }
    }

    fun subscribeFromSearchResult(searchResult: PodcastSearchResult) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val podcast = podcastService.subscribeToPodcast(searchResult.feedUrl)
                if (podcast != null) {
                    val updatedPodcasts = _uiState.value.podcasts + podcast
                    val allEpisodes = updatedPodcasts.flatMap { it.episodes }
                    
                    _uiState.value = _uiState.value.copy(
                        podcasts = updatedPodcasts,
                        allEpisodes = allEpisodes,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to subscribe to podcast"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Subscription failed: ${e.message}"
                )
            }
        }
    }

    fun addPodcastByFeedUrl(feedUrl: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val podcast = podcastService.subscribeToPodcast(feedUrl)
                if (podcast != null) {
                    val updatedPodcasts = _uiState.value.podcasts + podcast
                    val allEpisodes = updatedPodcasts.flatMap { it.episodes }
                    
                    _uiState.value = _uiState.value.copy(
                        podcasts = updatedPodcasts,
                        allEpisodes = allEpisodes,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Invalid RSS feed URL"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to add podcast: ${e.message}"
                )
            }
        }
    }

    fun unsubscribeFromPodcast(podcast: Podcast) {
        val updatedPodcasts = _uiState.value.podcasts.filter { it.id != podcast.id }
        val allEpisodes = updatedPodcasts.flatMap { it.episodes }
        val downloadedEpisodes = _uiState.value.downloadedEpisodes.filter { episode ->
            updatedPodcasts.any { it.episodes.contains(episode) }
        }
        
        _uiState.value = _uiState.value.copy(
            podcasts = updatedPodcasts,
            allEpisodes = allEpisodes,
            downloadedEpisodes = downloadedEpisodes
        )
    }

    fun refreshAllPodcasts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val updatedPodcasts = mutableListOf<Podcast>()
                
                _uiState.value.podcasts.forEach { podcast ->
                    val newEpisodes = podcastService.checkForNewEpisodes(podcast)
                    val updatedPodcast = if (newEpisodes.isNotEmpty()) {
                        podcast.copy(
                            episodes = podcast.episodes + newEpisodes,
                            totalEpisodes = podcast.totalEpisodes + newEpisodes.size,
                            lastUpdated = java.util.Date()
                        )
                    } else {
                        podcast
                    }
                    updatedPodcasts.add(updatedPodcast)
                }
                
                val allEpisodes = updatedPodcasts.flatMap { it.episodes }
                
                _uiState.value = _uiState.value.copy(
                    podcasts = updatedPodcasts,
                    allEpisodes = allEpisodes,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Refresh failed: ${e.message}"
                )
            }
        }
    }

    fun downloadEpisode(episode: PodcastEpisode) {
        viewModelScope.launch {
            try {
                // Find the podcast this episode belongs to
                val podcast = _uiState.value.podcasts.find { it.episodes.contains(episode) }
                if (podcast != null) {
                    val localPath = podcastService.downloadEpisode(episode, podcast.title)
                    if (localPath != null) {
                        val updatedEpisode = episode.copy(
                            isDownloaded = true,
                            localFilePath = localPath
                        )
                        
                        // Update the episode in the podcast
                        val updatedPodcast = podcast.copy(
                            episodes = podcast.episodes.map { 
                                if (it.id == episode.id) updatedEpisode else it 
                            }
                        )
                        
                        val updatedPodcasts = _uiState.value.podcasts.map {
                            if (it.id == podcast.id) updatedPodcast else it
                        }
                        
                        val allEpisodes = updatedPodcasts.flatMap { it.episodes }
                        val updatedDownloads = _uiState.value.downloadedEpisodes + updatedEpisode
                        
                        _uiState.value = _uiState.value.copy(
                            podcasts = updatedPodcasts,
                            allEpisodes = allEpisodes,
                            downloadedEpisodes = updatedDownloads
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to download episode"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Download failed: ${e.message}"
                )
            }
        }
    }

    fun deleteDownloadedEpisode(episode: PodcastEpisode) {
        viewModelScope.launch {
            try {
                // In a real app, delete the local file
                episode.localFilePath?.let { path ->
                    val file = java.io.File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                }
                
                // Update episode status
                val updatedEpisode = episode.copy(
                    isDownloaded = false,
                    localFilePath = null
                )
                
                // Update the episode in the podcast
                val updatedPodcasts = _uiState.value.podcasts.map { podcast ->
                    podcast.copy(
                        episodes = podcast.episodes.map { 
                            if (it.id == episode.id) updatedEpisode else it 
                        }
                    )
                }
                
                val allEpisodes = updatedPodcasts.flatMap { it.episodes }
                val updatedDownloads = _uiState.value.downloadedEpisodes.filter { it.id != episode.id }
                
                _uiState.value = _uiState.value.copy(
                    podcasts = updatedPodcasts,
                    allEpisodes = allEpisodes,
                    downloadedEpisodes = updatedDownloads
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete episode: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun loadPodcasts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // In a real app, load from database
                // For now, create some demo data
                val demoPodcasts = createDemoPodcasts()
                val allEpisodes = demoPodcasts.flatMap { it.episodes }
                val downloadedEpisodes = allEpisodes.filter { it.isDownloaded }
                
                _uiState.value = _uiState.value.copy(
                    podcasts = demoPodcasts,
                    allEpisodes = allEpisodes,
                    downloadedEpisodes = downloadedEpisodes,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load podcasts: ${e.message}"
                )
            }
        }
    }

    private fun createDemoPodcasts(): List<Podcast> {
        val now = java.util.Date()
        val yesterday = java.util.Date(now.time - 24 * 60 * 60 * 1000)
        val weekAgo = java.util.Date(now.time - 7 * 24 * 60 * 60 * 1000)
        
        return listOf(
            Podcast(
                id = "demo_1",
                title = "Tech Talk Daily",
                description = "Daily discussions about the latest in technology, AI, and innovation.",
                author = "Tech Media Network",
                feedUrl = "https://example.com/techtalk/feed.xml",
                websiteUrl = "https://example.com/techtalk",
                imageUrl = "https://via.placeholder.com/300x300/1565C0/ffffff?text=Tech+Talk",
                category = "Technology",
                isSubscribed = true,
                lastUpdated = now,
                totalEpisodes = 245,
                episodes = listOf(
                    PodcastEpisode(
                        id = "demo_ep_1",
                        title = "The Future of AI in 2025",
                        description = "Exploring the latest advancements in artificial intelligence and their impact on society.",
                        audioUrl = "https://example.com/audio/episode1.mp3",
                        duration = 2340, // 39 minutes
                        publishDate = now,
                        episodeNumber = 245
                    ),
                    PodcastEpisode(
                        id = "demo_ep_2", 
                        title = "Quantum Computing Breakthrough",
                        description = "Scientists achieve new milestone in quantum computing research.",
                        audioUrl = "https://example.com/audio/episode2.mp3",
                        duration = 1890, // 31.5 minutes
                        publishDate = yesterday,
                        episodeNumber = 244,
                        isDownloaded = true,
                        localFilePath = "/storage/podcasts/tech_talk/episode244.mp3"
                    )
                )
            ),
            Podcast(
                id = "demo_2",
                title = "Science Simplified",
                description = "Making complex scientific concepts accessible to everyone.",
                author = "Dr. Sarah Chen",
                feedUrl = "https://example.com/science/feed.xml",
                websiteUrl = "https://example.com/science",
                imageUrl = "https://via.placeholder.com/300x300/2C5F2D/ffffff?text=Science",
                category = "Science",
                isSubscribed = true,
                lastUpdated = weekAgo,
                totalEpisodes = 89,
                episodes = listOf(
                    PodcastEpisode(
                        id = "demo_ep_3",
                        title = "Understanding Black Holes",
                        description = "A deep dive into the mysteries of black holes and their role in the universe.",
                        audioUrl = "https://example.com/audio/episode3.mp3",
                        duration = 3240, // 54 minutes
                        publishDate = weekAgo,
                        episodeNumber = 89
                    )
                )
            ),
            Podcast(
                id = "demo_3",
                title = "Indie Developer Stories",
                description = "Interviews with independent game developers and their creative journeys.",
                author = "Gaming Insider",
                feedUrl = "https://example.com/gamedev/feed.xml",
                websiteUrl = "https://example.com/gamedev",
                imageUrl = "https://via.placeholder.com/300x300/7B1FA2/ffffff?text=Game+Dev",
                category = "Gaming",
                isSubscribed = true,
                lastUpdated = weekAgo,
                totalEpisodes = 156,
                episodes = listOf(
                    PodcastEpisode(
                        id = "demo_ep_4",
                        title = "Building a Pixel Art Platformer",
                        description = "How one developer created a successful indie game with retro aesthetics.",
                        audioUrl = "https://example.com/audio/episode4.mp3",
                        duration = 4200, // 70 minutes
                        publishDate = weekAgo,
                        episodeNumber = 156,
                        isDownloaded = true,
                        localFilePath = "/storage/podcasts/game_dev/episode156.mp3"
                    )
                )
            )
        )
    }
}

data class PodcastUiState(
    val podcasts: List<Podcast> = emptyList(),
    val allEpisodes: List<PodcastEpisode> = emptyList(),
    val downloadedEpisodes: List<PodcastEpisode> = emptyList(),
    val searchResults: List<PodcastSearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
)