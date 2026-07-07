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
    version = 3,
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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE user_items ADD COLUMN normalizedName TEXT NOT NULL DEFAULT ''",
                )
                db.execSQL(
                    "UPDATE user_items SET normalizedName = lower(trim(name))",
                )
                db.execSQL(
                    """
                    UPDATE user_items
                    SET
                        name = trim(
                            (
                                SELECT chosen.name
                                FROM user_items AS chosen
                                WHERE chosen.normalizedName = user_items.normalizedName
                                ORDER BY chosen.active DESC, chosen.id DESC
                                LIMIT 1
                            )
                        ),
                        category = (
                            SELECT chosen.category
                            FROM user_items AS chosen
                            WHERE chosen.normalizedName = user_items.normalizedName
                            ORDER BY chosen.active DESC, chosen.id DESC
                            LIMIT 1
                        ),
                        important = (
                            SELECT chosen.important
                            FROM user_items AS chosen
                            WHERE chosen.normalizedName = user_items.normalizedName
                            ORDER BY chosen.active DESC, chosen.id DESC
                            LIMIT 1
                        ),
                        active = (
                            SELECT chosen.active
                            FROM user_items AS chosen
                            WHERE chosen.normalizedName = user_items.normalizedName
                            ORDER BY chosen.active DESC, chosen.id DESC
                            LIMIT 1
                        ),
                        checkHint = (
                            SELECT chosen.checkHint
                            FROM user_items AS chosen
                            WHERE chosen.normalizedName = user_items.normalizedName
                            ORDER BY chosen.active DESC, chosen.id DESC
                            LIMIT 1
                        )
                    WHERE id IN (
                        SELECT MIN(id)
                        FROM user_items
                        GROUP BY normalizedName
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    DELETE FROM user_items
                    WHERE id NOT IN (
                        SELECT MIN(id)
                        FROM user_items
                        GROUP BY normalizedName
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX index_user_items_normalizedName ON user_items(normalizedName)",
                )
            }
        }
    }
}
