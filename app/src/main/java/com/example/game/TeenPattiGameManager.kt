package com.example.game

import com.example.data.model.Card
import com.example.data.model.HandRankType
import com.example.data.model.Rank
import com.example.data.model.Suit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeenPattiGameManager(
    private val scope: CoroutineScope,
    private val onRoundFinished: (boot: Double, pot: Double, winner: PlayerSeat, handTitle: String, isHumanWinner: Boolean) -> Unit,
    private val onDeductBet: suspend (Double) -> Boolean
) {

    private val _tableState = MutableStateFlow<TableState?>(null)
    val tableState: StateFlow<TableState?> = _tableState.asStateFlow()

    fun setupTable(config: TableConfig, humanName: String, humanChips: Double) {
        val initialSeats = listOf(
            PlayerSeat(id = "USER", name = humanName, avatarId = 1, isHuman = true, chips = humanChips),
            PlayerSeat(id = "BOT_1", name = "Vikram", avatarId = 2, isHuman = false, chips = 2500.0),
            PlayerSeat(id = "BOT_2", name = "Aarav", avatarId = 3, isHuman = false, chips = 1800.0),
            PlayerSeat(id = "BOT_3", name = "Kabir", avatarId = 4, isHuman = false, chips = 3200.0)
        )

        _tableState.value = TableState(
            tableConfig = config,
            seats = initialSeats,
            potAmount = 0.0,
            currentTurnIndex = 0,
            currentBaseBet = config.bootAmount,
            phase = TablePhase.IDLE,
            roundMessage = "Tap 'Start New Game' to play!"
        )
    }

    fun startNewRound() {
        val current = _tableState.value ?: return
        val boot = current.tableConfig.bootAmount

        // Generate and shuffle standard 52 deck
        val deck = mutableListOf<Card>()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deck.add(Card(suit, rank))
            }
        }
        deck.shuffle()

        // Deduct boot from all players
        var pot = 0.0
        val updatedSeats = current.seats.mapIndexed { index, seat ->
            val cards = listOf(deck.removeAt(0), deck.removeAt(0), deck.removeAt(0))
            val seatChips = maxOf(0.0, seat.chips - boot)
            pot += boot
            seat.copy(
                chips = seatChips,
                cards = cards,
                isSeen = false,
                isPacked = false,
                currentRoundBet = boot,
                lastAction = "Boot ₹${boot.toInt()}",
                isWinner = false
            )
        }

        // Notify deduction for human user
        scope.launch {
            onDeductBet(boot)
        }

        _tableState.value = current.copy(
            seats = updatedSeats,
            potAmount = pot,
            currentTurnIndex = 0, // Human plays first or rotates
            currentBaseBet = boot,
            phase = TablePhase.BETTING,
            roundMessage = "Round started. Boot ₹${boot.toInt()} collected from 4 players.",
            winnerSeat = null,
            winningHandTitle = null,
            commissionAmount = 0.0,
            netPotPrize = 0.0
        )
    }

    fun userSeeCards() {
        _tableState.update { current ->
            if (current == null) return@update null
            val updated = current.seats.map {
                if (it.isHuman) it.copy(isSeen = true, lastAction = "Seen Cards") else it
            }
            current.copy(seats = updated, roundMessage = "You saw your cards.")
        }
    }

    fun userChaal(isDouble: Boolean = false) {
        val current = _tableState.value ?: return
        if (current.phase != TablePhase.BETTING) return
        val userSeat = current.seats.firstOrNull { it.isHuman } ?: return
        if (current.currentTurnIndex != 0 || userSeat.isPacked) return

        val requiredMultiplier = if (userSeat.isSeen) 2.0 else 1.0
        val betMultiplier = if (isDouble) 2.0 else 1.0
        val betAmount = current.currentBaseBet * requiredMultiplier * betMultiplier

        scope.launch {
            val deducted = onDeductBet(betAmount)
            if (!deducted) {
                _tableState.update { it?.copy(roundMessage = "Insufficient balance to place bet of ₹${betAmount.toInt()}!") }
                return@launch
            }

            _tableState.update { state ->
                if (state == null) return@update null
                val updatedSeats = state.seats.mapIndexed { idx, s ->
                    if (idx == 0) {
                        s.copy(
                            chips = maxOf(0.0, s.chips - betAmount),
                            currentRoundBet = s.currentRoundBet + betAmount,
                            lastAction = if (s.isSeen) "Chaal ₹${betAmount.toInt()}" else "Blind ₹${betAmount.toInt()}"
                        )
                    } else s
                }
                val newBaseBet = if (isDouble) state.currentBaseBet * 2.0 else state.currentBaseBet
                state.copy(
                    seats = updatedSeats,
                    potAmount = state.potAmount + betAmount,
                    currentBaseBet = newBaseBet,
                    roundMessage = "You played ₹${betAmount.toInt()}"
                )
            }

            advanceTurn()
        }
    }

    fun userPack() {
        val current = _tableState.value ?: return
        if (current.phase != TablePhase.BETTING) return
        val userSeat = current.seats.firstOrNull { it.isHuman } ?: return
        if (current.currentTurnIndex != 0 || userSeat.isPacked) return

        _tableState.update { state ->
            if (state == null) return@update null
            val updatedSeats = state.seats.mapIndexed { idx, s ->
                if (idx == 0) s.copy(isPacked = true, lastAction = "Packed (Fold)") else s
            }
            state.copy(seats = updatedSeats, roundMessage = "You folded (Packed).")
        }

        checkRoundEndOrNext()
    }

    fun userShow() {
        val current = _tableState.value ?: return
        val activePlayers = current.seats.filter { it.isActiveInRound }
        if (activePlayers.size != 2) return // Show is only valid when 2 players remain
        if (current.currentTurnIndex != 0) return

        // Deduct show fee (chaal amount)
        val userSeat = current.seats[0]
        val requiredMultiplier = if (userSeat.isSeen) 2.0 else 1.0
        val showCost = current.currentBaseBet * requiredMultiplier

        scope.launch {
            val deducted = onDeductBet(showCost)
            if (!deducted) return@launch

            _tableState.update { state ->
                if (state == null) return@update null
                val updated = state.seats.mapIndexed { idx, s ->
                    if (idx == 0) s.copy(chips = maxOf(0.0, s.chips - showCost), lastAction = "SHOW") else s
                }
                state.copy(
                    seats = updated,
                    potAmount = state.potAmount + showCost,
                    phase = TablePhase.SHOWDOWN,
                    roundMessage = "SHOW called! Evaluating cards..."
                )
            }

            delay(1000)
            settleShowdown()
        }
    }

    private fun advanceTurn() {
        val current = _tableState.value ?: return
        val activePlayers = current.seats.filter { it.isActiveInRound }
        if (activePlayers.size <= 1) {
            concludeRoundWithSingleSurvivor()
            return
        }

        // Find next active player
        var nextIdx = (current.currentTurnIndex + 1) % current.seats.size
        while (current.seats[nextIdx].isPacked) {
            nextIdx = (nextIdx + 1) % current.seats.size
        }

        _tableState.update { it?.copy(currentTurnIndex = nextIdx) }

        if (!current.seats[nextIdx].isHuman) {
            scheduleBotTurn(nextIdx)
        }
    }

    private fun scheduleBotTurn(botIndex: Int) {
        scope.launch {
            delay(1200) // Realistic thinking time
            val current = _tableState.value ?: return@launch
            if (current.phase != TablePhase.BETTING) return@launch
            if (current.currentTurnIndex != botIndex) return@launch

            val bot = current.seats[botIndex]
            if (bot.isPacked) {
                advanceTurn()
                return@launch
            }

            // Bot decision engine
            val handEval = CardEvaluator.evaluateHand(bot.cards)
            // Bots see cards after 1 blind turn or immediately if good hand
            var isBotSeen = bot.isSeen
            if (!isBotSeen && (handEval.handType.weight >= HandRankType.SEQUENCE.weight || Math.random() > 0.4)) {
                isBotSeen = true
            }

            val activeCount = current.seats.count { it.isActiveInRound }

            // If only 2 players left, strong bots might SHOW
            if (activeCount == 2 && handEval.handType.weight >= HandRankType.COLOR.weight && Math.random() > 0.5) {
                // Bot calls SHOW
                val multiplier = if (isBotSeen) 2.0 else 1.0
                val cost = current.currentBaseBet * multiplier
                _tableState.update { state ->
                    if (state == null) return@update null
                    val updated = state.seats.mapIndexed { idx, s ->
                        if (idx == botIndex) s.copy(
                            isSeen = true,
                            chips = maxOf(0.0, s.chips - cost),
                            lastAction = "SHOW"
                        ) else s
                    }
                    state.copy(
                        seats = updated,
                        potAmount = state.potAmount + cost,
                        phase = TablePhase.SHOWDOWN,
                        roundMessage = "${bot.name} called SHOW!"
                    )
                }
                delay(1000)
                settleShowdown()
                return@launch
            }

            // Decide fold or call/chaal
            val shouldFold = when (handEval.handType) {
                HandRankType.HIGH_CARD -> Math.random() > 0.45 && current.potAmount > current.tableConfig.bootAmount * 5
                HandRankType.PAIR -> Math.random() > 0.8 && current.potAmount > current.tableConfig.bootAmount * 10
                else -> false // Never fold with color, sequence, pure sequence or trail
            }

            if (shouldFold) {
                _tableState.update { state ->
                    if (state == null) return@update null
                    val updated = state.seats.mapIndexed { idx, s ->
                        if (idx == botIndex) s.copy(isPacked = true, lastAction = "Packed") else s
                    }
                    state.copy(seats = updated, roundMessage = "${bot.name} folded (Packed).")
                }
                checkRoundEndOrNext()
            } else {
                // Chaal
                val multiplier = if (isBotSeen) 2.0 else 1.0
                val betAmount = current.currentBaseBet * multiplier
                _tableState.update { state ->
                    if (state == null) return@update null
                    val updated = state.seats.mapIndexed { idx, s ->
                        if (idx == botIndex) s.copy(
                            isSeen = isBotSeen,
                            chips = maxOf(0.0, s.chips - betAmount),
                            lastAction = if (isBotSeen) "Chaal ₹${betAmount.toInt()}" else "Blind ₹${betAmount.toInt()}"
                        ) else s
                    }
                    state.copy(
                        seats = updated,
                        potAmount = state.potAmount + betAmount,
                        roundMessage = "${bot.name} played ₹${betAmount.toInt()} (${if (isBotSeen) "Chaal" else "Blind"})"
                    )
                }
                advanceTurn()
            }
        }
    }

    private fun checkRoundEndOrNext() {
        val current = _tableState.value ?: return
        val active = current.seats.filter { it.isActiveInRound }
        if (active.size <= 1) {
            concludeRoundWithSingleSurvivor()
        } else {
            advanceTurn()
        }
    }

    private fun concludeRoundWithSingleSurvivor() {
        val current = _tableState.value ?: return
        val survivor = current.seats.firstOrNull { it.isActiveInRound } ?: current.seats[0]
        val handEval = CardEvaluator.evaluateHand(survivor.cards)

        finishRound(survivor, handEval.title, reason = "${survivor.name} wins! All other players folded.")
    }

    private fun settleShowdown() {
        val current = _tableState.value ?: return
        val active = current.seats.filter { it.isActiveInRound }
        if (active.isEmpty()) return

        var bestSeat = active[0]
        var bestHand = CardEvaluator.evaluateHand(bestSeat.cards)

        for (i in 1 until active.size) {
            val candidate = active[i]
            val candidateHand = CardEvaluator.evaluateHand(candidate.cards)
            if (candidateHand > bestHand) {
                bestSeat = candidate
                bestHand = candidateHand
            }
        }

        // Reveal all cards on showdown
        val revealedSeats = current.seats.map { it.copy(isSeen = true) }
        _tableState.update { it?.copy(seats = revealedSeats) }

        finishRound(bestSeat, bestHand.title, reason = "${bestSeat.name} wins Showdown with ${bestHand.title}!")
    }

    private fun finishRound(winner: PlayerSeat, handTitle: String, reason: String) {
        val current = _tableState.value ?: return
        val pot = current.potAmount
        val boot = current.tableConfig.bootAmount
        val isHumanWinner = winner.isHuman

        // Reveal winner's cards and mark winner
        val updatedSeats = current.seats.map {
            if (it.id == winner.id) it.copy(isWinner = true, isSeen = true) else it.copy(isWinner = false)
        }

        _tableState.update { state ->
            if (state == null) return@update null
            state.copy(
                seats = updatedSeats,
                phase = TablePhase.ROUND_OVER,
                winnerSeat = winner,
                winningHandTitle = handTitle,
                roundMessage = reason
            )
        }

        onRoundFinished(boot, pot, winner, handTitle, isHumanWinner)
    }
}
