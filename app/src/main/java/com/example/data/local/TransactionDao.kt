package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'DEPOSIT' AND status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingDeposits(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = 'WITHDRAWAL' AND status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingWithdrawals(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE transactionId = :id LIMIT 1")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("UPDATE transactions SET status = :status, adminNote = :note WHERE transactionId = :id")
    suspend fun updateTransactionStatus(id: String, status: String, note: String?)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'DEPOSIT' AND (status = 'APPROVED' OR status = 'SUCCESS')")
    fun getTotalApprovedDepositsFlow(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'WITHDRAWAL' AND (status = 'APPROVED' OR status = 'SUCCESS')")
    fun getTotalApprovedWithdrawalsFlow(): Flow<Double?>
}
