package com.yuseob.chaenggimdol.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "session_items",
    primaryKeys = ["sessionId", "itemId"],
    indices = [Index("sessionId")],
)
data class SessionItemEntity(
    val sessionId: Long,
    val itemId: Long,
    val name: String,
    val status: String,
)
