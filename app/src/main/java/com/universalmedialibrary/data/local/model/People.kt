package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a person associated with a media item (e.g., author, actor, director).
 *
 * This data class is a Room entity that corresponds to the `people` table.
 *
 * @property personId The unique identifier for the person.
 * @property name The full name of the person.
 * @property sortName The name formatted for sorting (e.g., "Asimov, Isaac").
 */
@Entity(tableName = "people")
data class People(
    @PrimaryKey(autoGenerate = true)
    val personId: Long = 0,
    val name: String,
    val sortName: String?
)
