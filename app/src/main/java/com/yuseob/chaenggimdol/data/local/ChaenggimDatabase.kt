package com.yuseob.chaenggimdol.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserItemEntity::class,
        SessionEntity::class,
        SessionItemEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class ChaenggimDatabase : RoomDatabase() {
    abstract fun userItemDao(): UserItemDao

    abstract fun sessionDao(): SessionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_items ADD COLUMN checkHint TEXT")
                db.execSQL(
                    "ALTER TABLE session_items ADD COLUMN important INTEGER NOT NULL DEFAULT 1",
                )
                db.execSQL("ALTER TABLE session_items ADD COLUMN checkHint TEXT")
                db.execSQL(
                    """
                    UPDATE session_items
                    SET important = COALESCE(
                        (
                            SELECT user_items.important
                            FROM user_items
                            WHERE user_items.id = session_items.itemId
                        ),
                        1
                    )
                    """.trimIndent(),
                )
            }
        }
    }
}
