package com.example.data

import com.example.data.local.SavedCalculation
import com.example.data.local.SavedCalculationDao
import kotlinx.coroutines.flow.Flow

class CalculationHistoryRepository(
    private val dao: SavedCalculationDao
) {
    val recentCalculations: Flow<List<SavedCalculation>> = dao.getRecentCalculations()

    suspend fun save(
        round: String,
        name: String,
        dateText: String,
        finalTotal: String,
        sign: String,
        summaryText: String
    ) {
        val item = SavedCalculation(
            round = round,
            name = name,
            dateText = dateText,
            finalTotal = finalTotal,
            sign = sign,
            summaryText = summaryText
        )
        dao.insert(item)
        dao.trimToLast5()
    }

    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clear() {
        dao.clearAll()
    }
}
