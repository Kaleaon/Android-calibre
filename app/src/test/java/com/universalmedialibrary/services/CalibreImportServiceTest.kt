package com.universalmedialibrary.services

import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.MediaItem
import com.universalmedialibrary.data.local.model.MetadataCommon
import com.universalmedialibrary.data.local.model.People
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File
import java.lang.reflect.Method

class CalibreImportServiceTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var calibreImportService: CalibreImportService
    private val mediaItemDao: MediaItemDao = mock()
    private val metadataDao: MetadataDao = mock()
    private val calibreDatabaseReader: CalibreDatabaseReader = mock()

    @Before
    fun setup() {
        calibreImportService = CalibreImportService(mediaItemDao, metadataDao, calibreDatabaseReader)
    }

    private fun invokeMyCapitalize(input: String): String {
        val method: Method = CalibreImportService::class.java.getDeclaredMethod("myCapitalize", String::class.java)
        method.isAccessible = true
        val serviceInstance = calibreImportService
        return method.invoke(serviceInstance, input) as String
    }

    private fun invokeCleanTitle(input: String): String {
        val method: Method = CalibreImportService::class.java.getDeclaredMethod("cleanTitle", String::class.java)
        method.isAccessible = true
        return method.invoke(calibreImportService, input) as String
    }

    private fun invokeCreateSortTitle(input: String): String {
        val method: Method = CalibreImportService::class.java.getDeclaredMethod("createSortTitle", String::class.java)
        method.isAccessible = true
        return method.invoke(calibreImportService, input) as String
    }

    private fun invokeCleanAuthorName(input: String): People {
        val method: Method = CalibreImportService::class.java.getDeclaredMethod("cleanAuthorName", String::class.java)
        method.isAccessible = true
        return method.invoke(calibreImportService, input) as People
    }


    @Test
    fun `myCapitalize capitalizes the first letter of a lowercase string`() {
        assertEquals("Hello", "hello".let { invokeMyCapitalize(it) })
    }

    @Test
    fun `myCapitalize does not change an already capitalized string`() {
        assertEquals("Hello", "Hello".let { invokeMyCapitalize(it) })
    }

    @Test
    fun `myCapitalize handles single-letter strings`() {
        assertEquals("A", "a".let { invokeMyCapitalize(it) })
    }

    @Test
    fun `myCapitalize handles empty strings`() {
        assertEquals("", "".let { invokeMyCapitalize(it) })
    }

    @Test
    fun `cleanTitle capitalizes each word in a title`() {
        assertEquals("A Title With Mixed Case", invokeCleanTitle("a tiTle wITh mixED caSe"))
    }

    @Test
    fun `createSortTitle moves 'The' to the end`() {
        assertEquals("Adventures of Tom Sawyer, The", invokeCreateSortTitle("The Adventures of Tom Sawyer"))
    }

    @Test
    fun `createSortTitle moves 'A' to the end`() {
        assertEquals("Gambler, A", invokeCreateSortTitle("A Gambler"))
    }

    @Test
    fun `createSortTitle moves 'An' to the end`() {
        assertEquals("Apple a Day, An", invokeCreateSortTitle("An Apple a Day"))
    }

    @Test
    fun `createSortTitle does not change title without an article`() {
        assertEquals("Moby Dick", invokeCreateSortTitle("Moby Dick"))
    }

    @Test
    fun `cleanAuthorName handles 'Last, First' format`() {
        val expected = People(0, "Isaac Asimov", "Asimov, Isaac")
        assertEquals(expected, invokeCleanAuthorName("Asimov, Isaac"))
    }

    @Test
    fun `cleanAuthorName handles 'First Last' format`() {
        val expected = People(0, "J.R.R. Tolkien", "Tolkien, J.R.R.")
        assertEquals(expected, invokeCleanAuthorName("J.R.R. Tolkien"))
    }

    @Test
    fun `cleanAuthorName handles single-name authors`() {
        val expected = People(0, "Plato", "Plato, ")
        assertEquals(expected, invokeCleanAuthorName("Plato"))
    }

    @Test
    fun `importCalibreDatabase imports books correctly`() = runTest {
        // Arrange
        val libraryRoot = tempFolder.newFolder("library")
        val bookFile = File(libraryRoot, "book.epub")
        bookFile.createNewFile()

        val rawBook = RawCalibreBook(
            id = 1L,
            title = "Test Book",
            authorName = "Test Author",
            path = "book.epub"
        )
        val rawBooks = mapOf(1L to rawBook)
        whenever(calibreDatabaseReader.readBooks(any())).thenReturn(rawBooks)
        whenever(mediaItemDao.insertMediaItem(any())).thenReturn(1L)
        whenever(metadataDao.insertPerson(any())).thenReturn(1L)

        // Act
        calibreImportService.importCalibreDatabase("dummy_path", libraryRoot.absolutePath, 1L)

        // Assert
        verify(mediaItemDao).insertMediaItem(any<MediaItem>())
        verify(metadataDao).insertMetadataCommon(any<MetadataCommon>())
        verify(metadataDao).insertPerson(any<People>())
    }
}
