package com.universalmedialibrary.services

import android.database.sqlite.SQLiteDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalibreDatabaseReader
    @Inject
    constructor() {
        fun readBooks(calibreDbPath: String): Map<Long, RawCalibreBook> {
            val calibreDb: SQLiteDatabase
            try {
                calibreDb = SQLiteDatabase.openDatabase(calibreDbPath, null, SQLiteDatabase.OPEN_READONLY)
            } catch (e: Exception) {
                return emptyMap()
            }

            // This query is complex because Calibre uses a normalized schema.
            // We need to join multiple tables to get all the data for a single book.
            val query =
                """
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

            while (cursor.moveToNext()) {
                val bookId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))

                // Initialize the book if we haven't seen it before
                if (!booksMap.containsKey(bookId)) {
                    booksMap[bookId] =
                        RawCalibreBook(
                            id = bookId,
                            title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                            path = cursor.getString(cursor.getColumnIndexOrThrow("path")),
                            authorNames = emptyList(), // Will be aggregated
                            seriesName = cursor.getString(cursor.getColumnIndexOrThrow("series_name")),
                            seriesIndex = cursor.getDouble(cursor.getColumnIndexOrThrow("series_index")),
                            publisher = cursor.getString(cursor.getColumnIndexOrThrow("publisher_name")),
                            isbn = cursor.getString(cursor.getColumnIndexOrThrow("isbn")),
                            tags = emptyList(), // Will be aggregated
                            comments = cursor.getString(cursor.getColumnIndexOrThrow("comments")),
                        )
                }

                // Aggregate authors
                cursor.getString(cursor.getColumnIndexOrThrow("author_name"))?.let {
                    authorsMap.getOrPut(bookId) { mutableListOf() }.add(it)
                }

                // Aggregate tags
                cursor.getString(cursor.getColumnIndexOrThrow("tag_name"))?.let {
                    tagsMap.getOrPut(bookId) { mutableListOf() }.add(it)
                }
            }
            cursor.close()
            calibreDb.close()

            // Combine the aggregated data into the final map
            return booksMap.mapValues { (id, book) ->
                book.copy(
                    authorNames = authorsMap[id] ?: emptyList(),
                    tags = tagsMap[id] ?: emptyList(),
                )
            }
        }
    }
