# Database Schema Design

This document outlines the proposed database schema for the Universal Media Library application. The design is intended to be extensible and support a wide variety of media types and advanced metadata features.

## Core Tables

### `Libraries`
Stores information about each media library the user has added.

| Column | Type | Description |
|---|---|---|
| `library_id` | INTEGER | Primary Key |
| `name` | TEXT | User-defined name for the library (e.g., "My Books") |
| `type` | TEXT | The type of library ('BOOK', 'MOVIE', 'TV_SHOW', 'MUSIC', 'COMIC') |
| `path` | TEXT | The absolute file system path to the library's root folder |

### `MediaItems`
The central table for all individual media files.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key |
| `library_id` | INTEGER | Foreign Key to `Libraries.library_id` |
| `file_path` | TEXT | The full, unique path to the media file |
| `date_added` | INTEGER | Timestamp of when the item was first added |
| `last_scanned` | INTEGER | Timestamp of the last time the file was scanned for changes |
| `file_hash` | TEXT | A hash of the file content to detect duplicates or changes |

## Metadata Tables

This set of tables uses a "Common" table for shared fields and type-specific tables for unique fields.

### `Metadata_Common`
Stores metadata fields that are common across all media types.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key, Foreign Key to `MediaItems.item_id` |
| `title` | TEXT | The primary title of the media item |
| `sort_title` | TEXT | A version of the title used for sorting (e.g., "Avengers, The") |
| `year` | INTEGER | The primary release year |
| `release_date` | INTEGER | Timestamp of the full release date |
| `rating` | REAL | User-defined rating (e.g., 1.0 to 5.0) |
| `summary` | TEXT | A plot summary or description |
| `cover_image_path` | TEXT | Path to a custom cover image file |

### `Metadata_Book`
Stores metadata specific to books.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key, Foreign Key to `MediaItems.item_id` |
| `subtitle` | TEXT | The book's subtitle |
| `publisher` | TEXT | The publisher's name |
| `isbn` | TEXT | The ISBN of the book |
| `page_count` | INTEGER | The number of pages in the book |
| `series_id` | INTEGER | Foreign Key to a `Series` table |

### `Metadata_Movie`
Stores metadata specific to movies.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key, Foreign Key to `MediaItems.item_id` |
| `tagline` | TEXT | The movie's tagline |
| `runtime` | INTEGER | The runtime in minutes |

### `Metadata_Music_Track`
Stores metadata specific to music tracks.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Primary Key, Foreign Key to `MediaItems.item_id` |
| `album_id` | INTEGER | Foreign Key to `Albums.album_id` |
| `track_number` | INTEGER | The track number on the album |
| `disc_number` | INTEGER | The disc number for multi-disc albums |
| `duration` | INTEGER | The duration of the track in seconds |

## Relational Tables (Many-to-Many)

These tables handle relationships between media items and entities like authors, genres, etc.

### `Genres`
| Column | Type | Description |
|---|---|---|
| `genre_id` | INTEGER | Primary Key |
| `name` | TEXT | The name of the genre (e.g., "Science Fiction", "Rock") |

### `Item_Genre`
| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Foreign Key to `MediaItems.item_id` |
| `genre_id` | INTEGER | Foreign Key to `Genres.genre_id` |

### `People`
Stores information about people (authors, actors, directors, etc.).

| Column | Type | Description |
|---|---|---|
| `person_id` | INTEGER | Primary Key |
| `name` | TEXT | The person's name |
| `sort_name` | TEXT | The name used for sorting (e.g., "Asimov, Isaac") |

### `Item_Person_Role`
Links people to media items with a specific role.

| Column | Type | Description |
|---|---|---|
| `item_id` | INTEGER | Foreign Key to `MediaItems.item_id` |
| `person_id` | INTEGER | Foreign Key to `People.person_id` |
| `role` | TEXT | The person's role (e.g., 'AUTHOR', 'ACTOR', 'DIRECTOR') |

### `Series`
| Column | Type | Description |
|---|---|---|
| `series_id` | INTEGER | Primary Key |
| `name` | TEXT | The name of the series |

### `Albums`
| Column | Type | Description |
|---|---|---|
| `album_id` | INTEGER | Primary Key |
| `title` | TEXT | The title of the album |
| `album_artist_id` | INTEGER | Foreign Key to `People.person_id` |
| `release_year` | INTEGER | The release year of the album |
| `album_art_path` | TEXT | Path to the album art image |
