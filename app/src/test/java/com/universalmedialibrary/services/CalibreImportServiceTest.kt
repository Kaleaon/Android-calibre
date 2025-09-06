package com.universalmedialibrary.services

import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.People
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class CalibreImportServiceTest {

    private lateinit var calibreImportService: CalibreImportService

    @Before
    fun setUp() {
        val mediaItemDao: MediaItemDao = mockk(relaxed = true)
        val metadataDao: MetadataDao = mockk(relaxed = true)
        calibreImportService = CalibreImportService(mediaItemDao, metadataDao)
    }

    @Test
    fun `cleanAuthorName given name in 'First Last' format, parses correctly`() {
        val result = calibreImportService.cleanAuthorName("J. R. R. Tolkien")
        assertEquals("J. R. R. Tolkien", result.name)
        assertEquals("Tolkien, J. R. R.", result.sortName)
    }

    @Test
    fun `cleanAuthorName given name in 'Last, First' format, parses correctly`() {
        val result = calibreImportService.cleanAuthorName("Tolkien, J. R. R.")
        assertEquals("J. R. R. Tolkien", result.name)
        assertEquals("Tolkien, J. R. R.", result.sortName)
    }

    @Test
    fun `cleanAuthorName given single word name, parses correctly`() {
        val result = calibreImportService.cleanAuthorName("Plato")
        assertEquals("Plato", result.name)
        assertEquals("Plato", result.sortName)
    }
}
