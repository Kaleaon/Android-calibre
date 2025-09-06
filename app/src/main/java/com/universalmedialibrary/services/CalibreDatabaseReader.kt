package com.universalmedialibrary.services

import android.database.sqlite.SQLiteDatabase
import com.universalmedialibrary.services.RawCalibreBook
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalibreDatabaseReader @Inject constructor() {

    fun readBooks(calibreDbPath: String): Map<Long, RawCalibreBook> {
        // Safety: Validate input path
        if (calibreDbPath.isBlank()) {
            return emptyMap()
        }
        
        val calibreDb: SQLiteDatabase
        try {
            calibreDb = SQLiteDatabase.openDatabase(calibreDbPath, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: Exception) {
            return emptyMap()
        }

        return try {
            // This query is complex because Calibre uses a normalized schema.
            // We need to join multiple tables to get all the data for a single book.
            val query = """
                SELECT
                    b.id, b.title, b.path, b.series_index,
                    a.name as author_name,
                    s.name as series_name,
                    p.name as publisher_name,
                    i.val as isbn,
                    t.name as tag_name,
                    c.text as comments
                FROM books b
                LEFT JOIN books_authors_link bal ON b.id = bal.book
                LEFT JOIN authors a ON bal.author = a.id
                LEFT JOIN books_series_link bsl ON b.id = bsl.book
                LEFT JOIN series s ON bsl.series = s.id
                LEFT JOIN books_publishers_link bpl ON b.id = bpl.book
                LEFT JOIN publishers p ON bpl.publisher = p.id
                LEFT JOIN identifiers i ON b.id = i.book AND i.type = 'isbn'
                LEFT JOIN books_tags_link btl ON b.id = btl.book
                LEFT JOIN tags t ON btl.tag = t.id
                LEFT JOIN comments c ON b.id = c.book
            """.trimIndent()

            val cursor = calibreDb.rawQuery(query, null)

            val booksMap = mutableMapOf<Long, RawCalibreBook>()
            val tagsMap = mutableMapOf<Long, MutableList<String>>()
            val authorsMap = mutableMapOf<Long, MutableList<String>>()

            cursor.use {
                while (cursor.moveToNext()) {
                    try {
                        val bookId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))

                        // Initialize the book if we haven't seen it before
                        if (!booksMap.containsKey(bookId)) {
                            // Safety: Handle potential null values from database
                            val title = cursor.getString(cursor.getColumnIndexOrThrow("title")) 
                                ?: "Unknown Title"
                            val path = cursor.getString(cursor.getColumnIndexOrThrow("path")) 
                                ?: ""
                            
                            booksMap[bookId] = RawCalibreBook(
                                id = bookId,
                                title = title,
                                path = path,
                                authorNames = emptyList(), // Will be aggregated
                                seriesName = cursor.getString(cursor.getColumnIndexOrThrow("series_name")),
                                seriesIndex = cursor.getDouble(cursor.getColumnIndexOrThrow("series_index")),
                                publisher = cursor.getString(cursor.getColumnIndexOrThrow("publisher_name")),
                                isbn = cursor.getString(cursor.getColumnIndexOrThrow("isbn")),
                                tags = emptyList(), // Will be aggregated
                                comments = cursor.getString(cursor.getColumnIndexOrThrow("comments"))
                            )
                        }

                        // Aggregate authors - Safety: Check for null and non-empty
                        cursor.getString(cursor.getColumnIndexOrThrow("author_name"))
                            ?.takeIf { it.isNotBlank() }?.let { authorName ->
                                authorsMap.getOrPut(bookId) { mutableListOf() }.add(authorName)
                            }

                        // Aggregate tags - Safety: Check for null and non-empty
                        cursor.getString(cursor.getColumnIndexOrThrow("tag_name"))
                            ?.takeIf { it.isNotBlank() }?.let { tagName ->
                                tagsMap.getOrPut(bookId) { mutableListOf() }.add(tagName)
                            }
                    } catch (e: Exception) {
                        // Safety: Continue processing if one record fails
                        continue
                    }
                }
            }

            // Combine the aggregated data into the final map
            booksMap.mapValues { (id, book) ->
                book.copy(
                    authorNames = authorsMap[id] ?: emptyList(),
                    tags = tagsMap[id] ?: emptyList()
                )
            }
        } catch (e: Exception) {
            emptyMap()
        } finally {
            try {
                calibreDb.close()
            } catch (e: Exception) {
                // Ignore close errors
            }
        }
    }
}
