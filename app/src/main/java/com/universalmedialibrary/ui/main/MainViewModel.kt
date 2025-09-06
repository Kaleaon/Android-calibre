package com.universalmedialibrary.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.data.local.dao.LibraryDao
import com.universalmedialibrary.data.local.model.Library
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ViewModel] for the main screen.
 *
 * This ViewModel is responsible for managing and exposing the list of libraries
 * to the UI and providing a method to add new libraries.
 *
 * @param libraryDao DAO for accessing library data.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val libraryDao: LibraryDao
) : ViewModel() {

    /**
     * A [StateFlow] that emits the list of all [Library] objects in the database.
     */
    val libraries: StateFlow<List<Library>> = libraryDao.getAllLibraries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Adds a new library to the database.
     *
     * @param name The name of the new library.
     * @param type The type of the new library (e.g., 'BOOK').
     * @param path The root file path of the new library.
     */
    fun addLibrary(name: String, type: String, path: String) {
        viewModelScope.launch {
            val newLibrary = Library(name = name, type = type, path = path)
            libraryDao.insertLibrary(newLibrary)
        }
    }
}
