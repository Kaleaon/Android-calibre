package com.universalmedialibrary.services.metadata

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

// Data classes for API responses
data class MetadataSearchResult(
    val id: String,
    val title: String,
    val author: String? = null,
    val director: String? = null,
    val artist: String? = null,
    val year: Int? = null,
    val coverUrl: String? = null,
    val description: String? = null,
    val genres: List<String> = emptyList(),
    val rating: Float? = null,
    val isbn: String? = null,
    val imdbId: String? = null,
    val source: MetadataSource
)

enum class MetadataSource {
    // Book sources (matching Calibre plugins)
    GOOGLE_BOOKS,
    OPEN_LIBRARY,
    AMAZON_PRODUCT_ADVERTISING,
    GOODREADS,
    HARDCOVER,
    ISFDB, // Internet Speculative Fiction Database
    FANTASTIC_FICTION,
    FICTION_DB,
    LIBRARY_THING,
    WORLDCAT_OCLC,
    
    // Comic/Manga sources
    COMICVINE,
    MANGA_UPDATES,
    
    // Movie/TV sources
    TMDB,
    OMDB,
    IMDB,
    TVDB,
    
    // Music sources
    MUSICBRAINZ,
    SPOTIFY,
    DISCOGS,
    LAST_FM,
    
    // Audiobook sources
    AUDIBLE,
    LIBRIVOX,
    
    // General ISBN databases
    ISBN_DB,
    WORLD_CAT,
    
    // Fanfiction sources (covered in WebFictionService)
    ARCHIVE_OF_OUR_OWN,
    FANFICTION_NET,
    WATTPAD
}

// Google Books API models
data class GoogleBooksResponse(
    val items: List<GoogleBookItem>? = null
)

data class GoogleBookItem(
    val id: String,
    val volumeInfo: GoogleBookVolumeInfo
)

data class GoogleBookVolumeInfo(
    val title: String? = null,
    val authors: List<String>? = null,
    val publishedDate: String? = null,
    val description: String? = null,
    val categories: List<String>? = null,
    val averageRating: Float? = null,
    val imageLinks: GoogleBookImageLinks? = null,
    val industryIdentifiers: List<GoogleBookIdentifier>? = null
)

data class GoogleBookImageLinks(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)

data class GoogleBookIdentifier(
    val type: String,
    val identifier: String
)

// Open Library API models
data class OpenLibraryResponse(
    val docs: List<OpenLibraryDoc>? = null
)

data class OpenLibraryDoc(
    val key: String,
    val title: String? = null,
    val author_name: List<String>? = null,
    val first_publish_year: Int? = null,
    val cover_i: Int? = null,
    val isbn: List<String>? = null
)

// TMDB API models
data class TMDBResponse(
    val results: List<TMDBResult>? = null
)

data class TMDBResult(
    val id: Int,
    val title: String? = null,
    val name: String? = null, // For TV shows
    val overview: String? = null,
    val release_date: String? = null,
    val first_air_date: String? = null, // For TV shows
    val poster_path: String? = null,
    val genre_ids: List<Int>? = null,
    val vote_average: Float? = null
)

// MusicBrainz API models
data class MusicBrainzResponse(
    val releases: List<MusicBrainzRelease>? = null
)

data class MusicBrainzRelease(
    val id: String,
    val title: String? = null,
    val date: String? = null,
    val artist_credit: List<MusicBrainzArtistCredit>? = null
)

data class MusicBrainzArtistCredit(
    val name: String? = null
)

// API Interfaces
interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10
    ): GoogleBooksResponse

    @GET("volumes/{id}")
    suspend fun getBookDetails(@Path("id") id: String): GoogleBookItem
}

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10
    ): OpenLibraryResponse
}

interface TMDBApi {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TMDBResponse

    @GET("search/tv")
    suspend fun searchTV(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TMDBResponse
}

interface MusicBrainzApi {
    @GET("release-group")
    suspend fun searchMusic(
        @Query("query") query: String,
        @Query("fmt") format: String = "json",
        @Query("limit") limit: Int = 10
    ): MusicBrainzResponse
}

@Singleton
class MetadataApiService @Inject constructor() {
    
