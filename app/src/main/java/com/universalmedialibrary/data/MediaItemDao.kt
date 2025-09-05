package com.universalmedialibrary.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class MediaItemDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * Adds a new MediaItem to the database.
     * @param item The MediaItem to add.
     * @return The ID of the newly inserted row, or -1 if an error occurred.
     */
    fun addMediaItem(item: MediaItem): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", item.title)
            put("file_path", item.filePath)
            put("media_type", item.mediaType.name)
            put("date_added", item.dateAdded)
            put("date_modified", item.dateModified)
        }
        // Note: The 'id' is not put here as it is AUTOINCREMENT
        val id = db.insert("media_items", null, values)
        db.close()
        return id
    }

    /**
     * Retrieves a single MediaItem from the database by its ID.
     * @param id The ID of the MediaItem to retrieve.
     * @return The MediaItem if found, otherwise null.
     */
    fun getMediaItem(id: Long): MediaItem? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "media_items", // Table
            arrayOf("id", "title", "file_path", "media_type", "date_added", "date_modified"), // Columns
            "id = ?", // Selection
            arrayOf(id.toString()), // Selection args
            null, // Group by
            null, // Having
            null  // Order by
        )

        var item: MediaItem? = null
        cursor.use { c ->
            if (c.moveToFirst()) {
                item = cursorToMediaItem(c)
            }
        }
        db.close()
        return item
    }

    /**
     * Retrieves all MediaItems from the database.
     * @return A list of all MediaItems.
     */
    fun getAllMediaItems(): List<MediaItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM media_items ORDER BY title ASC", null)
        val items = mutableListOf<MediaItem>()

        cursor.use { c ->
            if (c.moveToFirst()) {
                do {
                    items.add(cursorToMediaItem(c))
                } while (c.moveToNext())
            }
        }
        db.close()
        return items
    }

    private fun cursorToMediaItem(cursor: Cursor): MediaItem {
        return MediaItem(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
            filePath = cursor.getString(cursor.getColumnIndexOrThrow("file_path")),
            mediaType = MediaType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("media_type"))),
            dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow("date_added")),
            dateModified = cursor.getLong(cursor.getColumnIndexOrThrow("date_modified"))
        )
    }

    // Gets an author's ID if they exist, otherwise creates them and returns the new ID.
    private fun getOrCreateAuthor(db: SQLiteDatabase, authorName: String): Long {
        val cursor = db.query("authors", arrayOf("id"), "name = ?", arrayOf(authorName), null, null, null)
        val authorId: Long
        cursor.use { c ->
            if (c.moveToFirst()) {
                authorId = c.getLong(c.getColumnIndexOrThrow("id"))
            } else {
                val values = ContentValues().apply {
                    put("name", authorName)
                }
                authorId = db.insertOrThrow("authors", null, values)
            }
        }
        return authorId
    }

    /**
     * Adds a complete book record, including its base MediaItem, book details,
     * and author information, within a single database transaction.
     * @param mediaItem The base MediaItem object (must be of type BOOK).
     * @param book The Book object with specific details.
     * @param authors A list of Author objects.
     * @return The ID of the newly created MediaItem, or -1 if an error occurred.
     */
    fun addBook(mediaItem: MediaItem, book: Book, authors: List<Author>): Long {
        val db = dbHelper.writableDatabase
        var newMediaItemId: Long = -1

        db.beginTransaction()
        try {
            // 1. Add the base MediaItem
            val mediaItemValues = ContentValues().apply {
                put("title", mediaItem.title)
                put("file_path", mediaItem.filePath)
                put("media_type", MediaType.BOOK.name)
                put("date_added", mediaItem.dateAdded)
                put("date_modified", mediaItem.dateModified)
            }
            newMediaItemId = db.insertOrThrow("media_items", null, mediaItemValues)

            // 2. Add the book-specific details
            val bookValues = ContentValues().apply {
                put("media_item_id", newMediaItemId)
                put("subtitle", book.subtitle)
                put("isbn", book.isbn)
                put("page_count", book.pageCount)
                put("publisher", book.publisher)
            }
            db.insertOrThrow("books", null, bookValues)

            // 3. Add authors and link them
            for (author in authors) {
                val authorId = getOrCreateAuthor(db, author.name)
                val joinValues = ContentValues().apply {
                    put("media_item_id", newMediaItemId)
                    put("author_id", authorId)
                }
                db.insertOrThrow("media_item_author_join", null, joinValues)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }

        return newMediaItemId
    }
}
