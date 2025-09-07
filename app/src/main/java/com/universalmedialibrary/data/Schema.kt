package com.universalmedialibrary.data

object DatabaseSchema {

    const val SQL_CREATE_MEDIA_ITEMS = """
        CREATE TABLE media_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            file_path TEXT NOT NULL UNIQUE,
            media_type TEXT NOT NULL CHECK(media_type IN (
                'BOOK', 'EBOOK', 'AUDIOBOOK', 'MOVIE', 'TV_SHOW', 'DOCUMENTARY', 
                'MUSIC_TRACK', 'MUSIC_ALBUM', 'PODCAST_EPISODE', 'PODCAST_SERIES', 
                'COMIC', 'MANGA', 'MAGAZINE', 'NEWSPAPER', 'JOURNAL', 'NEWS_ARTICLE', 
                'ACADEMIC_PAPER', 'REPORT', 'PRESENTATION', 'SPREADSHEET', 'IMAGE', 
                'PHOTO_ALBUM', 'VIDEO_CLIP', 'ANIMATION', 'GAME', 'SOFTWARE', 
                'ARCHIVE', 'DOCUMENT', 'NOTE', 'RECIPE', 'MANUAL', 'TUTORIAL'
            )),
            date_added INTEGER NOT NULL,
            date_modified INTEGER NOT NULL
        );
    """

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

    const val SQL_CREATE_AUTHORS = """
        CREATE TABLE authors (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            sort_name TEXT
        );
    """

    const val SQL_CREATE_TAGS = """
        CREATE TABLE tags (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE
        );
    """

    const val SQL_CREATE_MEDIA_ITEM_AUTHOR_JOIN = """
        CREATE TABLE media_item_author_join (
            media_item_id INTEGER NOT NULL,
            author_id INTEGER NOT NULL,
            PRIMARY KEY (media_item_id, author_id),
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE,
            FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
        );
    """

    const val SQL_CREATE_MEDIA_ITEM_TAG_JOIN = """
        CREATE TABLE media_item_tag_join (
            media_item_id INTEGER NOT NULL,
            tag_id INTEGER NOT NULL,
            PRIMARY KEY (media_item_id, tag_id),
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE,
            FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
        );
    """

    // Additional metadata tables for different media types
    const val SQL_CREATE_MOVIES = """
        CREATE TABLE movies (
            media_item_id INTEGER PRIMARY KEY,
            director TEXT,
            runtime_minutes INTEGER,
            rating TEXT,
            release_year INTEGER,
            production_company TEXT,
            budget INTEGER,
            box_office INTEGER,
            imdb_id TEXT,
            tmdb_id TEXT,
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE
        );
    """

    const val SQL_CREATE_TV_SHOWS = """
        CREATE TABLE tv_shows (
            media_item_id INTEGER PRIMARY KEY,
            season_number INTEGER,
            episode_number INTEGER,
            series_name TEXT,
            network TEXT,
            air_date TEXT,
            episode_runtime INTEGER,
            tv_rating TEXT,
            tvdb_id TEXT,
            imdb_id TEXT,
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE
        );
    """

    const val SQL_CREATE_MUSIC = """
        CREATE TABLE music (
            media_item_id INTEGER PRIMARY KEY,
            artist TEXT,
            album TEXT,
            track_number INTEGER,
            disc_number INTEGER,
            duration_seconds INTEGER,
            genre TEXT,
            year INTEGER,
            bitrate INTEGER,
            sample_rate INTEGER,
            musicbrainz_id TEXT,
            spotify_id TEXT,
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE
        );
    """

    const val SQL_CREATE_PODCASTS = """
        CREATE TABLE podcasts (
            media_item_id INTEGER PRIMARY KEY,
            show_name TEXT,
            episode_number INTEGER,
            season_number INTEGER,
            duration_seconds INTEGER,
            publication_date TEXT,
            description TEXT,
            transcript_path TEXT,
            rss_url TEXT,
            podcast_guid TEXT,
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE
        );
    """

    const val SQL_CREATE_MAGAZINES = """
        CREATE TABLE magazines (
            media_item_id INTEGER PRIMARY KEY,
            issue_number TEXT,
            volume_number TEXT,
            publication_date TEXT,
            issn TEXT,
            magazine_title TEXT,
            article_count INTEGER,
            cover_story TEXT,
            editor TEXT,
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE
        );
    """

    const val SQL_CREATE_DOCUMENTS = """
        CREATE TABLE documents (
            media_item_id INTEGER PRIMARY KEY,
            document_type TEXT,
            author TEXT,
            creation_date TEXT,
            modification_date TEXT,
            page_count INTEGER,
            word_count INTEGER,
            language TEXT,
            format TEXT,
            file_size INTEGER,
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE
        );
    """

    const val SQL_CREATE_ACADEMIC_PAPERS = """
        CREATE TABLE academic_papers (
            media_item_id INTEGER PRIMARY KEY,
            journal_name TEXT,
            doi TEXT,
            abstract TEXT,
            publication_year INTEGER,
            citation_count INTEGER,
            keywords TEXT,
            research_field TEXT,
            peer_reviewed INTEGER DEFAULT 0,
            arxiv_id TEXT,
            FOREIGN KEY (media_item_id) REFERENCES media_items(id) ON DELETE CASCADE
        );
    """
}
