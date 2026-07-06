package com.yuseob.chaenggimdol.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_items")
data class UserItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val important: Boolean,
    val active: Boolean,
    val checkHint: String?,
)
