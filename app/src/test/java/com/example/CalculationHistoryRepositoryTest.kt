package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CalculationHistoryRepository
import com.example.data.local.AppDatabase
import com.example.data.local.SavedCalculation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CalculationHistoryRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: CalculationHistoryRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CalculationHistoryRepository(database.savedCalculationDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveRecentCalculations() = runBlocking {
        repository.save(
            round = "၁ ကြိမ်",
            name = "ကိုအောင်",
            dateText = "17.10.26",
            finalTotal = "- 5,000",
            sign = "-",
            summaryText = "1 ကြိမ်\n\nရောင်း: 10,000 ကျပ်"
        )

        val items = repository.recentCalculations.first()
        assertEquals(1, items.size)
        assertEquals("၁ ကြိမ်", items[0].round)
        assertEquals("ကိုအောင်", items[0].name)
        assertEquals("- 5,000", items[0].finalTotal)
    }

    @Test
    fun testStoreAtMostLast5Records() = runBlocking {
        // Insert 7 items sequentially
        for (i in 1..7) {
            repository.save(
                round = "$i ကြိမ်",
                name = "Customer $i",
                dateText = "17.10.26",
                finalTotal = "+ ${i * 1000}",
                sign = "+",
                summaryText = "Summary $i"
            )
            // ensure strictly increasing timestamp
            Thread.sleep(10)
        }

        val items = repository.recentCalculations.first()
        // Should only retain the last 5 records
        assertEquals(5, items.size)
        // Most recent should be Customer 7, Customer 6, Customer 5, Customer 4, Customer 3
        assertEquals("Customer 7", items[0].name)
        assertEquals("Customer 6", items[1].name)
        assertEquals("Customer 5", items[2].name)
        assertEquals("Customer 4", items[3].name)
        assertEquals("Customer 3", items[4].name)
    }

    @Test
    fun testDeleteAndClear() = runBlocking {
        repository.save(
            round = "၁ ကြိမ်",
            name = "Test",
            dateText = "17.10.26",
            finalTotal = "0",
            sign = "",
            summaryText = "Summary"
        )

        var items = repository.recentCalculations.first()
        assertEquals(1, items.size)

        repository.delete(items[0].id)
        items = repository.recentCalculations.first()
        assertTrue(items.isEmpty())

        repository.save(
            round = "၂ ကြိမ်",
            name = "Test 2",
            dateText = "17.10.26",
            finalTotal = "0",
            sign = "",
            summaryText = "Summary 2"
        )
        repository.clear()
        items = repository.recentCalculations.first()
        assertTrue(items.isEmpty())
    }
}
