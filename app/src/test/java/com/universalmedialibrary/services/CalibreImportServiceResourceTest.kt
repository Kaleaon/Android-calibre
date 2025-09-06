package com.universalmedialibrary.services

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import java.io.File
import java.io.FileOutputStream

class CalibreImportServiceResourceTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Mock
    private lateinit var mediaItemDao: MediaItemDao
    
    @Mock
    private lateinit var metadataDao: MetadataDao
    
    @Mock
    private lateinit var calibreReader: CalibreDatabaseReader

    private lateinit var service: CalibreImportService
    private lateinit var testFile: File

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        
        // Create a test file for MD5 calculation
        testFile = tempFolder.newFile("test.txt")
        FileOutputStream(testFile).use { output ->
            output.write("Hello, World!".toByteArray())
        }
        
        service = CalibreImportService(mediaItemDao, metadataDao, calibreReader)
    }

    @Test
    fun `calculateMD5 properly closes file streams`() {
        // This test will fail if FileInputStream is not properly closed
        // because the temporary file won't be deletable on Windows systems
        
        // Use reflection to access the private method
        val method = service.javaClass.getDeclaredMethod("calculateMD5", File::class.java)
        method.isAccessible = true
        
        // Calculate MD5 - this should properly close the stream
        val md5 = method.invoke(service, testFile) as String
        
        // Verify the MD5 is calculated correctly
        assertThat(md5).isEqualTo("65a8e27d8879283831b664bd8b7f0ad4") // MD5 of "Hello, World!"
        
        // Verify the file can be deleted (indicating streams were properly closed)
        assertThat(testFile.delete()).isTrue()
    }

    @Test
    fun `calculateMD5 handles large files without memory issues`() {
        // Create a larger test file to ensure buffering works correctly
        val largeFile = tempFolder.newFile("large.txt")
        FileOutputStream(largeFile).use { output ->
            repeat(1000) { i ->
                output.write("Line $i: This is a test line to create a larger file for testing.\n".toByteArray())
            }
        }
        
        val method = service.javaClass.getDeclaredMethod("calculateMD5", File::class.java)
        method.isAccessible = true
        
        // This should not throw OutOfMemoryError
        val md5 = method.invoke(service, largeFile) as String
        
        // Verify we got a valid MD5 hash (32 characters, hex)
        assertThat(md5).hasLength(32)
        assertThat(md5).matches("[a-f0-9]{32}")
        
        // Clean up
        assertThat(largeFile.delete()).isTrue()
    }
}