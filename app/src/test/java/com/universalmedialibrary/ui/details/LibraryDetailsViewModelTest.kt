package com.universalmedialibrary.ui.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.model.BookDetails
import com.universalmedialibrary.data.local.model.MediaItem
import com.universalmedialibrary.data.local.model.MetadataCommon
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class LibraryDetailsViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mediaItemDao: MediaItemDao

    @Mock
    private lateinit var savedStateHandle: SavedStateHandle
    
    private lateinit var viewModel: LibraryDetailsViewModel

    @Before
    fun setup() {
        // Default mock setup
        `when`(savedStateHandle.get<String>("libraryId")).thenReturn("1")
        `when`(mediaItemDao.getBookDetailsForLibrary(1L)).thenReturn(flowOf(emptyList()))
    }

    @Test
    fun `bookDetails flow emits data for valid library ID`() {
        // Arrange
        val libraryId = 123L
        val testBookDetails = listOf(
            BookDetails(
                mediaItem = MediaItem(
                    itemId = 1,
                    libraryId = libraryId,
                    filePath = "/path/1",
                    dateAdded = System.currentTimeMillis(),
                    lastScanned = System.currentTimeMillis(),
                    fileHash = "hash1"
                ),
                metadata = MetadataCommon(
                    itemId = 1,
                    title = "Test Book 1",
                    sortTitle = "Test Book 1",
                    year = 2023,
                    releaseDate = System.currentTimeMillis(),
                    rating = 4.5f,
                    summary = "A test book",
                    coverImagePath = "/cover/1.jpg"
                ),
                authorName = "Author 1"
            )
        )
        
        `when`(savedStateHandle.get<String>("libraryId")).thenReturn(libraryId.toString())
        `when`(mediaItemDao.getBookDetailsForLibrary(libraryId)).thenReturn(flowOf(testBookDetails))
        
        // Act
        viewModel = LibraryDetailsViewModel(mediaItemDao, savedStateHandle)
        
        // Assert
        assertThat(viewModel.bookDetails.value).isEqualTo(testBookDetails)
    }

    @Test
    fun `bookDetails flow uses default library ID when savedStateHandle returns null`() {
        // Arrange
        val defaultLibraryId = 0L
        val emptyBookDetails = emptyList<BookDetails>()
        
        `when`(savedStateHandle.get<String>("libraryId")).thenReturn(null)
        `when`(mediaItemDao.getBookDetailsForLibrary(defaultLibraryId)).thenReturn(flowOf(emptyBookDetails))
        
        // Act
        viewModel = LibraryDetailsViewModel(mediaItemDao, savedStateHandle)
        
        // Assert
        assertThat(viewModel.bookDetails.value).isEmpty()
    }

    @Test
    fun `bookDetails flow starts with empty list by default`() {
        // Arrange
        `when`(savedStateHandle.get<String>("libraryId")).thenReturn("1")
        `when`(mediaItemDao.getBookDetailsForLibrary(1L)).thenReturn(flowOf(emptyList()))
        
        // Act
        viewModel = LibraryDetailsViewModel(mediaItemDao, savedStateHandle)
        
        // Assert
        assertThat(viewModel.bookDetails.value).isEmpty()
    }
}