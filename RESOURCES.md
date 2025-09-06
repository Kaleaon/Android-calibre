# Project Resources and References

This document consolidates all external resources, APIs, tools, and project references for the Universal Media Library application.

## 1. Core Component Engines

### E-Reader Engine
- **epub4j**: A powerful, permissively licensed Java library for reading and writing epub files.
  - *License*: Apache 2.0
  - *GitHub*: `https://github.com/documentnode/epub4j`
  - *Note*: This replaces KOReader to avoid the AGPL license.

### Audio Player Engine
- **AntennaPod**: A popular open-source podcast manager for Android. Its architecture will serve as a blueprint for our audio player component.
  - *GitHub*: `https://github.com/AntennaPod/AntennaPod`

### Video Player Engine
- **AndroidX Media3 (ExoPlayer)**: The standard, modern, and recommended video player for Android.
  - *Developer Guide*: `https://developer.android.com/guide/topics/media/media3`

## 2. AI & Data Correction

### OCR Engine
- **Google ML Kit Text Recognition**: The modern, on-device OCR solution for Android.
  - *License*: Apache 2.0
  - *Website*: `https://developers.google.com/ml-kit/vision/text-recognition`

### NLP Engine
- **Apache OpenNLP**: A machine learning based toolkit for the processing of natural language text.
  - *License*: Apache 2.0
  - *Website*: `https://opennlp.apache.org/`
  - *Note*: Will be used for Named Entity Recognition (NER) to find titles and authors.

## 3. Metadata Sources (APIs)

### Books
- **Open Library API**: Provides bibliographic information.
  - *Website*: `https://openlibrary.org/developers/api`
- **Google Books API**: Provides full-text search and book information.
  - *Website*: `https://developers.google.com/books`
- **Hardcover API**: A modern Goodreads alternative with a GraphQL API.
  - *Website*: `https://hardcover.app/graphql`

### Comics
- **ComicVine API**: A comprehensive comic book database.
  - *License Note*: Requires a user-specific API key and is for non-commercial use only.
  - *Website*: `https://comicvine.gamespot.com/api/`

### Audiobooks
- **OverDrive API**: For audiobook metadata.
  - *Website*: `https://developer.overdrive.com/`

### Movies & TV
- **The Movie Database (TMDB) API**: A popular community-built movie and TV database.
  - *Website*: `https://www.themoviedb.org/documentation/api`
- **OMDb API**: The Open Movie Database.
  - *Website*: `https://www.omdbapi.com/`

### Music
- **MusicBrainz API**: An open music encyclopedia.
  - *Website*: `https://musicbrainz.org/doc/Development`
- **Spotify Web API**: A comprehensive source for music data.
  - *Website*: `https://developer.spotify.com/documentation/web-api`

## 4. UI/UX Inspiration & Benchmarks

### UI/UX Inspiration
- **book-story**: For its modern Material You aesthetic.
  - *GitHub*: `https://github.com/AdityaV025/book-story`
- **Plexoid**: For its multi-library organizational concepts.
  - *GitHub*: `https://github.com/aps-studio/Plexoid`

### Feature Benchmark
- **Moon+ Reader**: The feature set of this application will be used as a benchmark for a best-in-class reading experience.
  - *Google Play*: `https://play.google.com/store/apps/details?id=com.flyersoft.moonreader`

## 5. Development & Tooling References

### Embedded Metadata Tooling
- **Android `MediaMetadataRetriever`**: The native Android class for *reading* embedded metadata from media files.
- **Android `ExifInterface`**: The native Android class for *reading and writing* EXIF data from image files.
- **Note**: Libraries for *writing* audio and video metadata will be selected during implementation.

### High-Level Reference
- **TinyMediaManager**: A high-level reference for media scraping and organization logic.
  - *Website*: `https://www.tinymediamanager.org/`
