package com.universalmedialibrary.data.local.model

import androidx.room.Embedded

data class BookDetails(
    @Embedded(prefix = "media_") val mediaItem: MediaItem,
    @Embedded(prefix = "meta_") val metadata: MetadataCommon,
    val authorName: String?
)
