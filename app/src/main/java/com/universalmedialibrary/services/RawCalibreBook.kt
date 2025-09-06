package com.universalmedialibrary.services

data class RawCalibreBook(
    val id: Long,
    val title: String,
    val path: String,
    val authorNames: List<String>,
    val seriesName: String?,
    val seriesIndex: Double?,
    val publisher: String?,
    val isbn: String?,
    val tags: List<String>,
    val comments: String?,
)
