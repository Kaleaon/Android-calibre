package com.universalmedialibrary.services

import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.People
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method

class CalibreImportServiceTest {

    private lateinit var calibreImportService: CalibreImportService
    private lateinit var cleanAuthorName: Method

    @Before
    fun setUp() {
        val mediaItemDao: MediaItemDao = mockk(relaxed = true)
        val metadataDao: MetadataDao = mockk(relaxed = true)
        calibreImportService = CalibreImportService(mediaItemDao, metadataDao)

        cleanAuthorName = CalibreImportService::class.java.getDeclaredMethod("cleanAuthorName", String::class.java)
        cleanAuthorName.isAccessible = true
    }

    @Test
    fun `cleanAuthorName given name in 'First Last' format, parses correctly`() {
        val result = cleanAuthorName.invoke(calibreImportService, "J. R. R. Tolkien") as People
        assertEquals("J. R. R. Tolkien", result.name)
        assertEquals("Tolkien, J. R. R.", result.sortName)
    }

    @Test
    fun `cleanAuthorName given name in 'Last, First' format, parses correctly`() {
        val result = cleanAuthorName.invoke(calibreImportService, "Tolkien, J. R. R.") as People
        assertEquals("J. R. R. Tolkien", result.name)
        assertEquals("Tolkien, J. R. R.", result.sortName)
    }

    @Test
    fun `cleanAuthorName given single word name, parses correctly`() {
        val result = cleanAuthorName.invoke(calibreImportService, "Plato") as People
        assertEquals("Plato", result.name)
        assertEquals("Plato", result.sortName)
    }
}
