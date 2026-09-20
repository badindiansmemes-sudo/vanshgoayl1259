package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Card
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SuitBlack
import com.example.ui.theme.SuitRed

@Composable
fun PlayingCardView(
    card: Card?,
    isFaceUp: Boolean,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 68.dp,
    cardHeight: Dp = 98.dp,
    isWinnerHighlight: Boolean = false
) {
    val borderColor = if (isWinnerHighlight) GoldPrimary else Color(0x33FFD700)
    val borderWidth = if (isWinnerHighlight) 2.5.dp else 1.dp

    Box(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .shadow(if (isWinnerHighlight) 10.dp else 4.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
    ) {
        if (isFaceUp && card != null) {
            // Face Up Card
            val textColor = if (card.suit.isRed) SuitRed else SuitBlack

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFFFFFF), Color(0xFFF7F7F7), Color(0xFFECEFF1))
                        )
                    )
                    .padding(4.dp)
            ) {
                // Top-Left corner rank & suit
                Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.rank.label,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 12.sp
                    )
                    Text(
                        text = card.suit.symbol,
                        color = textColor,
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    )
                }

                // Center Large Suit Emblem
                Text(
                    text = card.suit.symbol,
                    color = textColor,
                    fontSize = 28.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Bottom-Right inverted corner rank & suit
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .rotate(180f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.rank.label,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 12.sp
                    )
                    Text(
                        text = card.suit.symbol,
                        color = textColor,
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    )
                }
            }
        } else {
            // Face Down Card (Royal Casino Back)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF800020), Color(0xFF4A0012), Color(0xFF2B000B))
                        )
                    )
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color(0x66FFD700), RoundedCornerShape(5.dp))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "♠",
                            color = GoldPrimary,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "TP",
                            color = GoldAccent,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
