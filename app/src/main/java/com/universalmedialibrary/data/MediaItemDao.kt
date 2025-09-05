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
}
