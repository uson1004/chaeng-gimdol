package com.yuseob.chaenggimdol.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserItemEntity::class,
        SessionEntity::class,
        SessionItemEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class ChaenggimDatabase : RoomDatabase() {
    abstract fun userItemDao(): UserItemDao

    abstract fun sessionDao(): SessionDao
}
