package com.universalmedialibrary.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universalmedialibrary.data.local.dao.MediaItemDao
import com.universalmedialibrary.data.local.dao.MetadataDao
import com.universalmedialibrary.data.local.model.*
import com.universalmedialibrary.services.ContentExtractionService
import com.universalmedialibrary.services.EpubCreationService
import com.universalmedialibrary.services.FanficExtractionService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ContentImportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentExtractionService: ContentExtractionService,
    private val fanficExtractionService: FanficExtractionService,
    private val epubCreationService: EpubCreationService,
    private val mediaItemDao: MediaItemDao,
    private val metadataDao: MetadataDao
) : ViewModel() {

    fun importFromUrl(url: String, libraryId: Long) {
        viewModelScope.launch {
            val extractedContent = if (url.contains("archiveofourown.org")) {
                fanficExtractionService.extractAo3Work(url)
            } else {
                contentExtractionService.extractArticleContent(url)
            }

            if (extractedContent != null) {
                val safeTitle = extractedContent.title.replace(Regex("[/\\\\?%*:|\"<>]"), "_")
                val fileName = "${safeTitle}.epub"
                val file = File(context.filesDir, fileName)

                epubCreationService.createEpubFile(
                    extractedContent.title,
                    extractedContent.author,
                    extractedContent.htmlContent,
                    file.absolutePath
                )

                val mediaItem = MediaItem(
                    libraryId = libraryId,
                    filePath = file.absolutePath,
                    dateAdded = System.currentTimeMillis(),
                    lastScanned = System.currentTimeMillis(),
                    fileHash = ""
                )
                val newId = mediaItemDao.insertMediaItem(mediaItem)

                val metadataCommon = MetadataCommon(
                    itemId = newId,
                    title = extractedContent.title,
                    sortTitle = createSortTitle(extractedContent.title),
                    summary = "Imported from URL.",
                    year = null, releaseDate = null, rating = null, coverImagePath = null
                )
                metadataDao.insertMetadataCommon(metadataCommon)

                val person = People(name = extractedContent.author, sortName = extractedContent.author)
                val personId = metadataDao.insertPerson(person)
                val itemPersonRole = ItemPersonRole(itemId = newId, personId = personId, role = "AUTHOR")
                metadataDao.insertItemPersonRole(itemPersonRole)
            }
        }
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
}
