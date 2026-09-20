package com.example.game

import com.example.data.model.Card
import com.example.data.model.HandEvaluationResult
import com.example.data.model.HandRankType
import com.example.data.model.Rank

object CardEvaluator {

    fun evaluateHand(cards: List<Card>): HandEvaluationResult {
        require(cards.size == 3) { "Teen Patti hand must consist of exactly 3 cards" }

        val sorted = cards.sortedByDescending { it.rank.value }
        val r1 = sorted[0].rank.value
        val r2 = sorted[1].rank.value
        val r3 = sorted[2].rank.value

        val sameSuit = cards[0].suit == cards[1].suit && cards[1].suit == cards[2].suit

        // 1. Trail / Trio (e.g. AAA, 888)
        if (r1 == r2 && r2 == r3) {
            return HandEvaluationResult(
                handType = HandRankType.TRAIL,
                title = "Trail (${sorted[0].rank.label}'s)",
                ranks = listOf(r1),
                cards = sorted
            )
        }

        // Check Sequence (A-K-Q is highest, A-2-3 is second highest, then K-Q-J down to 4-3-2)
        val isA23 = (r1 == Rank.ACE.value && r2 == Rank.THREE.value && r3 == Rank.TWO.value)
        val isNormalSequence = (r1 - 1 == r2 && r2 - 1 == r3)
        val isSequence = isNormalSequence || isA23

        val sequenceRanks = if (isA23) {
            // In Teen Patti, A-2-3 ranks higher than K-Q-J but lower than A-K-Q.
            // A-K-Q has value 14. We can assign A-2-3 an effective sequence high rank of 13.5 (represented as 135)
            // or integer ranks: A-K-Q -> 14, A-2-3 -> 13, K-Q-J -> 12, etc.
            listOf(13, 3, 2)
        } else if (r1 == Rank.ACE.value && r2 == Rank.KING.value && r3 == Rank.QUEEN.value) {
            listOf(14, 13, 12)
        } else {
            listOf(r1 - 1, r2, r3) // K-Q-J has r1=13, so value is mapped to r1-1 = 12
        }

        // 2. Pure Sequence (Straight Flush)
        if (isSequence && sameSuit) {
            return HandEvaluationResult(
                handType = HandRankType.PURE_SEQUENCE,
                title = "Pure Sequence (${sorted.joinToString("-") { it.rank.label }})",
                ranks = sequenceRanks,
                cards = sorted
            )
        }

        // 3. Normal Sequence
        if (isSequence) {
            return HandEvaluationResult(
                handType = HandRankType.SEQUENCE,
                title = "Sequence (${sorted.joinToString("-") { it.rank.label }})",
                ranks = sequenceRanks,
                cards = sorted
            )
        }

        // 4. Color (Flush)
        if (sameSuit) {
            return HandEvaluationResult(
                handType = HandRankType.COLOR,
                title = "Color / Flush (${sorted[0].suit.symbol} ${sorted[0].rank.label} High)",
                ranks = listOf(r1, r2, r3),
                cards = sorted
            )
        }

        // 5. Pair
        if (r1 == r2 || r2 == r3 || r1 == r3) {
            val pairVal: Int
            val kickerVal: Int
            if (r1 == r2) {
                pairVal = r1
                kickerVal = r3
            } else if (r2 == r3) {
                pairVal = r2
                kickerVal = r1
            } else {
                pairVal = r1
                kickerVal = r2
            }
            val pairLabel = Rank.values().firstOrNull { it.value == pairVal }?.label ?: "$pairVal"
            return HandEvaluationResult(
                handType = HandRankType.PAIR,
                title = "Pair of $pairLabel",
                ranks = listOf(pairVal, kickerVal),
                cards = sorted
            )
        }

        // 6. High Card
        return HandEvaluationResult(
            handType = HandRankType.HIGH_CARD,
            title = "High Card (${sorted[0].rank.label})",
            ranks = listOf(r1, r2, r3),
            cards = sorted
        )
    }

    fun compareHands(handA: List<Card>, handB: List<Card>): Int {
        val evalA = evaluateHand(handA)
        val evalB = evaluateHand(handB)
        return evalA.compareTo(evalB)
    }
}
