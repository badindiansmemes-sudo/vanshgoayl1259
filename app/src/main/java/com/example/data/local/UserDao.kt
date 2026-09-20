package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    fun getUserByIdFlow(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :login OR email = :login LIMIT 1")
    suspend fun getUserByPhoneOrEmail(login: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET walletBalance = :wallet, winningBalance = :winning, bonusBalance = :bonus WHERE userId = :userId")
    suspend fun updateBalances(userId: String, wallet: Double, winning: Double, bonus: Double)

    @Query("UPDATE users SET isBanned = :banned WHERE userId = :userId")
    suspend fun setBannedStatus(userId: String, banned: Boolean)

    @Query("UPDATE users SET totalGamesPlayed = totalGamesPlayed + 1, totalGamesWon = totalGamesWon + :isWon WHERE userId = :userId")
    suspend fun incrementGameStats(userId: String, isWon: Int)

    @Query("UPDATE users SET name = :name, phone = :phone, email = :email, avatarId = :avatarId WHERE userId = :userId")
    suspend fun updateProfile(userId: String, name: String, phone: String, email: String, avatarId: Int)

    @Query("UPDATE users SET password = :newPassword WHERE phone = :login OR email = :login")
    suspend fun updatePassword(login: String, newPassword: String): Int

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
