package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "platform_settings")
data class PlatformSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val commissionPercent: Double = 5.0, // 5% house rake from every pot
    val minDeposit: Double = 100.0,
    val minWithdraw: Double = 200.0,
    val totalPlatformRevenue: Double = 0.0, // accumulated admin commission
    val adminUpiId: String = "royalteenpatti@icici",
    val adminUpiName: String = "Teen Patti Royal House Admin",
    val supportPhone: String = "+91 98765 43210",
    val isGatewayActive: Boolean = true
)
