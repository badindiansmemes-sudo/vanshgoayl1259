package com.example.game

import com.example.data.model.Card

data class TableConfig(
    val id: String,
    val name: String,
    val bootAmount: Double,
    val maxBlindRounds: Int = 4,
    val maxPotLimit: Double = 50000.0,
    val tag: String = "Popular"
)

val DEFAULT_TABLES = listOf(
    TableConfig(id = "TBL_10", name = "Bronze Table", bootAmount = 10.0, tag = "Beginner"),
    TableConfig(id = "TBL_50", name = "Silver Table", bootAmount = 50.0, tag = "Popular"),
    TableConfig(id = "TBL_100", name = "Gold Table", bootAmount = 100.0, tag = "High Roller"),
    TableConfig(id = "TBL_500", name = "VIP Diamond Club", bootAmount = 500.0, tag = "VIP Only")
)

data class PlayerSeat(
    val id: String,
    val name: String,
    val avatarId: Int,
    val isHuman: Boolean,
    var chips: Double,
    var cards: List<Card> = emptyList(),
    var isSeen: Boolean = false,
    var isPacked: Boolean = false,
    var currentRoundBet: Double = 0.0,
    var lastAction: String = "",
    var isWinner: Boolean = false
) {
    val isActiveInRound: Boolean
        get() = !isPacked
}

enum class TablePhase {
    IDLE,
    DEALING,
    BETTING,
    SHOWDOWN,
    ROUND_OVER
}

data class TableState(
    val tableConfig: TableConfig,
    val seats: List<PlayerSeat>,
    val potAmount: Double = 0.0,
    val currentTurnIndex: Int = 0,
    val currentBaseBet: Double = 10.0, // Base bet amount for a blind player
    val phase: TablePhase = TablePhase.IDLE,
    val roundMessage: String = "Place your bets",
    val winnerSeat: PlayerSeat? = null,
    val winningHandTitle: String? = null,
    val commissionAmount: Double = 0.0,
    val netPotPrize: Double = 0.0,
    val userSideShowCandidate: PlayerSeat? = null
)
