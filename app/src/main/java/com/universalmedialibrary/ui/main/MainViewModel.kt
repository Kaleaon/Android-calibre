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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val libraryDao: LibraryDao
) : ViewModel() {

    val libraries: StateFlow<List<Library>> = libraryDao.getAllLibraries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addLibrary(name: String, type: String, path: String) {
        // Safety: Validate inputs before processing
        val trimmedName = name.trim()
        val trimmedType = type.trim()
        val trimmedPath = path.trim()
        
        if (trimmedName.isBlank() || trimmedType.isBlank() || trimmedPath.isBlank()) {
            return // Early return for invalid inputs
        }
        
        viewModelScope.launch {
            try {
                val newLibrary = Library(name = trimmedName, type = trimmedType, path = trimmedPath)
                libraryDao.insertLibrary(newLibrary)
            } catch (e: Exception) {
                // Safety: Handle database insertion errors gracefully
                // In production, this would be logged and possibly shown to user
            }
        }
    }
}
