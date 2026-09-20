package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PlatformSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlatformSettingsDao {
    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<PlatformSettingsEntity?>

    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): PlatformSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: PlatformSettingsEntity)

    @Query("UPDATE platform_settings SET totalPlatformRevenue = totalPlatformRevenue + :revenue WHERE id = 1")
    suspend fun addCommissionRevenue(revenue: Double)

    @Query("UPDATE platform_settings SET commissionPercent = :percent, minDeposit = :minDep, minWithdraw = :minWith, adminUpiId = :upiId, adminUpiName = :upiName WHERE id = 1")
    suspend fun updateConfig(percent: Double, minDep: Double, minWith: Double, upiId: String, upiName: String)
}
