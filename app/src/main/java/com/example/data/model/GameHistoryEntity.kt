package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey
    val gameId: String,
    val tableBoot: Double,
    val potAmount: Double,
    val winnerId: String,
    val winnerName: String,
    val winningHandTitle: String,
    val commissionRate: Double,
    val commissionAmount: Double,
    val netPrize: Double,
    val timestamp: Long = System.currentTimeMillis()
)
