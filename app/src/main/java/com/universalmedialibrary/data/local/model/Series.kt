package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a series that can contain multiple media items (e.g., a book series).
 *
 * This data class is a Room entity that corresponds to the `series` table.
 *
 * @property seriesId The unique identifier for the series.
 * @property name The name of the series.
 */
@Entity(tableName = "series")
data class Series(
    @PrimaryKey(autoGenerate = true)
    val seriesId: Long = 0,
    val name: String
)
