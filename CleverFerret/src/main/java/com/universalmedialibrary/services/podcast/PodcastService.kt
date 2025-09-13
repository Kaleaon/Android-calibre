package com.universalmedialibrary.services.podcast

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// Data models for podcasts
data class Podcast(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val feedUrl: String,
    val websiteUrl: String? = null,
    val imageUrl: String? = null,
    val category: String? = null,
    val language: String = "en",
    val isSubscribed: Boolean = false,
    val lastUpdated: Date? = null,
    val episodes: List<PodcastEpisode> = emptyList(),
    val totalEpisodes: Int = 0,
    val explicit: Boolean = false
)

data class PodcastEpisode(
    val id: String,
    val title: String,
    val description: String,
    val audioUrl: String,
    val duration: Long = 0, // in seconds
    val fileSize: Long = 0, // in bytes
    val publishDate: Date,
    val isDownloaded: Boolean = false,
    val isPlayed: Boolean = false,
    val localFilePath: String? = null,
    val episodeNumber: Int? = null,
    val seasonNumber: Int? = null,
    val imageUrl: String? = null,
    val chapterMarks: List<ChapterMark> = emptyList()
)

data class ChapterMark(
    val title: String,
    val startTime: Long, // in seconds
    val url: String? = null
)

data class PodcastSearchResult(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val feedUrl: String,
    val imageUrl: String?,
    val episodeCount: Int,
    val category: String?
)

// RSS/XML parsing models
data class RSSFeed(
    val title: String,
    val description: String,
    val link: String,
    val imageUrl: String?,
    val language: String?,
    val author: String?,
    val category: String?,
    val explicit: Boolean,
    val items: List<RSSItem>
)

data class RSSItem(
    val title: String,
    val description: String,
    val link: String?,
    val audioUrl: String?,
    val duration: String?,
    val fileSize: Long?,
    val pubDate: String?,
    val guid: String?,
    val episodeNumber: Int?,
    val seasonNumber: Int?,
    val imageUrl: String?
)

// Comprehensive Podcast APIs - covering all major services
interface PodcastIndexApi {
    @GET("search/byterm")
    suspend fun searchPodcasts(
        @Query("q") query: String,
        @Query("max") maxResults: Int = 20
    ): PodcastSearchResponse
    
    @GET("episodes/byfeedurl")
    suspend fun getEpisodesByFeedUrl(
        @Query("url") feedUrl: String,
        @Query("max") maxResults: Int = 100
    ): EpisodeSearchResponse
}

interface ListenNotesApi {
    @GET("search")
    suspend fun searchPodcasts(
        @Query("q") query: String,
        @Query("type") type: String = "podcast",
        @Query("page_size") pageSize: Int = 20
    ): ListenNotesResponse
    
    @GET("podcasts/{id}")
    suspend fun getPodcastById(@Path("id") id: String): ListenNotesPodcast
}

interface iTunesSearchApi {
    @GET("search")
    suspend fun searchPodcasts(
        @Query("term") term: String,
        @Query("media") media: String = "podcast",
        @Query("limit") limit: Int = 20
    ): iTunesSearchResponse
    
    @GET("lookup")
    suspend fun lookupPodcast(@Query("id") id: String): iTunesSearchResponse
}

interface SpotifyPodcastApi {
    @GET("search")
    suspend fun searchPodcasts(
        @Query("q") query: String,
        @Query("type") type: String = "show",
        @Query("limit") limit: Int = 20,
        @Header("Authorization") authorization: String
    ): SpotifySearchResponse
    
    @GET("shows/{id}")
    suspend fun getPodcastById(
        @Path("id") id: String,
        @Header("Authorization") authorization: String
    ): SpotifyPodcast
}

interface TaddyPodcastApi {
    @GET("search")
    suspend fun searchPodcasts(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20,
        @Header("X-API-KEY") apiKey: String
    ): TaddySearchResponse
}

data class PodcastSearchResponse(
    val status: String,
    val feeds: List<PodcastSearchFeed>,
    val count: Int
)

data class PodcastSearchFeed(
    val id: Long,
    val title: String,
    val url: String,
    val originalUrl: String,
    val link: String,
    val description: String,
    val author: String,
    val ownerName: String,
    val image: String,
    val artwork: String,
    val episodeCount: Int,
    val categories: Map<String, String>
)

data class EpisodeSearchResponse(
    val status: String,
    val items: List<PodcastSearchEpisode>,
    val count: Int
)

data class PodcastSearchEpisode(
    val id: Long,
    val title: String,
    val link: String,
    val description: String,
    val guid: String,
    val datePublished: Long,
    val enclosureUrl: String,
    val enclosureType: String,
    val enclosureLength: Long,
    val duration: Int,
    val explicit: Int,
    val episode: Int?,
    val season: Int?,
    val image: String,
    val feedImage: String
)

