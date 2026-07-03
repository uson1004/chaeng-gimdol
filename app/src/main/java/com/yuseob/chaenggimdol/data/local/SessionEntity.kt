package com.yuseob.chaenggimdol.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startedAtMillis: Long,
    val completedAtMillis: Long? = null,
    val reminderSent: Boolean = false,
    val uncheckedCountAtCompletion: Int? = null,
)
