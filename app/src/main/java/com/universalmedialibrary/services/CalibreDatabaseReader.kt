package com.universalmedialibrary.services

import android.database.sqlite.SQLiteDatabase

/**
 * Reads raw book data from a Calibre `metadata.db` file.
 *
 * This class is responsible for opening a connection to a Calibre database,
 * querying it for book and author information, and returning it in a raw format.
 */
class CalibreDatabaseReader {

    /**
     * Reads all books and their primary author from the specified Calibre database file.
     *
     * It performs a read-only query on the `books` and `authors` tables.
     * Note: This implementation only retrieves the first author for books with multiple authors.
     *
     * @param calibreDbPath The absolute file path to the Calibre `metadata.db` file.
     * @return A map of Calibre book IDs to [RawCalibreBook] objects. Returns an empty map if the database cannot be read.
     */
    fun readBooks(calibreDbPath: String): Map<Long, RawCalibreBook> {
        return try {
            SQLiteDatabase.openDatabase(calibreDbPath, null, SQLiteDatabase.OPEN_READONLY).use { calibreDb ->
                val query = """
                    SELECT b.id, b.title, b.path, a.name
                    FROM books b
                    LEFT JOIN books_authors_link bal ON b.id = bal.book
                    LEFT JOIN authors a ON bal.author = a.id
                """.trimIndent()

                val cursor = calibreDb.rawQuery(query, null)
                val rawBooks = mutableMapOf<Long, RawCalibreBook>()
                cursor.use { c ->
                    while (c.moveToNext()) {
                        val bookId = c.getLong(c.getColumnIndexOrThrow("id"))
                        if (!rawBooks.containsKey(bookId)) {
                            rawBooks[bookId] = RawCalibreBook(
                                id = bookId,
                                title = c.getString(c.getColumnIndexOrThrow("title")),
                                path = c.getString(c.getColumnIndexOrThrow("path")),
                                authorName = c.getString(c.getColumnIndexOrThrow("name"))
                            )
                        }
                    }
                }
                rawBooks
            }
        } catch (e: Exception) {
            // Consider logging the exception
            emptyMap()
        }
    }
}
