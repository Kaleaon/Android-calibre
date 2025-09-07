package com.universalmedialibrary.services

import com.google.common.truth.Truth.assertThat
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.People
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class CalibreImportServiceTest {

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

    // This is a bit of a hack to test a private function.
    private fun invokeCleanAuthorName(rawName: String): People {
        val method = service.javaClass.getDeclaredMethod("cleanAuthorName", String::class.java)
        method.isAccessible = true
        return method.invoke(service, rawName) as People
    }

    @Test
    fun `cleanAuthorName handles 'LastName, FirstName' format`() {
        val people = invokeCleanAuthorName("Salinger, J.D.")
        assertThat(people.name).isEqualTo("J.D. Salinger")
        assertThat(people.sortName).isEqualTo("Salinger, J.D.")
    }

    @Test
    fun `cleanAuthorName handles 'FirstName LastName' format`() {
        val people = invokeCleanAuthorName("Isaac Asimov")
        assertThat(people.name).isEqualTo("Isaac Asimov")
        assertThat(people.sortName).isEqualTo("Asimov, Isaac")
    }

    @Test
    fun `cleanAuthorName handles single name`() {
        val people = invokeCleanAuthorName("Plato")
        assertThat(people.name).isEqualTo("Plato")
        assertThat(people.sortName).isEqualTo("Plato")
    }
}
