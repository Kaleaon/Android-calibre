package com.universalmedialibrary.services

import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.*
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles the import of a Calibre library into the application's database.
 *
 * This service orchestrates the process of reading from a Calibre `metadata.db` file,
 * cleaning and transforming the data, and inserting it into the app's Room database.
 *
 * @property mediaItemDao DAO for media item operations.
 * @property metadataDao DAO for metadata operations.
 */
@Singleton
class CalibreImportService @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val metadataDao: MetadataDao
) {

    /**
     * Imports a Calibre library from the given database path.
     *
     * @param calibreDbPath The absolute path to the Calibre `metadata.db` file.
     * @param libraryRootPath The absolute path to the root folder of the Calibre library.
     * @param libraryId The ID of the app's library where the books will be imported.
     */
    suspend fun importCalibreDatabase(calibreDbPath: String, libraryRootPath: String, libraryId: Long) {
        val calibreReader = CalibreDatabaseReader()
        val rawBooks = calibreReader.readBooks(calibreDbPath)

        for ((_, rawBook) in rawBooks) {
            val fullPath = resolveFullPath(libraryRootPath, rawBook.path)
            if (fullPath == null || !File(fullPath).exists()) {
                continue
            }

            val cleanedTitle = cleanTitle(rawBook.title)
            val sortTitle = createSortTitle(cleanedTitle)
            val cleanedAuthor = cleanAuthorName(rawBook.authorName ?: "Unknown")

            // Create and insert the main media item
            val mediaItem = MediaItem(
                libraryId = libraryId,
                filePath = fullPath,
                dateAdded = System.currentTimeMillis(),
                lastScanned = System.currentTimeMillis(),
                fileHash = "" // Hashing can be implemented later
            )
            val newId = mediaItemDao.insertMediaItem(mediaItem)

            // Create and insert common metadata
            val metadataCommon = MetadataCommon(
                itemId = newId,
                title = cleanedTitle,
                sortTitle = sortTitle,
                year = null, releaseDate = null, rating = null, summary = null, coverImagePath = null
            )
            metadataDao.insertMetadataCommon(metadataCommon)

            // Create and insert author and link to the media item
            val personId = metadataDao.insertPerson(cleanedAuthor)
            val itemPersonRole = ItemPersonRole(itemId = newId, personId = personId, role = "AUTHOR")
            metadataDao.insertItemPersonRole(itemPersonRole)
        }
    }

    /**
     * Resolves the full path to a media file from the Calibre relative path.
     *
     * Calibre stores a path to the book's folder. This function finds the actual
     * e-book file within that folder.
     *
     * @param libraryRootPath The root path of the Calibre library.
     * @param relativePath The relative path from the Calibre database.
     * @return The absolute path to the e-book file, or null if not found.
     */
    private fun resolveFullPath(libraryRootPath: String, relativePath: String): String? {
        val file = File(libraryRootPath, relativePath)
        if (file.isDirectory) {
            // Find the first supported file type in the directory
            return file.listFiles()
                ?.firstOrNull { it.extension in listOf("epub", "mobi", "pdf") }
                ?.absolutePath
        }
        return file.absolutePath
    }

    /**
     * Applies title case capitalization to a string.
     * @param rawTitle The raw title string.
     * @return The cleaned title.
     */
    private fun cleanTitle(rawTitle: String): String {
        return rawTitle.split(' ').joinToString(" ") { it.myCapitalize() }
    }

    /**
     * Creates a sortable title by moving leading articles to the end.
     * Example: "The Hobbit" -> "Hobbit, The"
     * @param title The cleaned title.
     * @return The sortable title string.
     */
    private fun createSortTitle(title: String): String {
        val articles = listOf("The ", "A ", "An ")
        for (article in articles) {
            if (title.startsWith(article, ignoreCase = true)) {
                return title.substring(article.length) + ", " + title.substring(0, article.length - 1)
            }
        }
        return title
    }

    /**
     * Cleans and standardizes an author's name from Calibre.
     * Handles "Last, First" and "First Last" formats.
     * @param rawName The raw author name string.
     * @return A [People] object with standardized name and sortName.
     */
    private fun cleanAuthorName(rawName: String): People {
        val nameParts = if (rawName.contains(",")) {
            rawName.split(",").map { it.trim() }
        } else {
            val parts = rawName.split(" ").map { it.trim() }
            listOf(parts.dropLast(1).joinToString(" "), parts.last())
        }
        val firstName = nameParts.getOrNull(1)?.myCapitalize() ?: ""
        val lastName = nameParts.getOrNull(0)?.myCapitalize() ?: ""
        val cleanName = "$firstName $lastName".trim()
        val sortName = "$lastName, $firstName".trim()
        return People(personId = 0, name = cleanName, sortName = sortName)
    }

    /**
     * Capitalizes the first letter of a string.
     */
    private fun String.myCapitalize(): String {
        if (this.isEmpty()) return ""
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
