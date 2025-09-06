package com.universalmedialibrary.ui.metadata

/**
 * Represents editable metadata for a media item
 */
data class EditableMetadata(
    val title: String = "",
    val subtitle: String = "",
    val sortTitle: String = "",
    val summary: String = "",
    val authors: List<String> = emptyList(),
    val series: String = "",
    val seriesIndex: Int? = null,
    val rating: Int? = null,
    val publisher: String = "",
    val isbn: String = "",
    val genres: List<String> = emptyList(),
    val releaseDate: String = ""
)

/**
 * UI state for the metadata editor
 */
data class MetadataEditorUiState(
    val isLoading: Boolean = false,
    val metadata: EditableMetadata = EditableMetadata(),
    val originalMetadata: EditableMetadata? = null,
    val hasChanges: Boolean = false,
    val error: String? = null
)

/**
 * Sealed class representing different metadata field updates
 */
sealed class MetadataField {
    data class Title(val value: String) : MetadataField()
    data class Subtitle(val value: String) : MetadataField()
    data class SortTitle(val value: String) : MetadataField()
    data class Summary(val value: String) : MetadataField()
    data class Authors(val value: List<String>) : MetadataField()
    data class Series(val value: String) : MetadataField()
    data class SeriesIndex(val value: Int?) : MetadataField()
    data class Rating(val value: Int?) : MetadataField()
    data class Publisher(val value: String) : MetadataField()
    data class ISBN(val value: String) : MetadataField()
    data class Genres(val value: List<String>) : MetadataField()
    data class ReleaseDate(val value: String) : MetadataField()
}