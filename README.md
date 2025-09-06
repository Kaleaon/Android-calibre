# Universal Media Library

Universal Media Library is a native Android application for managing and consuming a complete media library directly on your device. This includes books, comics, music, movies, podcasts, and more. The application features a modern, extensible, and user-friendly interface with powerful tools for metadata management.

## Core Principles

*   **On-Device First:** The entire library and its database are managed on the user's device. No external server or desktop application is required.
*   **Extensible by Design:** The database schema and application architecture are designed from the ground up to support a wide variety of media types.
*   **User-Centric Metadata:** The user has full control over their metadata, with powerful tools for manual editing and automatic fetching from multiple online sources.
*   **Modern Native UI:** The application is built with the latest Android technologies (Kotlin, Jetpack Compose, Material You).
*   **Open Source:** The project is developed as a Free and Open Source Software (FOSS) project.

---

## Current Status

The project is currently in the early stages of development. The following features are implemented:

*   **Core Database Layer:** A robust database layer built with **Room** to manage libraries, media items, and metadata.
*   **Calibre Import:** A service to import book libraries from an existing Calibre `metadata.db` file.
*   **Basic UI:** A simple user interface built with **Jetpack Compose** that allows users to view their libraries and the books within them.
*   **Dependency Injection:** Using **Hilt** for dependency management.

---

## Getting Started

### Prerequisites

*   Android Studio (latest version recommended)
*   Java Development Kit (JDK) 17 or higher

### Building the Project

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/universal-media-library.git
    ```
2.  **Open the project in Android Studio.**
3.  **Build the project:**
    *   Use the "Build" menu in Android Studio (Build > Make Project).
    *   Or run the following command in the terminal:
        ```bash
        ./gradlew build
        ```
4.  **Run the application on an emulator or a physical device.**

---

## Project Structure

The project is organized into the following main packages:

*   `com.universalmedialibrary.data`: Contains all data-related classes, including Room entities, DAOs (Data Access Objects), and the database definition.
    *   `data.local.model`: Room entity classes that define the database tables.
    *   `data.local.dao`: Room DAO interfaces that define the database queries.
*   `com.universalmedialibrary.di`: Contains the Hilt dependency injection modules.
*   `com.universalmedialibrary.services`: Contains services for background tasks, such as the Calibre import service.
*   `com.universalmedialibrary.ui`: Contains the Jetpack Compose UI code and ViewModels.
    *   `ui.main`: UI and ViewModel for the main screen (library list).
    *   `ui.details`: UI and ViewModel for the library details screen.

---

## Architectural Overview

*   **UI:** The UI is built with **Kotlin** and **Jetpack Compose**, following the Material You design guidelines.
*   **Database:** The database is implemented using **Room**, providing a reliable and efficient local data store.
*   **Dependency Injection:** **Hilt** is used to manage dependencies throughout the application.
*   **Asynchronous Operations:** **Kotlin Coroutines** and **Flow** are used for managing background threads and asynchronous data streams.

### Planned Components

*   **E-Reader:** `epub4j` for parsing and displaying e-book files.
*   **Audio Player:** Architecture inspired by the **AntennaPod** project.
*   **Video Player:** **AndroidX Media3 (ExoPlayer)**.
*   **Metadata Correction:** **Google's ML Kit** for OCR and **Apache OpenNLP** for text analysis.
*   **External Metadata Sources:** APIs from Open Library, Google Books, TMDB, MusicBrainz, and more.

---

## Development Plan & Roadmap

The project is being developed in phases. See `RESOURCES.md` for a full list of technologies and APIs.

### Phase 1: Foundational Research & Component Selection (COMPLETE)
*   E-Reader Component Investigation (Completed: `epub4j`)
*   Data Correction Technology Research (Completed: ML Kit & OpenNLP)
*   Expanded Metadata Source Research (Completed: New APIs identified)

### Phase 2: Architecture & Core Data Model (COMPLETE)
*   System Architecture Design
*   Advanced Database Schema and Import Logic

### Phase 3: Implementation (IN PROGRESS)
1.  **Implement Core Data Layer:** Build the database and the Calibre importer.
2.  **Build the User Interface:** Build the main library screens and the manual metadata editor.
3.  **Implement "Content-to-Epub" Features:** Build the news and fanfiction downloaders.
4.  **Integrate Media Viewers & Players:** Integrate `epub4j` and build the audio/video players.

### Phase 4: Testing and Polish
*   Perform end-to-end testing.
*   Refine the UI and user experience.

### Future Feature Goals (Inspired by Moon+ Reader)
- Deep visual controls for reading customization.
- Advanced paging and auto-scroll options.
- Cloud sync for settings and reading progress.
- In-reader tools like annotations, dictionary, and translation.
- Text-to-Speech (TTS) and advanced PDF support.
