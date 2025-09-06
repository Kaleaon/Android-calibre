package com.universalmedialibrary.services

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.File

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CalibreDatabaseReaderTest {

    private val reader = CalibreDatabaseReader()

    @Test
    fun `readBooks returns empty map when database file does not exist`() {
        // Arrange
        val nonExistentPath = "/path/that/does/not/exist/metadata.db"
        
        // Act
        val result = reader.readBooks(nonExistentPath)
        
        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `readBooks returns empty map when database path is invalid`() {
        // Arrange
        val invalidPath = "/invalid/path/metadata.db"
        
        // Act
        val result = reader.readBooks(invalidPath)
        
        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `readBooks handles database access exceptions gracefully`() {
        // Arrange
        val tempFile = File.createTempFile("test", ".db")
        tempFile.deleteOnExit()
        
        // Create an invalid SQLite file (just some text)
        tempFile.writeText("This is not a valid SQLite database")
        
        // Act
        val result = reader.readBooks(tempFile.absolutePath)
        
        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `readBooks returns empty map for empty database`() {
        // Note: This test would require setting up a proper SQLite database
        // For now, we'll test with an empty file that will cause an exception
        val tempFile = File.createTempFile("empty", ".db")
        tempFile.deleteOnExit()
        
        // Act
        val result = reader.readBooks(tempFile.absolutePath)
        
        // Assert
        assertThat(result).isEmpty()
    }
}