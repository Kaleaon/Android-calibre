package com.universalmedialibrary.services

data class RawCalibreBook(
    val id: Long,
    val title: String,
    val path: String,
    val authorName: String?
)
