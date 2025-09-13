package com.universalmedialibrary.ui.webfiction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.services.webfiction.WebFictionService
import com.universalmedialibrary.services.webfiction.WebFictionStory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebFictionViewModel @Inject constructor(
    private val webFictionService: WebFictionService
) : ViewModel() {

    private val _uiState = MutableStateFlow(WebFictionUiState())
    val uiState: StateFlow<WebFictionUiState> = _uiState.asStateFlow()

    init {
        loadStories()
    }

    fun addStoryFromUrl(url: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val story = webFictionService.extractStoryFromUrl(url)
                if (story != null) {
                    // Download all chapters
                    val chapters = webFictionService.downloadAllChapters(story)
                    val completeStory = story.copy(chapters = chapters)
                    
                    // Add to local storage (in real app, save to database)
                    val updatedStories = _uiState.value.stories + completeStory
                    _uiState.value = _uiState.value.copy(
                        stories = updatedStories,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to extract story from URL. Please check the URL and try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error adding story: ${e.message}"
                )
            }
        }
    }

    fun checkForUpdates(story: WebFictionStory) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingUpdates = true)
            
            try {
                val newChapters = webFictionService.checkForUpdates(story)
                if (newChapters.isNotEmpty()) {
                    val updatedStory = story.copy(chapters = story.chapters + newChapters)
                    val updatedStories = _uiState.value.stories.map { 
                        if (it.id == story.id) updatedStory else it 
                    }
                    val updatedStoriesWithUpdates = _uiState.value.storiesWithUpdates + updatedStory
                    
                    _uiState.value = _uiState.value.copy(
                        stories = updatedStories,
                        storiesWithUpdates = updatedStoriesWithUpdates,
                        isCheckingUpdates = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isCheckingUpdates = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCheckingUpdates = false,
                    error = "Error checking for updates: ${e.message}"
                )
            }
        }
    }

    fun checkAllForUpdates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingUpdates = true)
            
            val updatesFound = mutableListOf<WebFictionStory>()
            val updatedStories = mutableListOf<WebFictionStory>()
            
            try {
                _uiState.value.stories.forEach { story ->
                    val newChapters = webFictionService.checkForUpdates(story)
                    if (newChapters.isNotEmpty()) {
                        val updatedStory = story.copy(chapters = story.chapters + newChapters)
                        updatedStories.add(updatedStory)
                        updatesFound.add(updatedStory)
                    } else {
                        updatedStories.add(story)
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    stories = updatedStories,
                    storiesWithUpdates = updatesFound,
                    isCheckingUpdates = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCheckingUpdates = false,
                    error = "Error checking for updates: ${e.message}"
                )
            }
        }
    }

    fun downloadStory(story: WebFictionStory) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val chapters = webFictionService.downloadAllChapters(story)
                val updatedStory = story.copy(chapters = chapters)
                val updatedStories = _uiState.value.stories.map { 
                    if (it.id == story.id) updatedStory else it 
                }
                
                _uiState.value = _uiState.value.copy(
                    stories = updatedStories,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error downloading story: ${e.message}"
                )
            }
        }
    }

    fun downloadAllUpdates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val updatedStories = _uiState.value.stories.toMutableList()
                
                _uiState.value.storiesWithUpdates.forEach { storyWithUpdate ->
                    val index = updatedStories.indexOfFirst { it.id == storyWithUpdate.id }
                    if (index != -1) {
                        val chapters = webFictionService.downloadAllChapters(storyWithUpdate)
                        updatedStories[index] = storyWithUpdate.copy(chapters = chapters)
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    stories = updatedStories,
                    storiesWithUpdates = emptyList(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error downloading updates: ${e.message}"
                )
            }
        }
    }

    fun removeStory(story: WebFictionStory) {
        val updatedStories = _uiState.value.stories.filter { it.id != story.id }
        val updatedStoriesWithUpdates = _uiState.value.storiesWithUpdates.filter { it.id != story.id }
        
        _uiState.value = _uiState.value.copy(
            stories = updatedStories,
            storiesWithUpdates = updatedStoriesWithUpdates
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun loadStories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // In a real app, load from database
                // For now, create some demo data
                val demoStories = createDemoStories()
                _uiState.value = _uiState.value.copy(
                    stories = demoStories,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading stories: ${e.message}"
                )
            }
        }
    }

    private fun createDemoStories(): List<WebFictionStory> {
        return listOf(
            WebFictionStory(
                id = "demo_1",
                title = "The Digital Awakening",
                author = "TechWizard42",
                description = "In a world where AI has become sentient, a young programmer must navigate the complex relationship between humans and artificial intelligence.",
                url = "https://archiveofourown.org/works/demo1",
                site = com.universalmedialibrary.services.webfiction.WebFictionSite.ARCHIVE_OF_OUR_OWN,
                totalChapters = 25,
                lastUpdated = "2024-01-15",
                status = "In-Progress",
                tags = listOf("AI", "Sci-Fi", "Technology", "Romance"),
                coverUrl = "https://via.placeholder.com/300x450/2C5F2D/ffffff?text=Digital+Awakening",
                wordCount = 125000
            ),
            WebFictionStory(
                id = "demo_2",
                title = "Royal Road Chronicles",
                author = "FantasyMaster",
                description = "A comprehensive LitRPG adventure following a player's journey through a virtual world that becomes all too real.",
                url = "https://www.royalroad.com/fiction/demo2",
                site = com.universalmedialibrary.services.webfiction.WebFictionSite.ROYAL_ROAD,
                totalChapters = 156,
                lastUpdated = "2024-01-20",
                status = "Complete",
                tags = listOf("LitRPG", "Adventure", "Virtual Reality", "Action"),
                coverUrl = "https://via.placeholder.com/300x450/1565C0/ffffff?text=Royal+Road",
                wordCount = 890000
            ),
            WebFictionStory(
                id = "demo_3",
                title = "Fanfiction Adventures",
                author = "StoryLover123",
                description = "A collection of interconnected stories exploring different universes and characters in creative ways.",
                url = "https://www.fanfiction.net/s/demo3",
                site = com.universalmedialibrary.services.webfiction.WebFictionSite.FANFICTION_NET,
                totalChapters = 42,
                lastUpdated = "2024-01-18",
                status = "Hiatus",
                tags = listOf("Crossover", "Adventure", "Friendship", "Drama"),
                coverUrl = "https://via.placeholder.com/300x450/7B1FA2/ffffff?text=Fanfiction",
                wordCount = 234000
            )
        )
    }
}

data class WebFictionUiState(
    val stories: List<WebFictionStory> = emptyList(),
    val storiesWithUpdates: List<WebFictionStory> = emptyList(),
    val isLoading: Boolean = false,
    val isCheckingUpdates: Boolean = false,
    val error: String? = null
)