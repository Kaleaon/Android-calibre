package com.universalmedialibrary.data

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.universalmedialibrary.data.local.AppDatabase
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.model.Library
import com.universalmedialibrary.data.local.model.MediaItem
import com.universalmedialibrary.data.local.model.MetadataBook
import com.universalmedialibrary.data.local.model.MetadataCommon
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MediaItemDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var mediaItemDao: MediaItemDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        mediaItemDao = db.mediaItemDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `insert and get media item`() = runBlocking {
        // ARRANGE
        val library = Library(name = "Test Library", type = "BOOK", path = "/path/to/library")
        val libraryId = db.libraryDao().insertLibrary(library)
        val mediaItem = MediaItem(libraryId = libraryId, filePath = "/path/to/book.epub", dateAdded = 1L, lastScanned = 1L, fileHash = "hash")
        val mediaItemId = mediaItemDao.insertMediaItem(mediaItem)

        // ACT
        val retrievedItem = mediaItemDao.getMediaItemById(mediaItemId)

        // ASSERT
        assertThat(retrievedItem).isNotNull()
        assertThat(retrievedItem?.itemId).isEqualTo(mediaItemId)
        assertThat(retrievedItem?.filePath).isEqualTo("/path/to/book.epub")
    }

    @Test
    fun `deleteMediaItem deletes item and metadata`() = runBlocking {
        // ARRANGE
        // 1. Create and insert a library
        val library = Library(name = "Test Library", type = "BOOK", path = "/path/to/library")
        val libraryId = db.libraryDao().insertLibrary(library)

        // 2. Create and insert a media item
        val mediaItem = MediaItem(libraryId = libraryId, filePath = "/path/to/book.epub", dateAdded = 1L, lastScanned = 1L, fileHash = "hash")
        val mediaItemId = mediaItemDao.insertMediaItem(mediaItem)

        // 3. Create and insert metadata
        val metadataCommon = MetadataCommon(itemId = mediaItemId, title = "Test Book", sortTitle = "Test Book", year = 2023, releaseDate = 1L, rating = 5.0f, summary = "Summary", coverImagePath = "/path/to/cover.jpg")
        val metadataBook = MetadataBook(itemId = mediaItemId, subtitle = "A Test Subtitle", publisher = "Publisher", isbn = "12345", pageCount = 100, seriesId = null)
        db.metadataDao().insertMetadataCommon(metadataCommon)
        db.metadataDao().insertMetadataBook(metadataBook)

        // ACT
        mediaItemDao.deleteMediaItem(mediaItemId)

        // ASSERT
        val retrievedItem = mediaItemDao.getMediaItemById(mediaItemId)
        assertThat(retrievedItem).isNull()

        val retrievedMetadataCommon = db.metadataDao().getMetadataCommonById(mediaItemId)
        assertThat(retrievedMetadataCommon).isNull()

        val retrievedMetadataBook = db.metadataDao().getMetadataBookById(mediaItemId)
        assertThat(retrievedMetadataBook).isNull()
    }
}
