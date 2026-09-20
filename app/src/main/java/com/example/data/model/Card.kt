package com.example.data.model

enum class Suit(val symbol: String, val isRed: Boolean) {
    HEARTS("♥", true),
    DIAMONDS("♦", true),
    CLUBS("♣", false),
    SPADES("♠", false)
}

enum class Rank(val value: Int, val label: String) {
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K"),
    ACE(14, "A")
}

data class Card(
    val suit: Suit,
    val rank: Rank
) {
    override fun toString(): String = "${rank.label}${suit.symbol}"
}

enum class HandRankType(val weight: Int, val title: String) {
    TRAIL(6, "Trail / Trio (Three of a Kind)"),
    PURE_SEQUENCE(5, "Pure Sequence (Straight Flush)"),
    SEQUENCE(4, "Sequence (Normal Run)"),
    COLOR(3, "Color (Flush)"),
    PAIR(2, "Pair (Two of a Kind)"),
    HIGH_CARD(1, "High Card")
}

data class HandEvaluationResult(
    val handType: HandRankType,
    val title: String,
    val ranks: List<Int>, // Ordered for tie-breaking
    val cards: List<Card>
) : Comparable<HandEvaluationResult> {
    override fun compareTo(other: HandEvaluationResult): Int {
        if (this.handType.weight != other.handType.weight) {
            return this.handType.weight.compareTo(other.handType.weight)
        }
        for (i in 0 until minOf(this.ranks.size, other.ranks.size)) {
            val cmp = this.ranks[i].compareTo(other.ranks[i])
            if (cmp != 0) return cmp
        }
        return 0
    }
}
