package com.universalmedialibrary.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "universalmedialibrary.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DatabaseSchema.SQL_CREATE_MEDIA_ITEMS)
        db?.execSQL(DatabaseSchema.SQL_CREATE_BOOKS)
        db?.execSQL(DatabaseSchema.SQL_CREATE_AUTHORS)
        db?.execSQL(DatabaseSchema.SQL_CREATE_TAGS)
        db?.execSQL(DatabaseSchema.SQL_CREATE_MEDIA_ITEM_AUTHOR_JOIN)
        db?.execSQL(DatabaseSchema.SQL_CREATE_MEDIA_ITEM_TAG_JOIN)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // For now, we'll just drop and recreate the database on upgrade.
        // A real migration strategy would be needed for a production app.
        db?.execSQL("DROP TABLE IF EXISTS media_item_tag_join")
        db?.execSQL("DROP TABLE IF EXISTS media_item_author_join")
        db?.execSQL("DROP TABLE IF EXISTS tags")
        db?.execSQL("DROP TABLE IF EXISTS authors")
        db?.execSQL("DROP TABLE IF EXISTS books")
        db?.execSQL("DROP TABLE IF EXISTS media_items")
        onCreate(db)
    }
}
