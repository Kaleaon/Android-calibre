package com.universalmedialibrary.services

import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.util.zip.ZipFile

class ContentServicesTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `EpubCreationService creates a valid epub file`() {
        val service = EpubCreationService()
        val testFile = tempFolder.newFile("test.epub")

        service.createEpubFile("Test Title", "Test Author", "<p>Hello</p>", testFile.absolutePath)

        assertThat(testFile.exists()).isTrue()
        assertThat(testFile.length()).isGreaterThan(0L)

        // Verify it's a valid zip file with the correct entries
        val zipFile = ZipFile(testFile)
        assertThat(zipFile.getEntry("mimetype")).isNotNull()
        assertThat(zipFile.getEntry("META-INF/container.xml")).isNotNull()
        assertThat(zipFile.getEntry("OEBPS/content.opf")).isNotNull()
        zipFile.close()
    }

    @Test
    fun `FanficExtractionService extracts title and author from local HTML`() {
        val service = FanficExtractionService()
        val inputStream = javaClass.classLoader?.getResourceAsStream("The_Wonderful_Wizard_of_Oz_AO3.html")
        assertThat(inputStream).withFailMessage("Test resource 'The_Wonderful_Wizard_of_Oz_AO3.html' not found in classpath.").isNotNull()
        val html = inputStream.bufferedReader().readText()
        val doc = org.jsoup.Jsoup.parse(html)

        val content = service.parseAo3Document(doc)

        assertThat(content).isNotNull()
        assertThat(content!!.title).isEqualTo("The Wonderful Wizard of Oz")
        assertThat(content.author).isEqualTo("L. Frank Baum")
    }
}
