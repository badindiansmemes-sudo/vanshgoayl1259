package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val walletBalance: Double = 500.0, // default welcome deposit chips
    val winningBalance: Double = 0.0,
    val bonusBalance: Double = 100.0, // ₹100 signup bonus
    val isAdmin: Boolean = false,
    val isBanned: Boolean = false,
    val avatarId: Int = 1,
    val totalGamesPlayed: Int = 0,
    val totalGamesWon: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalAvailableBalance: Double
        get() = walletBalance + winningBalance + bonusBalance
}
