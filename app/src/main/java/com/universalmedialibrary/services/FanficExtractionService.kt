package com.universalmedialibrary.services

import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FanficExtractionService @Inject constructor() {

    suspend fun extractAo3Work(url: String): ExtractedContent? {
        if (!url.contains("archiveofourown.org/works/")) {
            return null
        }

        val fullWorkUrl = if (url.contains("?")) "$url&view_full_work=true" else "$url?view_full_work=true"
        val doc = Jsoup.connect(fullWorkUrl).timeout(30000).get()
        return parseAo3Document(doc)
    }

    internal fun parseAo3Document(doc: org.jsoup.nodes.Document): ExtractedContent? {
        val title = doc.selectFirst("h2.title")?.text() ?: "Untitled"
        val author = doc.selectFirst("a[rel=author]")?.text() ?: "Unknown"
        val contentHtml = doc.selectFirst("#chapters")?.html() ?: ""

        if (contentHtml.isEmpty()) {
            return null
        }
        return ExtractedContent(title, contentHtml, author)
    }
}
