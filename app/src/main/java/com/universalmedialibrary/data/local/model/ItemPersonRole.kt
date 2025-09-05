package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey

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
    ]
)
data class ItemPersonRole(
    val itemId: Long,
    val personId: Long,
    val role: String // e.g., 'AUTHOR', 'ACTOR', 'DIRECTOR'
)
