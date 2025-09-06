package com.universalmedialibrary.services

import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.*
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalibreImportService @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val metadataDao: MetadataDao
) {

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

            val mediaItem = MediaItem(
                libraryId = libraryId,
                filePath = fullPath,
                dateAdded = System.currentTimeMillis(),
                lastScanned = System.currentTimeMillis(),
                fileHash = ""
            )
            val newId = mediaItemDao.insertMediaItem(mediaItem)

            val metadataCommon = MetadataCommon(
                itemId = newId,
                title = cleanedTitle,
                sortTitle = sortTitle,
                year = null, releaseDate = null, rating = null, summary = null, coverImagePath = null
            )
            metadataDao.insertMetadataCommon(metadataCommon)

            val personId = metadataDao.insertPerson(cleanedAuthor)
            val itemPersonRole = ItemPersonRole(itemId = newId, personId = personId, role = "AUTHOR")
            metadataDao.insertItemPersonRole(itemPersonRole)
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

    private fun String.myCapitalize(): String {
        if (this.isEmpty()) return ""
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
