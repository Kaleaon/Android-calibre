package com.universalmedialibrary.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.universalmedialibrary.data.local.dao.LibraryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: MainViewModel
    private val libraryDao: LibraryDao = mock()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(libraryDao.getAllLibraries()).thenReturn(flowOf(emptyList()))
        viewModel = MainViewModel(libraryDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addLibrary should insert library into database`() = runTest {
        // Given
        val libraryName = "My New Library"
        val libraryPath = "/path/to/my/library"
        val libraryType = "BOOK"

        // When
        viewModel.addLibrary(libraryName, libraryType, libraryPath)

        // Then
        verify(libraryDao).insertLibrary(any())
    }
}
