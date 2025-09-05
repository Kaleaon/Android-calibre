# Project Plan: Universal Media Library for Android

## 1. High-Level Vision

The goal is to create a new, fully self-contained, native Android application for managing a user's complete media library directly on their device. This includes books, comics, music, movies, podcasts, and more. The application will feature a modern, extensible, and user-friendly interface for organizing, viewing, and consuming all types of media, with powerful tools for metadata management.

---

## 2. Core Principles

*   **On-Device First:** The entire library and its database will be managed on the user's device. No external server or desktop application will be required.
*   **Extensible by Design:** The database schema and application architecture will be designed from the ground up to support a wide variety of media types.
*   **User-Centric Metadata:** The user will have full control over their metadata, with powerful tools for manual editing and automatic fetching from multiple online sources.
*   **Modern Native UI:** The application will be built with the latest Android technologies (Kotlin, Jetpack Compose, Material You).
*   **Open Source:** The project will be developed as a Free and Open Source Software (FOSS) project.

---

## 3. Research Summary & Final Architecture

An extensive research phase was conducted to determine the best architecture and components for this project.

### Final Proposed Architecture (On-Device)

The project direction has been finalized. A **fully self-contained, on-device application is feasible**. This architecture does not require a separate computer or server.

1.  **Core Concept:** The application will be a native Android app that directly reads and manages a user's media library, which will be stored on the device's local storage.

2.  **Database Layer:** A new data layer will be built in **Kotlin** using standard Android SQLite libraries. It will be responsible for all database operations based on a custom, extensible schema designed for multiple media types.

3.  **Reader Component (KOReader):**
    *   The app will integrate the **KOReader** project as its reading engine for e-books and comics.
    *   **License:** This choice mandates that the final application must be licensed under the **AGPL-3.0**.
    *   **Feature Benchmark:** The feature set of **Moon+ Reader** will be used as a benchmark for quality and customization options to be added to the KOReader base.

4.  **Audio Component (AntennaPod Model):**
    *   A dedicated audio player for music and audiobooks will be built, using the architecture of the **AntennaPod** project as a blueprint.

5.  **Video Component (ExoPlayer or libvlc):**
    *   The app will use a robust video playback engine. The default choice is **AndroidX Media3 (ExoPlayer)**. The powerful **libvlc** engine is a potential alternative to be considered during implementation.

6.  **UI/UX (Jetpack Compose & Material You):**
    *   The UI will be a modern, native Android interface built with **Kotlin** and **Jetpack Compose**.
    *   The design will be inspired by **`book-story`** (for its Material You aesthetic) and **`Plexoid`** (for its multi-library organizational concepts).

### External Metadata Sources

The app will enrich its library by fetching data from:
*   **Books:** Open Library API
*   **Comics:** ComicVine API (requires user API key, non-commercial use only)
*   **Audiobooks:** OverDrive API
*   **Movies/TV:** The Movie Database (TMDB) API, TheSportsDB API (to be investigated)
*   **Music:** MusicBrainz API (to be investigated)

### Metadata Tooling & Strategy

*   **Reading Embedded Metadata:** Use the native Android **`MediaMetadataRetriever`**.
*   **Writing Embedded Metadata (Audio/Video):** Use the C++ library **`tageditor`** as a reference or integrated component.
*   **Writing Embedded Metadata (Comics):** Replicate the logic from the Python tool **`ComicTagger`** to write `ComicInfo.xml` files.
*   **Scraping & Organization Logic:** Use **`TinyMediaManager`** and **`Chocolate`** as high-level references.

---

## 4. Development Plan

### Phase 1: Core Architecture and Database (MVP)
1.  **Database Schema Design:** Design the detailed SQLite database schema.
2.  **Native Database Library:** Implement the core data access layer in Kotlin.
3.  **Proof-of-Concept UI:** Build a simple UI to display items from the database.

### Phase 2: Reader and Player Integration
1.  **Integrate E-book/Comic Reader:** Integrate the KOReader engine.
2.  **Build Audio Player:** Build the audio player based on the AntennaPod model.
3.  **Build Video Player:** Integrate the ExoPlayer/libvlc engine.

### Phase 3: Metadata and UI Polish
1.  **Metadata Fetching Service:** Implement the service to fetch metadata from all external APIs.
2.  **Metadata Editing UI:** Build the user interface for manual metadata editing.
3.  **Full-Featured UI:** Build out the complete, polished user interface.

---

## 5. Feature Roadmap (Inspired by Moon+ Reader)

The following features, inspired by the best-in-class Moon+ Reader Pro, should be considered for implementation to ensure a competitive and full-featured application.

### Reading & Customization
- **Deep Visual Controls:** Line space, font scale, bold, italic, shadow, alpha colors, fading edge.
- **Theming:** Multiple embedded themes, including a Day/Night mode switcher.
- **Advanced Paging:** Support for touch screen, volume keys, and other hardware keys. Highly customizable gestures and key mappings.
- **Auto-Scroll:** Multiple modes (rolling blind, by pixel, by line, by page) with real-time speed control.
- **Ergonomics:** Brightness control via screen edge gestures; "Keep your eyes health" options for long reading sessions.
- **Page Effects:** Realistic page-turning animations.
- **Layout:** Justified text alignment, hyphenation mode, and dual-page mode for landscape.
- **EPUB3 Support:** Handle multimedia content (video and audio) embedded in ePub files.

### Library & Data Management
- **Bookshelf Design:** Advanced library organization with Favorites, Downloads, Authors, and Tags. Support for custom book covers.
- **Cloud Sync:** Backup and restore settings, reading positions, highlights, and notes to cloud services (e.g., Dropbox, WebDav).
- **Widgets:** Home screen widgets for displaying a "shelf" of favorite books.

### In-Reader Tools
- **Annotations:** Support for highlighting and annotating text.
- **Dictionary:** Offline and online dictionary integration.
- **Translation:** Integration with translation services.
- **Sharing:** Ability to share snippets, highlights, and notes.
- **Reading Ruler:** A tool to help focus reading on a specific line.

### Pro-Tier Features
- **Text-to-Speech (TTS):** "Shake to speak" or other easy-access TTS controls.
- **Advanced PDF:** High-performance PDF rendering with annotation support.
- **Security:** Option for password protection at startup.
- **Shortcuts:** "Book to home screen" shortcut creation.
