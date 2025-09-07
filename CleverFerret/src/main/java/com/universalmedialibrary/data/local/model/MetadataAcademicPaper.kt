package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "metadata_academic_paper",
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MetadataAcademicPaper(
    @PrimaryKey
    val itemId: Long,
    val journalName: String? = null,
    val doi: String? = null,
    val abstractText: String? = null, // Changed from 'abstract' to avoid keyword conflict
    val publicationYear: Int? = null,
    val citationCount: Int? = null,
    val keywords: String? = null,
    val researchField: String? = null,
    val peerReviewed: Boolean = false,
    val arxivId: String? = null
)