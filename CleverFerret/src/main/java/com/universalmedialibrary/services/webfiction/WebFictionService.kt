package com.universalmedialibrary.services.webfiction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

// Data models for web fiction
data class WebFictionStory(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val url: String,
    val site: WebFictionSite,
    val chapters: List<WebFictionChapter> = emptyList(),
    val totalChapters: Int = 0,
    val lastUpdated: String? = null,
    val status: String = "Unknown", // Complete, In-Progress, Hiatus
    val rating: String? = null,
    val tags: List<String> = emptyList(),
    val coverUrl: String? = null,
    val wordCount: Long = 0
)

data class WebFictionChapter(
    val id: String,
    val title: String,
    val url: String,
    val content: String,
    val chapterNumber: Int,
    val publishDate: String? = null,
    val wordCount: Int = 0
)

enum class WebFictionSite(
    val displayName: String,
    val baseUrl: String,
    val supportsUpdate: Boolean = true
) {
    ARCHIVE_OF_OUR_OWN("Archive of Our Own", "https://archiveofourown.org", true),
    FANFICTION_NET("FanFiction.Net", "https://www.fanfiction.net", true),
    ROYAL_ROAD("Royal Road", "https://www.royalroad.com", true),
    WEBNOVEL("WebNovel", "https://www.webnovel.com", true),
    WATTPAD("Wattpad", "https://www.wattpad.com", true),
    SCRIBBLE_HUB("Scribble Hub", "https://www.scribblehub.com", true),
    SPACEBATTLES("SpaceBattles", "https://forums.spacebattles.com", true),
    SUFFICIENT_VELOCITY("Sufficient Velocity", "https://forums.sufficientvelocity.com", true),
    QUESTIONABLE_QUESTING("Questionable Questing", "https://forum.questionablequesting.com", true),
    FIMFICTION("FimFiction", "https://www.fimfiction.net", true),
    LITEROTICA("Literotica", "https://www.literotica.com", false), // Adult content
    GENERIC("Generic Web Fiction", "", true) // For custom/unsupported sites
}

// API interfaces for sites that have them
interface RoyalRoadApi {
    @GET("fiction/{fictionId}")
    suspend fun getFiction(@Path("fictionId") fictionId: String): RoyalRoadFiction
    
    @GET("fiction/{fictionId}/chapters")
    suspend fun getChapters(@Path("fictionId") fictionId: String): List<RoyalRoadChapter>
}

data class RoyalRoadFiction(
    val id: String,
    val title: String,
    val author: RoyalRoadAuthor,
    val description: String,
    val cover: String?,
    val tags: List<String>,
    val status: String,
    val chapters: Int,
    val pages: Int,
    val followers: Int,
    val favorites: Int,
    val rating: Float
)

data class RoyalRoadAuthor(
    val name: String,
    val id: String
)

data class RoyalRoadChapter(
    val id: String,
    val title: String,
    val url: String,
    val order: Int,
    val content: String? = null
)

@Singleton
class WebFictionService @Inject constructor() {
    
