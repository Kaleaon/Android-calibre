package com.universalmedialibrary.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.model.BookDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * [ViewModel] for the library details screen.
 *
 * This ViewModel is responsible for fetching and exposing the list of book details
 * for a specific library to the UI.
 *
 * @param mediaItemDao DAO for accessing media item data.
 * @param savedStateHandle Handle to the saved state of the ViewModel, used to retrieve navigation arguments.
 */
@HiltViewModel
class LibraryDetailsViewModel @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val libraryId: Long = savedStateHandle.get<String>("libraryId")?.toLong() ?: 0

    /**
     * A [StateFlow] that emits the list of [BookDetails] for the current library.
     * The flow is collected while the UI is subscribed and has an initial value of an empty list.
     */
    val bookDetails: StateFlow<List<BookDetails>> = mediaItemDao.getBookDetailsForLibrary(libraryId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
