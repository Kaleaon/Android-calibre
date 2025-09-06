package com.universalmedialibrary.data.local.dao

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.universalmedialibrary.data.local.AppDatabase
import com.universalmedialibrary.data.local.model.Library
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LibraryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var libraryDao: LibraryDao

    @Before
    fun setup() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        libraryDao = database.libraryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `insertLibrary and getAllLibraries returns correct libraries`() = runBlocking {
        // Arrange
        val library1 = Library(name = "Books Library", type = "BOOK", path = "/books")
        val library2 = Library(name = "Movies Library", type = "MOVIE", path = "/movies")
        
        // Act
        libraryDao.insertLibrary(library1)
        libraryDao.insertLibrary(library2)
        
        val result = libraryDao.getAllLibraries().first()
        
        // Assert
        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactly("Books Library", "Movies Library")
        assertThat(result.map { it.type }).containsExactly("BOOK", "MOVIE")
        assertThat(result.map { it.path }).containsExactly("/books", "/movies")
    }

    @Test
    fun `getAllLibraries returns empty list when no libraries exist`() = runBlocking {
        // Act
        val result = libraryDao.getAllLibraries().first()
        
        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `insertLibrary with same name replaces existing library`() = runBlocking {
        // Arrange
        val originalLibrary = Library(name = "Test Library", type = "BOOK", path = "/old/path")
        val updatedLibrary = Library(name = "Test Library", type = "MOVIE", path = "/new/path")
        
        // Act
        libraryDao.insertLibrary(originalLibrary)
        libraryDao.insertLibrary(updatedLibrary)
        
        val result = libraryDao.getAllLibraries().first()
        
        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0].type).isEqualTo("MOVIE")
        assertThat(result[0].path).isEqualTo("/new/path")
    }

    @Test
    fun `getAllLibraries returns libraries sorted by name`() = runBlocking {
        // Arrange
        val libraryZ = Library(name = "Z Library", type = "BOOK", path = "/z")
        val libraryA = Library(name = "A Library", type = "BOOK", path = "/a")
        val libraryM = Library(name = "M Library", type = "BOOK", path = "/m")
        
        // Act
        libraryDao.insertLibrary(libraryZ)
        libraryDao.insertLibrary(libraryA)
        libraryDao.insertLibrary(libraryM)
        
        val result = libraryDao.getAllLibraries().first()
        
        // Assert
        assertThat(result.map { it.name }).containsExactly("A Library", "M Library", "Z Library").inOrder()
    }
}