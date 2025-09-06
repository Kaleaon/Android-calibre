package com.universalmedialibrary.data

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MediaItemDaoTest {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var mediaItemDao: MediaItemDao

    @Before
    fun setup() {
        // Using ApplicationProvider to get a context in a test environment with Robolectric
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        dbHelper = DatabaseHelper(context)
        mediaItemDao = MediaItemDao(context)

        // Clean the database before each test to ensure isolation
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM media_items")
        db.close()
    }

    @After
    fun teardown() {
        dbHelper.close()
    }

    @Test
    fun `addMediaItem and getMediaItem returns correct item`() {
        // ARRANGE
        val newItem =
            MediaItem(
                id = 1, // id is ignored by addMediaItem, but needed for the data class
                title = "Test Book",
                filePath = "/path/to/book.epub",
                mediaType = MediaType.BOOK,
                dateAdded = System.currentTimeMillis(),
                dateModified = System.currentTimeMillis(),
            )

        // ACT
        val newId = mediaItemDao.addMediaItem(newItem)
        val retrievedItem = mediaItemDao.getMediaItem(newId)

        // ASSERT
        assertThat(retrievedItem).isNotNull()
        assertThat(retrievedItem?.id).isEqualTo(newId)
        assertThat(retrievedItem?.title).isEqualTo("Test Book")
        assertThat(retrievedItem?.mediaType).isEqualTo(MediaType.BOOK)
    }

    @Test
    fun `getAllMediaItems returns all items sorted by title`() {
        // ARRANGE
        val itemB = MediaItem(0, "Book B", "/path/b", MediaType.BOOK, 2L, 2L)
        val itemA = MediaItem(0, "Book A", "/path/a", MediaType.BOOK, 1L, 1L)
        mediaItemDao.addMediaItem(itemB)
        mediaItemDao.addMediaItem(itemA)

        // ACT
        val allItems = mediaItemDao.getAllMediaItems()

        // ASSERT
        assertThat(allItems).hasSize(2)
        // Check for correct sorting
        assertThat(allItems.map { it.title }).containsExactly("Book A", "Book B").inOrder()
    }

    @Test
    fun `getMediaItem returns null for non-existent item`() {
        // ACT
        val retrievedItem = mediaItemDao.getMediaItem(999)

        // ASSERT
        assertThat(retrievedItem).isNull()
    }

    @Test
    fun `addBook transaction inserts book, author, and links correctly`() {
        // ARRANGE
        val mediaItem = MediaItem(0, "Transaction Test", "/path/trans", MediaType.BOOK, 1L, 1L)
        val book = Book(0, "A Test Subtitle", "12345", 100, "Test Publisher")
        val authors = listOf(Author(0, "New Author", null))

        // ACT
        val newId = mediaItemDao.addBook(mediaItem, book, authors)
        assertThat(newId).isNotEqualTo(-1)

        // ASSERT
        // Use raw queries to check the state of each table directly
        val db = dbHelper.readableDatabase

        // 1. Check media_items table
        db.query("media_items", null, "id = ?", arrayOf(newId.toString()), null, null, null).use {
            assertThat(it.count).isEqualTo(1)
            it.moveToFirst()
            assertThat(it.getString(it.getColumnIndexOrThrow("title"))).isEqualTo("Transaction Test")
        }

        // 2. Check books table
        db.query("books", null, "media_item_id = ?", arrayOf(newId.toString()), null, null, null).use {
            assertThat(it.count).isEqualTo(1)
            it.moveToFirst()
            assertThat(it.getString(it.getColumnIndexOrThrow("subtitle"))).isEqualTo("A Test Subtitle")
        }

        // 3. Check authors table and get authorId
        val authorId =
            db.query("authors", arrayOf("id"), "name = ?", arrayOf("New Author"), null, null, null).use {
                assertThat(it.count).isEqualTo(1)
                it.moveToFirst()
                it.getLong(it.getColumnIndexOrThrow("id"))
            }

        // 4. Check join table
        db
            .query(
                "media_item_author_join",
                null,
                "media_item_id = ? AND author_id = ?",
                arrayOf(newId.toString(), authorId.toString()),
                null,
                null,
                null,
            ).use {
                assertThat(it.count).isEqualTo(1)
            }

        db.close()
    }
}
