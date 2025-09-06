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
        // Safety: Validate inputs
        if (calibreDbPath.isBlank() || libraryRootPath.isBlank() || libraryId <= 0) {
            return // Early return for invalid inputs
        }
        
        val rawBooks = calibreReader.readBooks(calibreDbPath)

        for ((_, rawBook) in rawBooks) {
            try {
                val resolvedPath = resolveFullPath(libraryRootPath, rawBook.path) ?: continue
                val file = File(resolvedPath)
                if (!file.exists()) {
                    continue
                }
                val fullPath = file.absolutePath

                // Safety: Validate title is not empty
                val rawTitle = rawBook.title.takeIf { it.isNotBlank() } ?: "Unknown Title"
                val cleanedTitle = cleanTitle(rawTitle)
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

                // Handle Authors - Safety: validate author names
                for (authorName in rawBook.authorNames) {
                    val trimmedAuthor = authorName.trim()
                    if (trimmedAuthor.isNotBlank()) {
                        val cleanedAuthor = cleanAuthorName(trimmedAuthor)
                        val personId = metadataDao.findPersonByName(cleanedAuthor.name)
                            ?: metadataDao.insertPerson(cleanedAuthor)
                        val itemPersonRole = ItemPersonRole(itemId = newId, personId = personId, role = "AUTHOR")
                        metadataDao.insertItemPersonRole(itemPersonRole)
                    }
                }

                // Handle Series
                rawBook.seriesName?.takeIf { it.isNotBlank() }?.let { seriesName ->
                    val seriesId = metadataDao.findSeriesByName(seriesName)
                        ?: metadataDao.insertSeries(Series(name = seriesName))
                    metadataDao.updateBookWithSeries(newId, seriesId)
                }

                // Handle Genres (from Tags) - Safety: validate tag names
                for (tagName in rawBook.tags) {
                    val trimmedTag = tagName.trim()
                    if (trimmedTag.isNotBlank()) {
                        val genreId = metadataDao.findGenreByName(trimmedTag)
                            ?: metadataDao.insertGenre(Genre(name = trimmedTag))
                        metadataDao.insertItemGenre(ItemGenre(itemId = newId, genreId = genreId))
                    }
                }
            } catch (e: Exception) {
                // Safety: Continue processing other books if one fails
                // In production, this would be logged
                continue
            }
        }
    }

    private fun resolveFullPath(libraryRootPath: String, relativePath: String): String? {
        // Safety: Validate inputs
        if (libraryRootPath.isBlank() || relativePath.isBlank()) {
            return null
        }
        
        return try {
            val file = File(libraryRootPath, relativePath)
            if (file.isDirectory) {
                file.listFiles()
                    ?.firstOrNull { it.extension.lowercase() in listOf("epub", "mobi", "pdf") }
                    ?.absolutePath
            } else if (file.exists()) {
                file.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            // Safety: Handle file system errors gracefully
            null
        }
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
        // Safety: Handle empty or blank names
        val trimmedName = rawName.trim()
        if (trimmedName.isBlank()) {
            return People(personId = 0, name = "Unknown Author", sortName = "Unknown Author")
        }
        
        val (lastName, firstName) = if (trimmedName.contains(",")) {
            val parts = trimmedName.split(",", limit = 2).map { it.trim() }
            // Safety: Use safe indexing with proper fallbacks, filter out non-alphanumeric parts
            val safePart0 = parts.getOrNull(0)?.takeIf { it.isNotBlank() && it.any { char -> char.isLetterOrDigit() } } ?: "Unknown"
            val safePart1 = parts.getOrNull(1)?.takeIf { it.isNotBlank() && it.any { char -> char.isLetterOrDigit() } } ?: ""
            Pair(safePart0, safePart1)
        } else {
            val parts = trimmedName.split(" ").filter { it.isNotBlank() }
            // Safety: Handle single name or empty parts list
            when {
                parts.isEmpty() -> Pair("Unknown", "")
                parts.size == 1 -> Pair(parts[0], "")
                else -> Pair(parts.lastOrNull() ?: "", parts.dropLast(1).joinToString(" "))
            }
        }

        val finalFirstName = firstName.myCapitalize()
        val finalLastName = lastName.myCapitalize()

        val cleanName = "$finalFirstName $finalLastName".trim()
        val sortName = if (finalFirstName.isNotBlank()) {
            "$finalLastName, $finalFirstName".trim().removeSuffix(",").trim()
        } else {
            finalLastName
        }
        return People(personId = 0, name = cleanName, sortName = sortName)
    }

    private fun String.myCapitalize(): String {
        if (this.isEmpty()) return ""
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    private fun calculateMD5(file: File): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            FileInputStream(file).use { inputStream ->
                val buffer = ByteArray(8192)
                var read: Int
                while (inputStream.read(buffer).also { read = it } > 0) {
                    digest.update(buffer, 0, read)
                }
            }
            val md5sum = digest.digest()
            md5sum.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            // Safety: Return empty string or a default value on error
            // In production, this might be logged
            ""
        }
    }
}
