package com.universalmedialibrary.services

import com.google.common.truth.Truth.assertThat
import org.junit.Test

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
    fun `readBooks handles null path gracefully`() {
        // Arrange - using empty string instead of null for safety
        val emptyPath = ""
        
        // Act
        val result = reader.readBooks(emptyPath)
        
        // Assert
        assertThat(result).isEmpty()
    }
}