    private val royalRoadApi: RoyalRoadApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.royalroad.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RoyalRoadApi::class.java)
    }

    /**
     * Extract story information from a URL
     */
    suspend fun extractStoryFromUrl(url: String): WebFictionStory? {
        return withContext(Dispatchers.IO) {
            try {
                val site = detectSite(url)
                when (site) {
                    WebFictionSite.ARCHIVE_OF_OUR_OWN -> extractFromAO3(url)
                    WebFictionSite.FANFICTION_NET -> extractFromFFN(url)
                    WebFictionSite.ROYAL_ROAD -> extractFromRoyalRoad(url)
                    WebFictionSite.WEBNOVEL -> extractFromWebnovel(url)
                    WebFictionSite.WATTPAD -> extractFromWattpad(url)
                    WebFictionSite.SCRIBBLE_HUB -> extractFromScribbleHub(url)
                    WebFictionSite.FIMFICTION -> extractFromFimFiction(url)
                    else -> extractGeneric(url)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Check for new chapters in an existing story
     */
    suspend fun checkForUpdates(story: WebFictionStory): List<WebFictionChapter> {
        return withContext(Dispatchers.IO) {
            try {
                val currentStory = extractStoryFromUrl(story.url) ?: return@withContext emptyList()
                
                // Find chapters that weren't in the original story
                val existingChapterIds = story.chapters.map { it.id }.toSet()
                currentStory.chapters.filter { it.id !in existingChapterIds }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Download all chapters of a story
     */
    suspend fun downloadAllChapters(story: WebFictionStory): List<WebFictionChapter> {
        return withContext(Dispatchers.IO) {
            try {
                when (story.site) {
                    WebFictionSite.ARCHIVE_OF_OUR_OWN -> downloadAO3Chapters(story)
                    WebFictionSite.FANFICTION_NET -> downloadFFNChapters(story)
                    WebFictionSite.ROYAL_ROAD -> downloadRoyalRoadChapters(story)
                    WebFictionSite.WEBNOVEL -> downloadWebnovelChapters(story)
                    WebFictionSite.WATTPAD -> downloadWattpadChapters(story)
                    else -> emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private fun detectSite(url: String): WebFictionSite {
        val domain = URL(url).host.lowercase()
        return when {
            "archiveofourown.org" in domain -> WebFictionSite.ARCHIVE_OF_OUR_OWN
            "fanfiction.net" in domain -> WebFictionSite.FANFICTION_NET
            "royalroad.com" in domain -> WebFictionSite.ROYAL_ROAD
            "webnovel.com" in domain -> WebFictionSite.WEBNOVEL
            "wattpad.com" in domain -> WebFictionSite.WATTPAD
            "scribblehub.com" in domain -> WebFictionSite.SCRIBBLE_HUB
            "spacebattles.com" in domain -> WebFictionSite.SPACEBATTLES
            "sufficientvelocity.com" in domain -> WebFictionSite.SUFFICIENT_VELOCITY
            "questionablequesting.com" in domain -> WebFictionSite.QUESTIONABLE_QUESTING
            "fimfiction.net" in domain -> WebFictionSite.FIMFICTION
            else -> WebFictionSite.GENERIC
        }
    }

    // Archive of Our Own scraper
    private suspend fun extractFromAO3(url: String): WebFictionStory? {
        val doc = Jsoup.connect(url).get()
        
        val title = doc.select("h2.title").text()
        val author = doc.select("a[rel=author]").text()
        val description = doc.select("div.summary blockquote").text()
        val tags = doc.select("dd.freeform a.tag").map { it.text() }
        val rating = doc.select("dd.rating a.tag").text()
        val status = doc.select("dd.status a.tag").text()
        val chapterCount = doc.select("dd.chapters").text().split("/")[0].toIntOrNull() ?: 1
        
        val storyId = extractAO3Id(url)
        
        return WebFictionStory(
            id = storyId,
            title = title,
            author = author,
            description = description,
            url = url,
            site = WebFictionSite.ARCHIVE_OF_OUR_OWN,
            totalChapters = chapterCount,
            status = status,
            rating = rating,
            tags = tags
        )
    }

    // FanFiction.Net scraper
    private suspend fun extractFromFFN(url: String): WebFictionStory? {
        val doc = Jsoup.connect(url).get()
        
        val title = doc.select("b.xcontrast_txt").text()
        val author = doc.select("a.xcontrast_txt").first()?.text() ?: "Unknown"
        val description = doc.select("div#profile_top div.xcontrast_txt").text()
        val storyInfo = doc.select("span.xgray").text()
        
        // Parse story info (Rating, Language, Chapters, etc.)
        val chapterCount = Regex("Chapters: (\\d+)").find(storyInfo)?.groupValues?.get(1)?.toIntOrNull() ?: 1
        val status = if ("Complete" in storyInfo) "Complete" else "In-Progress"
        
        val storyId = extractFFNId(url)
        
        return WebFictionStory(
            id = storyId,
            title = title,
            author = author,
            description = description,
            url = url,
            site = WebFictionSite.FANFICTION_NET,
            totalChapters = chapterCount,
            status = status
        )
    }

    // Royal Road scraper
    private suspend fun extractFromRoyalRoad(url: String): WebFictionStory? {
        val doc = Jsoup.connect(url).get()
        
        val title = doc.select("h1").text()
        val author = doc.select("h4 a[href*=/profile]").text()
        val description = doc.select("div.description").text()
        val coverUrl = doc.select("img.thumbnail").attr("src")
        val tags = doc.select("span.tags a").map { it.text() }
        val status = doc.select("span.label").text()
        val chapterCount = doc.select("tbody tr").size
        
        val storyId = extractRoyalRoadId(url)
        
        return WebFictionStory(
            id = storyId,
            title = title,
            author = author,
            description = description,
            url = url,
            site = WebFictionSite.ROYAL_ROAD,
            totalChapters = chapterCount,
            status = status,
            tags = tags,
            coverUrl = coverUrl
        )
    }

    // WebNovel scraper
    private suspend fun extractFromWebnovel(url: String): WebFictionStory? {
        val doc = Jsoup.connect(url).get()
        
        val title = doc.select("h1.pt4").text()
        val author = doc.select("address a").text()
        val description = doc.select("p").first { it.text().isNotEmpty() }?.text() ?: ""
        val coverUrl = doc.select("i.g_thumb img").attr("src")
        val tags = doc.select("p.tags a").map { it.text() }
        
        val storyId = extractWebnovelId(url)
        
        return WebFictionStory(
            id = storyId,
            title = title,
            author = author,
            description = description,
            url = url,
            site = WebFictionSite.WEBNOVEL,
            tags = tags,
            coverUrl = coverUrl
        )
    }

    // Wattpad scraper
    private suspend fun extractFromWattpad(url: String): WebFictionStory? {
        // Wattpad requires more complex handling due to dynamic loading
        val doc = Jsoup.connect(url).get()
        
        val title = doc.select("h1").text()
        val author = doc.select("a.username").text()
        val description = doc.select("h2.description").text()
        val coverUrl = doc.select("img.cover").attr("src")
        val tags = doc.select("div.tags a").map { it.text() }
        
        val storyId = extractWattpadId(url)
        
        return WebFictionStory(
            id = storyId,
            title = title,
            author = author,
            description = description,
            url = url,
            site = WebFictionSite.WATTPAD,
            tags = tags,
            coverUrl = coverUrl
        )
    }

    // ScribbleHub scraper
    private suspend fun extractFromScribbleHub(url: String): WebFictionStory? {
        val doc = Jsoup.connect(url).get()
        
        val title = doc.select("div.fic_title").text()
        val author = doc.select("span.auth_name_fic").text()
        val description = doc.select("div.wi_fic_desc").text()
        val coverUrl = doc.select("div.fic_image img").attr("src")
        val tags = doc.select("a.fic_genre").map { it.text() }
        val status = doc.select("span.pub_status").text()
        
        val storyId = extractScribbleHubId(url)
        
        return WebFictionStory(
            id = storyId,
            title = title,
            author = author,
            description = description,
            url = url,
            site = WebFictionSite.SCRIBBLE_HUB,
            status = status,
            tags = tags,
            coverUrl = coverUrl
        )
    }

    // FimFiction scraper
    private suspend fun extractFromFimFiction(url: String): WebFictionStory? {
        val doc = Jsoup.connect(url).get()
        
        val title = doc.select("h1 a").text()
        val author = doc.select("div.author a").text()
        val description = doc.select("div.description").text()
        val coverUrl = doc.select("div.story_image img").attr("src")
        val tags = doc.select("a.character_tag, a.content_tag").map { it.text() }
        val status = doc.select("span.completion_status").text()
        
        val storyId = extractFimFictionId(url)
        
        return WebFictionStory(
            id = storyId,
            title = title,
            author = author,
            description = description,
            url = url,
            site = WebFictionSite.FIMFICTION,
            status = status,
            tags = tags,
            coverUrl = coverUrl
        )
    }

    // Generic scraper for unsupported sites
    private suspend fun extractGeneric(url: String): WebFictionStory? {
        val doc = Jsoup.connect(url).get()
        
        // Try common selectors
        val title = doc.select("h1").first()?.text() ?: doc.title()
        val description = doc.select("meta[name=description]").attr("content")
        
        val storyId = url.hashCode().toString()
        
        return WebFictionStory(
            id = storyId,
            title = title,
            author = "Unknown",
            description = description,
            url = url,
            site = WebFictionSite.GENERIC
        )
    }

    // Chapter downloading methods
    private suspend fun downloadAO3Chapters(story: WebFictionStory): List<WebFictionChapter> {
        val chapters = mutableListOf<WebFictionChapter>()
        
        // AO3 allows downloading entire work at once
        val fullWorkUrl = "${story.url}?view_full_work=true"
        val doc = Jsoup.connect(fullWorkUrl).get()
        
        val chapterElements = doc.select("div.chapter")
        chapterElements.forEachIndexed { index, element ->
            val chapterTitle = element.select("h3.landmark").text()
            val content = element.select("div.userstuff").html()
            
            chapters.add(
                WebFictionChapter(
                    id = "${story.id}_${index + 1}",
                    title = chapterTitle.ifEmpty { "Chapter ${index + 1}" },
                    url = "${story.url}#chapter-${index + 1}",
                    content = content,
                    chapterNumber = index + 1
                )
            )
        }
        
        return chapters
    }

    private suspend fun downloadFFNChapters(story: WebFictionStory): List<WebFictionChapter> {
        val chapters = mutableListOf<WebFictionChapter>()
        
        for (chapterNum in 1..story.totalChapters) {
            val chapterUrl = "${story.url.removeSuffix("/1")}/$chapterNum"
            val doc = Jsoup.connect(chapterUrl).get()
            
            val chapterTitle = doc.select("select option[selected]").text()
            val content = doc.select("div#storytext").html()
            
            chapters.add(
                WebFictionChapter(
                    id = "${story.id}_$chapterNum",
                    title = chapterTitle.ifEmpty { "Chapter $chapterNum" },
                    url = chapterUrl,
                    content = content,
                    chapterNumber = chapterNum
                )
            )
        }
        
        return chapters
    }

    private suspend fun downloadRoyalRoadChapters(story: WebFictionStory): List<WebFictionChapter> {
        val chapters = mutableListOf<WebFictionChapter>()
        
        // Get chapter list from table of contents
        val tocUrl = story.url
        val doc = Jsoup.connect(tocUrl).get()
        
        val chapterLinks = doc.select("tbody tr td a[href*=/chapter/]")
        
        chapterLinks.forEachIndexed { index, link ->
            val chapterUrl = "https://www.royalroad.com${link.attr("href")}"
            val chapterTitle = link.text()
            
            val chapterDoc = Jsoup.connect(chapterUrl).get()
            val content = chapterDoc.select("div.chapter-content").html()
            
            chapters.add(
                WebFictionChapter(
                    id = "${story.id}_${index + 1}",
                    title = chapterTitle,
                    url = chapterUrl,
                    content = content,
                    chapterNumber = index + 1
                )
            )
        }
        
        return chapters
    }

    private suspend fun downloadWebnovelChapters(story: WebFictionStory): List<WebFictionChapter> {
        // WebNovel often requires login and has anti-scraping measures
        // This would need more sophisticated handling
        return emptyList()
    }

    private suspend fun downloadWattpadChapters(story: WebFictionStory): List<WebFictionChapter> {
        // Wattpad requires handling dynamic content loading
        // This would need more sophisticated handling
        return emptyList()
    }

    // Helper methods to extract IDs from URLs
    private fun extractAO3Id(url: String): String {
        return Regex("works/(\\d+)").find(url)?.groupValues?.get(1) ?: url.hashCode().toString()
    }

    private fun extractFFNId(url: String): String {
        return Regex("s/(\\d+)").find(url)?.groupValues?.get(1) ?: url.hashCode().toString()
    }

    private fun extractRoyalRoadId(url: String): String {
        return Regex("fiction/(\\d+)").find(url)?.groupValues?.get(1) ?: url.hashCode().toString()
    }

    private fun extractWebnovelId(url: String): String {
        return Regex("book/(\\d+)").find(url)?.groupValues?.get(1) ?: url.hashCode().toString()
    }

    private fun extractWattpadId(url: String): String {
        return Regex("story/(\\d+)").find(url)?.groupValues?.get(1) ?: url.hashCode().toString()
    }

    private fun extractScribbleHubId(url: String): String {
        return Regex("series/(\\d+)").find(url)?.groupValues?.get(1) ?: url.hashCode().toString()
    }

    private fun extractFimFictionId(url: String): String {
        return Regex("story/(\\d+)").find(url)?.groupValues?.get(1) ?: url.hashCode().toString()
    }
}