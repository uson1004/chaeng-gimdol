package com.yuseob.chaenggimdol.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yuseob.chaenggimdol.data.local.ChaenggimDatabase
import com.yuseob.chaenggimdol.data.local.UserItemEntity
import com.yuseob.chaenggimdol.data.repository.RoomSessionRepository
import com.yuseob.chaenggimdol.domain.item.UserItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChaenggimDatabaseTest {
    private lateinit var database: ChaenggimDatabase

    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            ChaenggimDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun itemRoundTrips() = runTest {
        database.userItemDao().upsert(
            UserItemEntity(
                name = "충전기",
                category = "charger",
                important = true,
                active = true,
                checkHint = "책상 위",
            ),
        )

        assertEquals(
            "충전기",
            database.userItemDao().observeAll().first().single().name,
        )
        assertEquals(
            "책상 위",
            database.userItemDao().observeAll().first().single().checkHint,
        )
    }

    @Test
    fun sessionSnapshotsItemCheckMetadata() = runTest {
        val repository = RoomSessionRepository(database)

        val sessionId = repository.create(
            items = listOf(
                UserItem(
                    id = 1,
                    name = "충전기",
                    category = "charger",
                    important = false,
                    checkHint = "콘센트 옆",
                ),
            ),
            startedAtMillis = 1_000L,
        )

        val item = repository.get(sessionId)!!.items.single()
        assertEquals(false, item.important)
        assertEquals("콘센트 옆", item.checkHint)
    }

    @Test
    fun migrationFromVersionOneKeepsActiveSessionReadable() = runTest {
        database.close()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "migration-v1-v2.db"
        context.deleteDatabase(name)
        val file = context.getDatabasePath(name)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            db.execSQL(
                """
                CREATE TABLE user_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    category TEXT NOT NULL,
                    important INTEGER NOT NULL,
                    active INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE check_sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    startedAtMillis INTEGER NOT NULL,
                    completedAtMillis INTEGER,
                    reminderSent INTEGER NOT NULL,
                    uncheckedCountAtCompletion INTEGER
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE session_items (
                    sessionId INTEGER NOT NULL,
                    itemId INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    status TEXT NOT NULL,
                    PRIMARY KEY(sessionId, itemId)
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX index_session_items_sessionId ON session_items(sessionId)",
            )
            db.execSQL(
                "INSERT INTO user_items (id, name, category, important, active) VALUES (1, '우산', 'umbrella', 0, 1)",
            )
            db.execSQL(
                "INSERT INTO check_sessions (id, startedAtMillis, completedAtMillis, reminderSent, uncheckedCountAtCompletion) VALUES (1, 1000, NULL, 0, NULL)",
            )
            db.execSQL(
                "INSERT INTO session_items (sessionId, itemId, name, status) VALUES (1, 1, '우산', 'Unchecked')",
            )
            db.execSQL(
                "INSERT INTO session_items (sessionId, itemId, name, status) VALUES (1, 99, '삭제된 물건', 'Unchecked')",
            )
            db.version = 1
        }

        database = Room.databaseBuilder(context, ChaenggimDatabase::class.java, name)
            .addMigrations(
                ChaenggimDatabase.MIGRATION_1_2,
                ChaenggimDatabase.MIGRATION_2_3,
            )
            .allowMainThreadQueries()
            .build()

        val session = RoomSessionRepository(database).get(1)!!
        assertEquals(listOf(false, true), session.items.map { it.important })
        assertEquals(listOf(null, null), session.items.map { it.checkHint })
        assertNull(database.userItemDao().observeAll().first().single().checkHint)
    }

    @Test
    fun migrationFromVersionTwoDeduplicatesItemNames() = runTest {
        database.close()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "migration-v2-v3.db"
        context.deleteDatabase(name)
        val file = context.getDatabasePath(name)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            db.execSQL(
                """
                CREATE TABLE user_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    category TEXT NOT NULL,
                    important INTEGER NOT NULL,
                    active INTEGER NOT NULL,
                    checkHint TEXT
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE check_sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    startedAtMillis INTEGER NOT NULL,
                    completedAtMillis INTEGER,
                    reminderSent INTEGER NOT NULL,
                    uncheckedCountAtCompletion INTEGER
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                CREATE TABLE session_items (
                    sessionId INTEGER NOT NULL,
                    itemId INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    status TEXT NOT NULL,
                    important INTEGER NOT NULL,
                    checkHint TEXT,
                    PRIMARY KEY(sessionId, itemId)
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX index_session_items_sessionId ON session_items(sessionId)",
            )
            db.execSQL(
                "INSERT INTO user_items (id, name, category, important, active, checkHint) VALUES (1, '휴대폰', 'phone', 1, 0, '예전 자리')",
            )
            db.execSQL(
                "INSERT INTO user_items (id, name, category, important, active, checkHint) VALUES (2, ' 휴대폰 ', 'phone', 0, 1, '가방 앞주머니')",
            )
            db.version = 2
        }

        database = Room.databaseBuilder(context, ChaenggimDatabase::class.java, name)
            .addMigrations(ChaenggimDatabase.MIGRATION_2_3)
            .allowMainThreadQueries()
            .build()

        assertEquals(
            listOf("휴대폰"),
            database.userItemDao().observeAll().first().map { it.name },
        )
        assertEquals(
            listOf(true),
            database.userItemDao().observeAll().first().map { it.active },
        )
        assertEquals(
            listOf(false),
            database.userItemDao().observeAll().first().map { it.important },
        )
        assertEquals(
            listOf("가방 앞주머니"),
            database.userItemDao().observeAll().first().map { it.checkHint },
        )
        try {
            database.userItemDao().upsert(
                UserItemEntity(
                    name = " 휴대폰 ",
                    category = "phone",
                    important = true,
                    active = true,
                    checkHint = null,
                ),
            )
            fail("Duplicate normalized item name should be rejected")
        } catch (_: android.database.sqlite.SQLiteConstraintException) {
            // expected
        }
    }
}
