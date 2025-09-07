package com.universalmedialibrary.data.local.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LibraryTest {

    @Test
    fun `Library model creates instance with correct properties`() {
        // Arrange
        val libraryId = 1L
        val name = "My Books"
        val type = "BOOK"
        val path = "/storage/books"
        
        // Act
        val library = Library(libraryId = libraryId, name = name, type = type, path = path)
        
        // Assert
        assertThat(library.libraryId).isEqualTo(libraryId)
        assertThat(library.name).isEqualTo(name)
        assertThat(library.type).isEqualTo(type)
        assertThat(library.path).isEqualTo(path)
    }

    @Test
    fun `Library model uses default libraryId when not specified`() {
        // Arrange
        val name = "Default Library"
        val type = "MOVIE"
        val path = "/storage/movies"
        
        // Act
        val library = Library(name = name, type = type, path = path)
        
        // Assert
        assertThat(library.libraryId).isEqualTo(0L)
        assertThat(library.name).isEqualTo(name)
        assertThat(library.type).isEqualTo(type)
        assertThat(library.path).isEqualTo(path)
    }

    @Test
    fun `Library model supports different media types`() {
        // Arrange & Act
        val bookLibrary = Library(name = "Books", type = "BOOK", path = "/books")
        val movieLibrary = Library(name = "Movies", type = "MOVIE", path = "/movies")
        val musicLibrary = Library(name = "Music", type = "MUSIC", path = "/music")
        
        // Assert
        assertThat(bookLibrary.type).isEqualTo("BOOK")
        assertThat(movieLibrary.type).isEqualTo("MOVIE")
        assertThat(musicLibrary.type).isEqualTo("MUSIC")
    }

    @Test
    fun `Library model supports equality comparison`() {
        // Arrange
        val library1 = Library(libraryId = 1L, name = "Test Library", type = "BOOK", path = "/test")
        val library2 = Library(libraryId = 1L, name = "Test Library", type = "BOOK", path = "/test")
        val library3 = Library(libraryId = 2L, name = "Test Library", type = "BOOK", path = "/test")
        
        // Act & Assert
        assertThat(library1).isEqualTo(library2)
        assertThat(library1).isNotEqualTo(library3)
    }
}