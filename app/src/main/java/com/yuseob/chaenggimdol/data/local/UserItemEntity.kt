package com.yuseob.chaenggimdol.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_items",
    indices = [
        Index(value = ["normalizedName"], unique = true),
    ],
)
data class UserItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val normalizedName: String = name.trim().lowercase(),
    val category: String,
    val important: Boolean,
    val active: Boolean,
    val checkHint: String?,
)
