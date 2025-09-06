package com.universalmedialibrary.data

/**
 * Defines the database schema for the application.
 *
 * This object contains the SQL `CREATE TABLE` statements for all tables
 * used in the application's database. These definitions are used by the
 * [DatabaseHelper] to create the database on first launch.
 */
object DatabaseSchema {

    /** SQL statement to create the `media_items` table. */
    const val SQL_CREATE_MEDIA_ITEMS = """
        CREATE TABLE media_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            file_path TEXT NOT NULL UNIQUE,
            media_type TEXT NOT NULL CHECK(media_type IN ('BOOK', 'MOVIE', 'MUSIC_TRACK', 'PODCAST_EPISODE', 'COMIC')),
            date_added INTEGER NOT NULL,
            date_modified INTEGER NOT NULL
        );
    """

    /** SQL statement to create the `books` table. */
    const val SQL_CREATE_BOOKS = """
        CREATE TABLE books (
            media_item_id INTEGER PRIMARY KEY,
            subtitle TEXT,
            isbn TEXT,
            page_count INTEGER,
            publisher TEXT,
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE
        );
    """

    /** SQL statement to create the `authors` table. */
    const val SQL_CREATE_AUTHORS = """
        CREATE TABLE authors (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            sort_name TEXT
        );
    """

    /** SQL statement to create the `tags` table. */
    const val SQL_CREATE_TAGS = """
        CREATE TABLE tags (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE
        );
    """

    /** SQL statement to create the `media_item_author_join` table. */
    const val SQL_CREATE_MEDIA_ITEM_AUTHOR_JOIN = """
        CREATE TABLE media_item_author_join (
            media_item_id INTEGER NOT NULL,
            author_id INTEGER NOT NULL,
            PRIMARY KEY (media_item_id, author_id),
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE,
            FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
        );
    """

    /** SQL statement to create the `media_item_tag_join` table. */
    const val SQL_CREATE_MEDIA_ITEM_TAG_JOIN = """
        CREATE TABLE media_item_tag_join (
            media_item_id INTEGER NOT NULL,
            tag_id INTEGER NOT NULL,
            PRIMARY KEY (media_item_id, tag_id),
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE,
            FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
        );
    """
}
