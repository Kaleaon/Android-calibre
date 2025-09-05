# Project Resources and References

This document consolidates all external resources, APIs, tools, and project references for the Universal Media Library application.

## 1. Core Component Engines

### E-Reader Engine
- **KOReader**: A powerful, cross-platform, and actively maintained document and image viewer.
  - *License Note*: Using KOReader mandates an AGPL-3.0 license for the final application.
  - *Website*: `https://koreader.rocks/`
  - *GitHub*: `https://github.com/koreader/koreader`

### Audio Player Engine
- **AntennaPod**: A popular open-source podcast manager for Android. Its architecture will serve as a blueprint for our audio player component.
  - *GitHub*: `https://github.com/AntennaPod/AntennaPod`

### Video Player Engine
- **AndroidX Media3 (ExoPlayer)**: The standard, modern, and recommended video player for Android.
  - *Developer Guide*: `https://developer.android.com/guide/topics/media/media3`
- **libVLC**: A powerful alternative video player engine.
  - *Website*: `https://www.videolan.org/vlc/`

## 2. Metadata Sources (APIs)

### Books
- **Open Library API**: Provides bibliographic information.
  - *Website*: `https://openlibrary.org/developers/api`

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
- **TheSportsDB API**: For sports-related video metadata.
  - *Website*: `https://www.thesportsdb.com/api.php`

### Music
- **MusicBrainz API**: An open music encyclopedia.
  - *Website*: `https://musicbrainz.org/doc/Development`

## 3. UI/UX Inspiration & Benchmarks

### UI/UX Inspiration
- **book-story**: For its modern Material You aesthetic.
  - *GitHub*: `https://github.com/AdityaV025/book-story`
- **Plexoid**: For its multi-library organizational concepts.
  - *GitHub*: `https://github.com/aps-studio/Plexoid`

### Feature Benchmark
- **Moon+ Reader**: The feature set of this application will be used as a benchmark for a best-in-class reading experience.
  - *Google Play*: `https://play.google.com/store/apps/details?id=com.flyersoft.moonreader`

## 4. Development & Tooling References

### CI/CD (Continuous Integration)
- **Automated build android app with github action**: A GitHub Marketplace action for automating the Android build process.
  - *Marketplace*: `https://github.com/marketplace/actions/automated-build-android-app-with-github-action`

### Embedded Metadata Tooling
- **MediaMetadataRetriever**: The native Android class for reading embedded metadata from media files.
- **tageditor**: A C++ library used as a reference for *writing* embedded audio/video tags.
- **ComicTagger**: A Python tool used as a reference for *writing* `ComicInfo.xml` files for comics.
- **TinyMediaManager**: A high-level reference for media scraping and organization logic.
  - *Website*: `https://www.tinymediamanager.org/`
- **Chocolate**: A high-level reference for application organization logic.
  - *GitHub*: `https://github.com/ChocolateApp/Chocolate`
