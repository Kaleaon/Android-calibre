# Database Schema Design

This document outlines the proposed database schema for the Universal Media Library application. The design is intended to be extensible and support a wide variety of media types and advanced metadata features.

## Core Tables

### `Libraries`
Stores information about each media library the user has added.

| Column | Type | Description |
|---|---|---|
| `library_id` | INTEGER | Primary Key |
| `name` | TEXT | User-defined name for the library (e.g., "My Books"). `NOT NULL`. |
| `type` | TEXT | The type of library ('BOOK', 'MOVIE', 'TV_SHOW', 'MUSIC', 'COMIC'). `NOT NULL`. |
| `path` | TEXT | The absolute file system path to the library's root folder. `NOT NULL`, `UNIQUE`. |

### `MediaItems`
The central table for all individual media files.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key |
| `library_id` | INTEGER | Foreign Key to `Libraries.library_id`. `NOT NULL`. |
| `file_path` | TEXT | The full, unique path to the media file. `NOT NULL`, `UNIQUE`. |
| `date_added` | INTEGER | Timestamp of when the item was first added. `NOT NULL`. |
| `last_scanned` | INTEGER | Timestamp of the last time the file was scanned for changes. `NOT NULL`. |
| `file_hash` | TEXT | A hash of the file content to detect duplicates or changes. |

## Metadata Tables

This set of tables uses a "Common" table for shared fields and type-specific tables for unique fields.

### `Metadata_Common`
Stores metadata fields that are common across all media types.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key, Foreign Key to `MediaItems.item_id`. `NOT NULL`. |
| `title` | TEXT | The primary title of the media item. `NOT NULL`. |
| `sort_title` | TEXT | A version of the title used for sorting (e.g., "Avengers, The"). |
| `year` | INTEGER | The primary release year. |
| `release_date` | INTEGER | Timestamp of the full release date. |
| `rating` | REAL | User-defined rating (e.g., 1.0 to 5.0). |
| `summary` | TEXT | A plot summary or description. |
| `cover_image_path` | TEXT | Path to a custom cover image file. |

### `Metadata_Book`
Stores metadata specific to books.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key, Foreign Key to `MediaItems.item_id`. `NOT NULL`. |
| `subtitle` | TEXT | The book's subtitle. |
| `publisher` | TEXT | The publisher's name. |
| `isbn` | TEXT | The ISBN of the book. |
| `page_count` | INTEGER | The number of pages in the book. |
| `series_id` | INTEGER | Foreign Key to a `Series` table. |

### `Metadata_Movie`
Stores metadata specific to movies.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key, Foreign Key to `MediaItems.item_id`. `NOT NULL`. |
| `tagline` | TEXT | The movie's tagline. |
| `runtime` | INTEGER | The runtime in minutes. |

### `Metadata_Music_Track`
Stores metadata specific to music tracks.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key, Foreign Key to `MediaItems.item_id`. `NOT NULL`. |
| `album_id` | INTEGER | Foreign Key to `Albums.album_id`. |
| `track_number` | INTEGER | The track number on the album. |
| `disc_number` | INTEGER | The disc number for multi-disc albums. |
| `duration` | INTEGER | The duration of the track in seconds. |

## Relational Tables (Many-to-Many)

These tables handle relationships between media items and entities like authors, genres, etc.

### `Genres`
| Column | Type | Description |
|---|---|---|
| `genre_id` | INTEGER | Primary Key |
| `name` | TEXT | The name of the genre (e.g., "Science Fiction", "Rock"). `NOT NULL`, `UNIQUE`. |

### `Item_Genre`
| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Foreign Key to `MediaItems.item_id`. `NOT NULL`. |
| `genre_id` | INTEGER | Foreign Key to `Genres.genre_id`. `NOT NULL`. |
| *Composite* | | `PRIMARY KEY (item_id, genre_id)` |

### `People`
Stores information about people (authors, actors, directors, etc.).

| Column | Type | Description |
|---|---|---|
| `person_id` | INTEGER | Primary Key |
| `name` | TEXT | The person's name. `NOT NULL`, `UNIQUE`. |
| `sort_name` | TEXT | The name used for sorting (e.g., "Asimov, Isaac"). |

### `Item_Person_Role`
Links people to media items with a specific role.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Foreign Key to `MediaItems.item_id`. `NOT NULL`. |
| `person_id` | INTEGER | Foreign Key to `People.person_id`. `NOT NULL`. |
| `role` | TEXT | The person's role (e.g., 'AUTHOR', 'ACTOR', 'DIRECTOR'). `NOT NULL`. |
| *Composite* | | `PRIMARY KEY (item_id, person_id, role)` |

### `Series`
| Column | Type | Description |
|---|---|---|
| `series_id` | INTEGER | Primary Key |
| `name` | TEXT | The name of the series. `NOT NULL`, `UNIQUE`. |

### `Albums`
| Column | Type | Description |
|---|---|---|
| `album_id` | INTEGER | Primary Key |
| `title` | TEXT | The title of the album. `NOT NULL`. |
| `album_artist_id` | INTEGER | Foreign Key to `People.person_id`. |
| `release_year` | INTEGER | The release year of the album. |
| `album_art_path` | TEXT | Path to the album art image. |

## Indexing Strategy

To ensure fast query performance, especially as the library grows, a clear indexing strategy is required. While primary keys are automatically indexed, secondary indexes should be created for foreign keys and frequently queried columns.

### Recommended Indexes

-   **`idx_mediaitems_library_id`**: On `MediaItems(library_id)` to speed up queries for all items in a specific library.
-   **`idx_metadata_common_title`**: On `Metadata_Common(title)` to speed up title searches.
-   **`idx_item_genre_genre_id`**: On `Item_Genre(genre_id)` to quickly find all items of a specific genre.
-   **`idx_item_person_role_person_id`**: On `Item_Person_Role(person_id)` to quickly find all items associated with a person.
-   **`idx_people_name`**: On `People(name)` to speed up searches by person's name.
-   **`idx_series_name`**: On `Series(name)` to speed up searches by series name.
