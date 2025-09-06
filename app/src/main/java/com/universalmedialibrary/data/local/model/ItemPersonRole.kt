package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Represents the many-to-many relationship between a [MediaItem] and a [People],
 * including the role that person played for that item.
 *
 * This data class is a Room entity that corresponds to the `item_person_role` join table.
 *
 * @property itemId The ID of the media item.
 * @property personId The ID of the person.
 * @property role The role the person had in relation to the media item (e.g., 'AUTHOR', 'ACTOR').
 */
@Entity(
    tableName = "item_person_role",
    primaryKeys = ["itemId", "personId", "role"],
    foreignKeys = [
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = People::class,
            parentColumns = ["personId"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["personId"])]
)
data class ItemPersonRole(
    val itemId: Long,
    val personId: Long,
    val role: String // e.g., 'AUTHOR', 'ACTOR', 'DIRECTOR'
)