    private val googleBooksApi: GoogleBooksApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApi::class.java)
    }

    private val openLibraryApi: OpenLibraryApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryApi::class.java)
    }

    private val tmdbApi: TMDBApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBApi::class.java)
    }

    private val musicBrainzApi: MusicBrainzApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://musicbrainz.org/ws/2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicBrainzApi::class.java)
    }

    suspend fun searchBooks(query: String): List<MetadataSearchResult> {
        val results = mutableListOf<MetadataSearchResult>()
        
        try {
            // Google Books API
            val googleBooks = googleBooksApi.searchBooks(query)
            googleBooks.items?.forEach { item ->
                results.add(
                    MetadataSearchResult(
                        id = item.id,
                        title = item.volumeInfo.title ?: "Unknown Title",
                        author = item.volumeInfo.authors?.joinToString(", "),
                        year = item.volumeInfo.publishedDate?.take(4)?.toIntOrNull(),
                        coverUrl = item.volumeInfo.imageLinks?.thumbnail,
                        description = item.volumeInfo.description,
                        genres = item.volumeInfo.categories ?: emptyList(),
                        rating = item.volumeInfo.averageRating,
                        isbn = item.volumeInfo.industryIdentifiers
                            ?.find { it.type == "ISBN_13" }?.identifier,
                        source = MetadataSource.GOOGLE_BOOKS
                    )
                )
            }
        } catch (e: Exception) {
            // Log error but continue with other sources
        }

        try {
            // Open Library API
            val openLibrary = openLibraryApi.searchBooks(query)
            openLibrary.docs?.forEach { doc ->
                results.add(
                    MetadataSearchResult(
                        id = doc.key,
                        title = doc.title ?: "Unknown Title",
                        author = doc.author_name?.joinToString(", "),
                        year = doc.first_publish_year,
                        coverUrl = doc.cover_i?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" },
                        isbn = doc.isbn?.firstOrNull(),
                        source = MetadataSource.OPEN_LIBRARY
                    )
                )
            }
        } catch (e: Exception) {
            // Log error but continue
        }

        return results
    }

    suspend fun searchMovies(query: String, tmdbApiKey: String?): List<MetadataSearchResult> {
        val results = mutableListOf<MetadataSearchResult>()
        
        if (tmdbApiKey.isNullOrEmpty()) {
            // Return demo data if no API key
            return createDemoMovieResults(query)
        }

        try {
            val movies = tmdbApi.searchMovies(tmdbApiKey, query)
            movies.results?.forEach { movie ->
                results.add(
                    MetadataSearchResult(
                        id = movie.id.toString(),
                        title = movie.title ?: movie.name ?: "Unknown Title",
                        year = movie.release_date?.take(4)?.toIntOrNull() 
                            ?: movie.first_air_date?.take(4)?.toIntOrNull(),
                        coverUrl = movie.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" },
                        description = movie.overview,
                        rating = movie.vote_average,
                        source = MetadataSource.TMDB
                    )
                )
            }
        } catch (e: Exception) {
            return createDemoMovieResults(query)
        }

        return results
    }

    suspend fun searchMusic(query: String): List<MetadataSearchResult> {
        val results = mutableListOf<MetadataSearchResult>()
        
        try {
            val music = musicBrainzApi.searchMusic(query)
            music.releases?.forEach { release ->
                results.add(
                    MetadataSearchResult(
                        id = release.id,
                        title = release.title ?: "Unknown Title",
                        artist = release.artist_credit?.firstOrNull()?.name,
                        year = release.date?.take(4)?.toIntOrNull(),
                        source = MetadataSource.MUSICBRAINZ
                    )
                )
            }
        } catch (e: Exception) {
            // Return demo data on error
            return createDemoMusicResults(query)
        }

        return results
    }

    private fun createDemoMovieResults(query: String): List<MetadataSearchResult> {
        return listOf(
            MetadataSearchResult(
                id = "demo1",
                title = "Demo Movie: $query",
                director = "Demo Director",
                year = 2024,
                coverUrl = "https://via.placeholder.com/500x750/1565C0/ffffff?text=Movie",
                description = "This is a demo movie result for '$query'. In production, this would use the actual TMDB API.",
                genres = listOf("Action", "Drama"),
                rating = 8.5f,
                source = MetadataSource.TMDB
            )
        )
    }

    private fun createDemoMusicResults(query: String): List<MetadataSearchResult> {
        return listOf(
            MetadataSearchResult(
                id = "demo1",
                title = "Demo Album: $query",
                artist = "Demo Artist",
                year = 2024,
                coverUrl = "https://via.placeholder.com/500x500/7B1FA2/ffffff?text=Music",
                description = "This is a demo music result for '$query'.",
                genres = listOf("Electronic", "Ambient"),
                source = MetadataSource.MUSICBRAINZ
            )
        )
    }
}