package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Card
import com.example.data.model.HandRankType
import com.example.data.model.Rank
import com.example.data.model.Suit
import com.example.game.CardEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Teen Patti Royal", appName)
    }

    @Test
    fun `teen patti hand evaluator ranks trail higher than pure sequence`() {
        val trailAces = listOf(
            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.DIAMONDS, Rank.ACE)
        )
        val pureSequenceAKQ = listOf(
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.HEARTS, Rank.QUEEN)
        )

        val evalTrail = CardEvaluator.evaluateHand(trailAces)
        val evalPureSeq = CardEvaluator.evaluateHand(pureSequenceAKQ)

        assertEquals(HandRankType.TRAIL, evalTrail.handType)
        assertEquals(HandRankType.PURE_SEQUENCE, evalPureSeq.handType)
        assertTrue("Trail should beat Pure Sequence", evalTrail > evalPureSeq)
    }

    @Test
    fun `teen patti hand evaluator ranks color higher than pair`() {
        val colorHearts = listOf(
            Card(Suit.HEARTS, Rank.KING),
            Card(Suit.HEARTS, Rank.NINE),
            Card(Suit.HEARTS, Rank.FOUR)
        )
        val pairAces = listOf(
            Card(Suit.SPADES, Rank.ACE),
            Card(Suit.HEARTS, Rank.ACE),
            Card(Suit.CLUBS, Rank.FIVE)
        )

        val evalColor = CardEvaluator.evaluateHand(colorHearts)
        val evalPair = CardEvaluator.evaluateHand(pairAces)

        assertEquals(HandRankType.COLOR, evalColor.handType)
        assertEquals(HandRankType.PAIR, evalPair.handType)
        assertTrue("Color should beat Pair", evalColor > evalPair)
    }
}
