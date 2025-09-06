package com.universalmedialibrary.services

import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.*
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalibreImportService @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val metadataDao: MetadataDao,
    private val calibreReader: CalibreDatabaseReader
) {

    suspend fun importCalibreDatabase(calibreDbPath: String, libraryRootPath: String, libraryId: Long) {
        val rawBooks = calibreReader.readBooks(calibreDbPath)

        for ((_, rawBook) in rawBooks) {
            val file = File(resolveFullPath(libraryRootPath, rawBook.path) ?: continue)
            if (!file.exists()) {
                continue
            }
            val fullPath = file.absolutePath


            val cleanedTitle = cleanTitle(rawBook.title)
            val sortTitle = createSortTitle(cleanedTitle)

            val mediaItem = MediaItem(
                libraryId = libraryId,
                filePath = fullPath,
                dateAdded = System.currentTimeMillis(),
                lastScanned = System.currentTimeMillis(),
                fileHash = calculateMD5(file)
            )
            val newId = mediaItemDao.insertMediaItem(mediaItem)

            val metadataCommon = MetadataCommon(
                itemId = newId,
                title = cleanedTitle,
                sortTitle = sortTitle,
                year = null, // Not available from Calibre easily
                releaseDate = null, // Not available from Calibre easily
                rating = null, // Not available from Calibre easily
                summary = rawBook.comments,
                coverImagePath = null
            )
            metadataDao.insertMetadataCommon(metadataCommon)

            // Insert Book-specific metadata
            val metadataBook = MetadataBook(
                itemId = newId,
                subtitle = null, // Not available from Calibre easily
                publisher = rawBook.publisher,
                isbn = rawBook.isbn,
                pageCount = null, // Not available from Calibre easily
                seriesId = null // Will be handled next
            )
            metadataDao.insertMetadataBook(metadataBook)

            // Handle Authors
            for (authorName in rawBook.authorNames) {
                val cleanedAuthor = cleanAuthorName(authorName)
                val personId = metadataDao.findPersonByName(cleanedAuthor.name)
                    ?: metadataDao.insertPerson(cleanedAuthor)
                val itemPersonRole = ItemPersonRole(itemId = newId, personId = personId, role = "AUTHOR")
                metadataDao.insertItemPersonRole(itemPersonRole)
            }

            // Handle Series
            rawBook.seriesName?.let { seriesName ->
                val seriesId = metadataDao.findSeriesByName(seriesName)
                    ?: metadataDao.insertSeries(Series(name = seriesName))
                metadataDao.updateBookWithSeries(newId, seriesId)
            }

            // Handle Genres (from Tags)
            for (tagName in rawBook.tags) {
                val genreId = metadataDao.findGenreByName(tagName)
                    ?: metadataDao.insertGenre(Genre(name = tagName))
                metadataDao.insertItemGenre(ItemGenre(itemId = newId, genreId = genreId))
            }
        }
    }

    private fun resolveFullPath(libraryRootPath: String, relativePath: String): String? {
        val file = File(libraryRootPath, relativePath)
        if (file.isDirectory) {
            return file.listFiles()
                ?.firstOrNull { it.extension in listOf("epub", "mobi", "pdf") }
                ?.absolutePath
        }
        return file.absolutePath
    }

    private fun cleanTitle(rawTitle: String): String {
        return rawTitle.split(' ').joinToString(" ") { it.myCapitalize() }
    }

    private fun createSortTitle(title: String): String {
        val articles = listOf("The ", "A ", "An ")
        for (article in articles) {
            if (title.startsWith(article, ignoreCase = true)) {
                return title.substring(article.length) + ", " + title.substring(0, article.length - 1)
            }
        }
        return title
    }

    private fun cleanAuthorName(rawName: String): People {
        val (lastName, firstName) = if (rawName.contains(",")) {
            val parts = rawName.split(",", limit = 2).map { it.trim() }
            Pair(parts.getOrElse(0) { "" }, parts.getOrElse(1) { "" })
        } else {
            val parts = rawName.split(" ").filter { it.isNotBlank() }
            Pair(parts.lastOrNull() ?: "", parts.dropLast(1).joinToString(" "))
        }

        val finalFirstName = firstName.myCapitalize()
        val finalLastName = lastName.myCapitalize()

        val cleanName = "$finalFirstName $finalLastName".trim()
        val sortName = "$finalLastName, $finalFirstName".trim().removeSuffix(",").trim()
        return People(personId = 0, name = cleanName, sortName = sortName)
    }

    private fun String.myCapitalize(): String {
        if (this.isEmpty()) return ""
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    private fun calculateMD5(file: File): String {
        val digest = MessageDigest.getInstance("MD5")
        val inputStream = FileInputStream(file)
        val buffer = ByteArray(8192)
        var read: Int
        while (inputStream.read(buffer).also { read = it } > 0) {
            digest.update(buffer, 0, read)
        }
        val md5sum = digest.digest()
        return md5sum.joinToString("") { "%02x".format(it) }
    }
}
