package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_calculations")
data class SavedCalculation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val round: String,
    val name: String,
    val dateText: String,
    val finalTotal: String,
    val sign: String,
    val summaryText: String,
    val timestamp: Long = System.currentTimeMillis()
)
