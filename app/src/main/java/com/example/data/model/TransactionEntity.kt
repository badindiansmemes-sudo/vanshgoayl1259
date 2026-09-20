package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    GAME_WIN,
    GAME_LOSS,
    PLATFORM_COMMISSION
}

enum class TransactionStatus {
    PENDING,
    APPROVED,
    REJECTED,
    SUCCESS
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val transactionId: String,
    val userId: String,
    val userName: String,
    val type: String, // from TransactionType
    val amount: Double,
    val status: String, // from TransactionStatus
    val paymentMethod: String, // e.g. "UPI_GATEWAY", "DIRECT_UPI_UTR", "BANK_TRANSFER", "GAME_TABLE"
    val referenceNumber: String = "", // UTR / Gateway Order ID
    val accountOrUpiDetail: String = "", // destination or source UPI ID / Bank
    val adminNote: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
