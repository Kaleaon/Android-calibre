package com.universalmedialibrary.services

import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

data class ExtractedContent(
    val title: String,
    val htmlContent: String,
    val author: String
)

@Singleton
class ContentExtractionService @Inject constructor() {

    suspend fun extractArticleContent(url: String): ExtractedContent {
        val doc = Jsoup.connect(url).timeout(30000).get()

        val title = doc.title()
        val author = doc.selectFirst("meta[property=og:site_name]")?.attr("content") ?:
                     doc.selectFirst("meta[name=author]")?.attr("content") ?:
                     java.net.URI(url).host ?: "Unknown"

        val mainContent = doc.select("article, main, [role=main]").firstOrNull() ?: doc.body()
        val htmlContent = mainContent.html()

        return ExtractedContent(title, htmlContent, author)
    }
}
