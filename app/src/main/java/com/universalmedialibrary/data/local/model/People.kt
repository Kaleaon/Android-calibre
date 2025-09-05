package com.universalmedialibrary.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class People(
    @PrimaryKey(autoGenerate = true)
    val personId: Long = 0,
    val name: String,
    val sortName: String?
)
