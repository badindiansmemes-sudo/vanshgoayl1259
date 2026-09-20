package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.GameHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameHistoryDao {
    @Query("SELECT * FROM game_history ORDER BY timestamp DESC")
    fun getAllGames(): Flow<List<GameHistoryEntity>>

    @Query("SELECT * FROM game_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentGames(limit: Int): Flow<List<GameHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameHistoryEntity)

    @Query("SELECT SUM(commissionAmount) FROM game_history")
    fun getTotalRakeEarningsFlow(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM game_history")
    fun getTotalGamesCountFlow(): Flow<Int>

    @Query("SELECT SUM(potAmount) FROM game_history")
    fun getTotalPotTurnoverFlow(): Flow<Double?>
}
