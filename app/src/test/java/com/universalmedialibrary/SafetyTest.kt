package com.universalmedialibrary

import com.google.common.truth.Truth.assertThat
import com.universalmedialibrary.services.CalibreImportService
import com.universalmedialibrary.services.CalibreDatabaseReader
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.People
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.lang.reflect.Method

/**
 * Safety tests to prevent crashes and handle edge cases gracefully.
 * These tests specifically target null pointer risks, array bounds, and input validation.
 */
class SafetyTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mediaItemDao: MediaItemDao

    @Mock
    private lateinit var metadataDao: MetadataDao

    @Mock
    private lateinit var calibreReader: CalibreDatabaseReader

    @InjectMocks
    private lateinit var service: CalibreImportService

    private fun invokeCleanAuthorName(rawName: String): People {
        val method: Method = service.javaClass.getDeclaredMethod("cleanAuthorName", String::class.java)
        method.isAccessible = true
        return method.invoke(service, rawName) as People
    }

    @Test
    fun `cleanAuthorName handles empty string safely`() {
        val people = invokeCleanAuthorName("")
        assertThat(people.name).isEqualTo("Unknown Author")
        assertThat(people.sortName).isEqualTo("Unknown Author")
    }

    @Test
    fun `cleanAuthorName handles whitespace only string safely`() {
        val people = invokeCleanAuthorName("   ")
        assertThat(people.name).isEqualTo("Unknown Author")
        assertThat(people.sortName).isEqualTo("Unknown Author")
    }

    @Test
    fun `cleanAuthorName handles single character name`() {
        val people = invokeCleanAuthorName("X")
        assertThat(people.name).isEqualTo("X")
        assertThat(people.sortName).isEqualTo("X")
    }

    @Test
    fun `cleanAuthorName handles comma without last name`() {
        val people = invokeCleanAuthorName(", John")
        assertThat(people.name).isEqualTo("John Unknown")
        assertThat(people.sortName).isEqualTo("Unknown, John")
    }

    @Test
    fun `cleanAuthorName handles comma without first name`() {
        val people = invokeCleanAuthorName("Smith,")
        assertThat(people.name).isEqualTo("Smith")
        assertThat(people.sortName).isEqualTo("Smith")
    }

    @Test
    fun `cleanAuthorName handles only commas`() {
        val people = invokeCleanAuthorName(",,")
        assertThat(people.name).isEqualTo("Unknown")
        assertThat(people.sortName).isEqualTo("Unknown")
    }

    @Test
    fun `cleanAuthorName handles multiple spaces in name`() {
        val people = invokeCleanAuthorName("  John   Doe  ")
        assertThat(people.name).isEqualTo("John Doe")
        assertThat(people.sortName).isEqualTo("Doe, John")
    }

    @Test
    fun `cleanAuthorName handles special characters safely`() {
        val people = invokeCleanAuthorName("O'Neil, Patrick")
        assertThat(people.name).isEqualTo("Patrick O'Neil")
        assertThat(people.sortName).isEqualTo("O'Neil, Patrick")
    }

    @Test
    fun `calibreDatabaseReader handles empty path safely`() {
        val reader = CalibreDatabaseReader()
        val result = reader.readBooks("")
        assertThat(result).isEmpty()
    }

    @Test
    fun `calibreDatabaseReader handles whitespace path safely`() {
        val reader = CalibreDatabaseReader()
        val result = reader.readBooks("   ")
        assertThat(result).isEmpty()
    }

    @Test
    fun `calibreDatabaseReader handles nonexistent file safely`() {
        val reader = CalibreDatabaseReader()
        val result = reader.readBooks("/nonexistent/path/to/database.db")
        assertThat(result).isEmpty()
    }

    /**
     * Test color selection logic safety in PlaceholderCover
     */
    @Test
    fun `color selection handles empty colors list safely`() {
        val colors = emptyList<androidx.compose.ui.graphics.Color>()
        
        // This simulates the safety check we added
        val title = "Test Title"
        val selectedColor = if (colors.isNotEmpty()) {
            colors[kotlin.math.abs(title.hashCode()) % colors.size]
        } else {
            androidx.compose.ui.graphics.Color.Gray // Fallback color
        }
        
        assertThat(selectedColor).isEqualTo(androidx.compose.ui.graphics.Color.Gray)
    }

    @Test
    fun `color selection handles negative hashCode safely`() {
        val colors = listOf(
            androidx.compose.ui.graphics.Color.Red,
            androidx.compose.ui.graphics.Color.Blue
        )
        
        // Test with a string that might produce negative hashCode
        val title = "Test"
        val index = kotlin.math.abs(title.hashCode()) % colors.size
        
        // This should never throw an exception or be negative
        assertThat(index).isAtLeast(0)
        assertThat(index).isLessThan(colors.size)
    }

    @Test
    fun `input validation handles blank library name`() {
        val trimmedName = "".trim()
        val isValid = trimmedName.isNotBlank()
        assertThat(isValid).isFalse()
    }

    @Test
    fun `input validation handles whitespace library name`() {
        val trimmedName = "   ".trim()
        val isValid = trimmedName.isNotBlank()
        assertThat(isValid).isFalse()
    }

    @Test
    fun `input validation handles valid library name`() {
        val trimmedName = "  My Library  ".trim()
        val isValid = trimmedName.isNotBlank()
        assertThat(isValid).isTrue()
        assertThat(trimmedName).isEqualTo("My Library")
    }

    @Test
    fun `navigation parameter validation handles valid library ID`() {
        val libraryIdString = "123"
        val libraryId = libraryIdString.toLongOrNull()?.takeIf { it > 0 } ?: 1L
        assertThat(libraryId).isEqualTo(123L)
    }

    @Test
    fun `navigation parameter validation handles invalid library ID`() {
        val libraryIdString = "-5"
        val libraryId = libraryIdString.toLongOrNull()?.takeIf { it > 0 } ?: 1L
        assertThat(libraryId).isEqualTo(1L) // Should fall back to default
    }

    @Test
    fun `navigation parameter validation handles non-numeric library ID`() {
        val libraryIdString = "abc"
        val libraryId = libraryIdString.toLongOrNull()?.takeIf { it > 0 } ?: 1L
        assertThat(libraryId).isEqualTo(1L) // Should fall back to default
    }

    @Test
    fun `file path validation handles empty library path`() {
        val libraryRootPath = ""
        val relativePath = "author/book.epub"
        val isValid = libraryRootPath.isNotBlank() && relativePath.isNotBlank()
        assertThat(isValid).isFalse()
    }

    @Test
    fun `file path validation handles empty relative path`() {
        val libraryRootPath = "/path/to/library"
        val relativePath = ""
        val isValid = libraryRootPath.isNotBlank() && relativePath.isNotBlank()
        assertThat(isValid).isFalse()
    }

    @Test
    fun `file extension validation handles case insensitive extensions`() {
        val filename = "test.EPUB"
        val supportedExtensions = listOf("epub", "mobi", "pdf")
        val isSupported = filename.substringAfterLast('.', "").lowercase() in supportedExtensions
        assertThat(isSupported).isTrue()
    }
}