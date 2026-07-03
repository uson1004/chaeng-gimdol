package com.yuseob.chaenggimdol.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yuseob.chaenggimdol.data.local.ChaenggimDatabase
import com.yuseob.chaenggimdol.data.local.UserItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
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
            ),
        )

        assertEquals(
            "충전기",
            database.userItemDao().observeAll().first().single().name,
        )
    }
}
