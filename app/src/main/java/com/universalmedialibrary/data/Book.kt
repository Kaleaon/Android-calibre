package com.universalmedialibrary.data

data class Book(
    val mediaItemId: Long,
    val subtitle: String?,
    val isbn: String?,
    val pageCount: Int?,
    val publisher: String?,
)
