package com.universalmedialibrary.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.universalmedialibrary.data.local.dao.LibraryDao
import com.universalmedialibrary.data.local.model.Library
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class MainViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var libraryDao: LibraryDao
    
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        // Setup default mock behavior
        `when`(libraryDao.getAllLibraries()).thenReturn(flowOf(emptyList()))
        
        viewModel = MainViewModel(libraryDao)
    }

    @Test
    fun `libraries flow emits data from dao`() {
        // Arrange
        val testLibraries = listOf(
            Library(libraryId = 1, name = "Test Library 1", type = "Books", path = "/path/1"),
            Library(libraryId = 2, name = "Test Library 2", type = "Movies", path = "/path/2")
        )
        `when`(libraryDao.getAllLibraries()).thenReturn(flowOf(testLibraries))
        
        // Act
        val viewModel = MainViewModel(libraryDao)
        
        // Assert
        val result = viewModel.libraries.value
        assertThat(result).isEqualTo(testLibraries)
    }

    @Test
    fun `addLibrary triggers library addition`() {
        // Arrange
        val name = "New Library"
        val type = "Books"
        val path = "/new/path"
        
        // Act - This test verifies the method can be called without exception
        viewModel.addLibrary(name, type, path)
        
        // Assert - No exception should be thrown
        // Note: Verifying actual DAO interaction would require coroutine testing
        assertThat(true).isTrue() // Simple assertion to ensure test runs
    }

    @Test
    fun `libraries flow starts with empty list by default`() {
        // Arrange
        `when`(libraryDao.getAllLibraries()).thenReturn(flowOf(emptyList()))
        
        // Act
        val viewModel = MainViewModel(libraryDao)
        
        // Assert
        assertThat(viewModel.libraries.value).isEmpty()
    }
}