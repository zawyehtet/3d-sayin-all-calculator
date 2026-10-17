package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCalculationDao {

    @Query("SELECT * FROM saved_calculations ORDER BY timestamp DESC LIMIT 5")
    fun getRecentCalculations(): Flow<List<SavedCalculation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calculation: SavedCalculation): Long

    @Query("DELETE FROM saved_calculations WHERE id NOT IN (SELECT id FROM saved_calculations ORDER BY timestamp DESC LIMIT 5)")
    suspend fun trimToLast5()

    @Query("DELETE FROM saved_calculations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM saved_calculations")
    suspend fun clearAll()
}