@Singleton
class PodcastService @Inject constructor(
    private val context: Context
) {
    
    private val httpClient = OkHttpClient.Builder().build()
    
    private val podcastIndexApi: PodcastIndexApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.podcastindex.org/api/1.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(PodcastIndexApi::class.java)
    }

    /**
     * Search for podcasts using Podcast Index API
     */
    suspend fun searchPodcasts(query: String): List<PodcastSearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response = podcastIndexApi.searchPodcasts(query)
                response.feeds.map { feed ->
                    PodcastSearchResult(
                        id = feed.id.toString(),
                        title = feed.title,
                        author = feed.author.ifEmpty { feed.ownerName },
                        description = feed.description,
                        feedUrl = feed.url,
                        imageUrl = feed.image.ifEmpty { feed.artwork },
                        episodeCount = feed.episodeCount,
                        category = feed.categories.values.firstOrNull()
                    )
                }
            } catch (e: Exception) {
                // Fallback to manual RSS discovery
                searchPodcastsByRSSDiscovery(query)
            }
        }
    }

    /**
     * Subscribe to a podcast by RSS feed URL
     */
    suspend fun subscribeToPodcast(feedUrl: String): Podcast? {
        return withContext(Dispatchers.IO) {
            try {
                val rssFeed = parseRSSFeed(feedUrl)
                val podcastId = generatePodcastId(feedUrl)
                
                Podcast(
                    id = podcastId,
                    title = rssFeed.title,
                    description = rssFeed.description,
                    author = rssFeed.author ?: "Unknown",
                    feedUrl = feedUrl,
                    websiteUrl = rssFeed.link,
                    imageUrl = rssFeed.imageUrl,
                    category = rssFeed.category,
                    language = rssFeed.language ?: "en",
                    isSubscribed = true,
                    lastUpdated = Date(),
                    episodes = convertRSSItemsToEpisodes(rssFeed.items, podcastId),
                    totalEpisodes = rssFeed.items.size,
                    explicit = rssFeed.explicit
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Check for new episodes in subscribed podcasts
     */
    suspend fun checkForNewEpisodes(podcast: Podcast): List<PodcastEpisode> {
        return withContext(Dispatchers.IO) {
            try {
                val rssFeed = parseRSSFeed(podcast.feedUrl)
                val currentEpisodes = convertRSSItemsToEpisodes(rssFeed.items, podcast.id)
                
                // Find episodes not in the existing list
                val existingGuids = podcast.episodes.map { it.id }.toSet()
                currentEpisodes.filter { it.id !in existingGuids }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Download a podcast episode
     */
    suspend fun downloadEpisode(episode: PodcastEpisode, podcastTitle: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(episode.audioUrl).build()
                val response = httpClient.newCall(request).execute()
                
                if (!response.isSuccessful) return@withContext null
                
                // Create download directory
                val podcastDir = File(context.getExternalFilesDir("podcasts"), sanitizeFileName(podcastTitle))
                podcastDir.mkdirs()
                
                // Generate filename
                val fileName = "${sanitizeFileName(episode.title)}.${getFileExtension(episode.audioUrl)}"
                val file = File(podcastDir, fileName)
                
                // Download file
                response.body?.byteStream()?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                
                file.absolutePath
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Get episode transcripts (if available)
     */
    suspend fun getEpisodeTranscript(episode: PodcastEpisode): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Many podcasts include transcript URLs in their RSS feeds
                // This would need specific parsing for different podcast providers
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Parse RSS feed from URL
     */
    private suspend fun parseRSSFeed(feedUrl: String): RSSFeed {
        val request = Request.Builder().url(feedUrl).build()
        val response = httpClient.newCall(request).execute()
        val xml = response.body?.string() ?: throw Exception("Empty RSS feed")
        
        val doc = Jsoup.parse(xml, "", Parser.xmlParser())
        
        // Parse channel info
        val channel = doc.select("channel").first()
            ?: throw Exception("Invalid RSS feed - no channel found")
        
        val title = channel.select("title").text()
        val description = channel.select("description").text()
        val link = channel.select("link").text()
        val imageUrl = channel.select("image url").text().ifEmpty { 
            channel.select("itunes|image").attr("href")
        }
        val language = channel.select("language").text()
        val author = channel.select("itunes|author").text().ifEmpty { 
            channel.select("managingEditor").text() 
        }
        val category = channel.select("itunes|category").attr("text")
        val explicit = channel.select("itunes|explicit").text().equals("yes", true)
        
        // Parse episodes
        val items = channel.select("item").map { item ->
            RSSItem(
                title = item.select("title").text(),
                description = item.select("description").text(),
                link = item.select("link").text(),
                audioUrl = item.select("enclosure").attr("url"),
                duration = item.select("itunes|duration").text(),
                fileSize = item.select("enclosure").attr("length").toLongOrNull(),
                pubDate = item.select("pubDate").text(),
                guid = item.select("guid").text().ifEmpty { item.select("link").text() },
                episodeNumber = item.select("itunes|episode").text().toIntOrNull(),
                seasonNumber = item.select("itunes|season").text().toIntOrNull(),
                imageUrl = item.select("itunes|image").attr("href")
            )
        }.filter { it.audioUrl.isNotEmpty() } // Only include items with audio
        
        return RSSFeed(
            title = title,
            description = description,
            link = link,
            imageUrl = imageUrl,
            language = language,
            author = author,
            category = category,
            explicit = explicit,
            items = items
        )
    }

    /**
     * Convert RSS items to podcast episodes
     */
    private fun convertRSSItemsToEpisodes(items: List<RSSItem>, podcastId: String): List<PodcastEpisode> {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
        
        return items.mapIndexed { index, item ->
            val publishDate = try {
                dateFormat.parse(item.pubDate ?: "")
            } catch (e: Exception) {
                Date() // Fallback to current date
            }
            
            val duration = parseDuration(item.duration)
            
            PodcastEpisode(
                id = item.guid ?: "${podcastId}_$index",
                title = item.title,
                description = item.description,
                audioUrl = item.audioUrl ?: "",
                duration = duration,
                fileSize = item.fileSize ?: 0,
                publishDate = publishDate ?: Date(),
                episodeNumber = item.episodeNumber,
                seasonNumber = item.seasonNumber,
                imageUrl = item.imageUrl
            )
        }
    }

    /**
     * Search podcasts by RSS discovery (fallback method)
     */
    private suspend fun searchPodcastsByRSSDiscovery(query: String): List<PodcastSearchResult> {
        // This would implement RSS feed discovery from popular podcast directories
        // For now, return empty list
        return emptyList()
    }

    /**
     * Parse duration string (HH:MM:SS or seconds)
     */
    private fun parseDuration(durationStr: String?): Long {
        if (durationStr.isNullOrEmpty()) return 0L
        
        return try {
            if (durationStr.contains(":")) {
                val parts = durationStr.split(":")
                when (parts.size) {
                    3 -> { // HH:MM:SS
                        val hours = parts[0].toLong()
                        val minutes = parts[1].toLong()
                        val seconds = parts[2].toLong()
                        hours * 3600 + minutes * 60 + seconds
                    }
                    2 -> { // MM:SS
                        val minutes = parts[0].toLong()
                        val seconds = parts[1].toLong()
                        minutes * 60 + seconds
                    }
                    else -> 0L
                }
            } else {
                // Assume it's seconds
                durationStr.toLongOrNull() ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Generate podcast ID from feed URL
     */
    private fun generatePodcastId(feedUrl: String): String {
        return "podcast_${feedUrl.hashCode().toString().replace("-", "")}"
    }

    /**
     * Sanitize filename for file system
     */
    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9.-]"), "_").take(100)
    }

    /**
     * Get file extension from URL
     */
    private fun getFileExtension(url: String): String {
        return try {
            val path = URL(url).path
            val lastDot = path.lastIndexOf('.')
            if (lastDot > 0 && lastDot < path.length - 1) {
                path.substring(lastDot + 1).lowercase()
            } else {
                "mp3" // Default to mp3
            }
        } catch (e: Exception) {
            "mp3"
        }
    }

    /**
     * Import OPML file (podcast subscription list)
     */
    suspend fun importOPML(opmlContent: String): List<Podcast> {
        return withContext(Dispatchers.IO) {
            try {
                val doc = Jsoup.parse(opmlContent, "", Parser.xmlParser())
                val outlines = doc.select("outline[xmlUrl]")
                
                outlines.mapNotNull { outline ->
                    val feedUrl = outline.attr("xmlUrl")
                    if (feedUrl.isNotEmpty()) {
                        subscribeToPodcast(feedUrl)
                    } else null
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Export subscribed podcasts to OPML
     */
    fun exportToOPML(podcasts: List<Podcast>): String {
        val opml = StringBuilder()
        opml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        opml.append("<opml version=\"2.0\">\n")
        opml.append("  <head>\n")
        opml.append("    <title>CleverFerret Podcast Subscriptions</title>\n")
        opml.append("    <dateCreated>${Date()}</dateCreated>\n")
        opml.append("  </head>\n")
        opml.append("  <body>\n")
        
        podcasts.forEach { podcast ->
            opml.append("    <outline text=\"${escapeXml(podcast.title)}\" ")
            opml.append("title=\"${escapeXml(podcast.title)}\" ")
            opml.append("type=\"rss\" ")
            opml.append("xmlUrl=\"${escapeXml(podcast.feedUrl)}\" ")
            if (podcast.websiteUrl != null) {
                opml.append("htmlUrl=\"${escapeXml(podcast.websiteUrl)}\" ")
            }
            opml.append("/>\n")
        }
        
        opml.append("  </body>\n")
        opml.append("</opml>")
        
        return opml.toString()
    }

    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;")
    }
}