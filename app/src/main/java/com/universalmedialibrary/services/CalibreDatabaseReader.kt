package com.universalmedialibrary.services

import android.database.sqlite.SQLiteDatabase
import com.universalmedialibrary.services.RawCalibreBook

class CalibreDatabaseReader {

    fun readBooks(calibreDbPath: String): Map<Long, RawCalibreBook> {
        val calibreDb: SQLiteDatabase
        try {
            calibreDb = SQLiteDatabase.openDatabase(calibreDbPath, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: Exception) {
            return emptyMap()
        }

        val query = """
            SELECT b.id, b.title, b.path, a.name
            FROM books b
            LEFT JOIN books_authors_link bal ON b.id = bal.book
            LEFT JOIN authors a ON bal.author = a.id
        """.trimIndent()

        val cursor = calibreDb.rawQuery(query, null)

        val rawBooks = mutableMapOf<Long, RawCalibreBook>()
        while (cursor.moveToNext()) {
            val bookId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            if (!rawBooks.containsKey(bookId)) {
                rawBooks[bookId] = RawCalibreBook(
                    id = bookId,
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    path = cursor.getString(cursor.getColumnIndexOrThrow("path")),
                    authorName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                )
            }
        }
        cursor.close()
        calibreDb.close()
        return rawBooks
    }
}
