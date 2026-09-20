package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.CardEvaluator
import com.example.game.PlayerSeat
import com.example.game.TablePhase
import com.example.game.TableState
import com.example.ui.components.PlayingCardView
import com.example.ui.theme.CasinoGreenDark
import com.example.ui.theme.CasinoGreenLight
import com.example.ui.theme.CasinoGreenPrimary
import com.example.ui.theme.CasinoTableFelt
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TeenPattiViewModel

@Composable
fun GameTableScreen(
    viewModel: TeenPattiViewModel,
    onLeaveTable: () -> Unit
) {
    val tableState by viewModel.tableState.collectAsState()
    val platformSettings by viewModel.platformSettings.collectAsState()
    val manager = viewModel.gameManager

    if (tableState == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(CasinoGreenDark),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading Table...", color = GoldLight)
        }
        return
    }

    val state = tableState!!
    val userSeat = state.seats.firstOrNull { it.isHuman }
    val isUserTurn = state.currentTurnIndex == 0 && state.phase == TablePhase.BETTING && userSeat?.isPacked == false

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF0F3820), Color(0xFF071F13), Color(0xFF030D08))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar with Table Name, Boot stake, Pot, and Exit
            TableTopBar(
                tableName = state.tableConfig.name,
                boot = state.tableConfig.bootAmount,
                rakePercent = platformSettings.commissionPercent,
                onLeave = onLeaveTable
            )

            // Table Arena (Oval Casino Table)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Table Felt Surface
                OvalTableSurface(
                    modifier = Modifier.fillMaxSize()
                )

                // Center Pot Display
                CenterPotDisplay(
                    potAmount = state.potAmount,
                    roundMessage = state.roundMessage,
                    rakePercent = platformSettings.commissionPercent,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Seated Players around the table
                // Top Seat: Bot 2 (Aarav)
                if (state.seats.size > 2) {
                    PlayerSeatView(
                        seat = state.seats[2],
                        isCurrentTurn = state.currentTurnIndex == 2 && state.phase == TablePhase.BETTING,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                    )
                }

                // Left Seat: Bot 1 (Vikram)
                if (state.seats.size > 1) {
                    PlayerSeatView(
                        seat = state.seats[1],
                        isCurrentTurn = state.currentTurnIndex == 1 && state.phase == TablePhase.BETTING,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                    )
                }

                // Right Seat: Bot 3 (Kabir)
                if (state.seats.size > 3) {
                    PlayerSeatView(
                        seat = state.seats[3],
                        isCurrentTurn = state.currentTurnIndex == 3 && state.phase == TablePhase.BETTING,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    )
                }

                // Bottom Seat: Human Player
                if (userSeat != null) {
                    HumanPlayerSeatView(
                        userSeat = userSeat,
                        isCurrentTurn = isUserTurn,
                        onSeeCards = { manager?.userSeeCards() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 6.dp)
                    )
                }
            }

            // Bottom Action Controls Bar
            TableActionBar(
                state = state,
                isUserTurn = isUserTurn,
                onChaal = { manager?.userChaal(isDouble = false) },
                onChaalDouble = { manager?.userChaal(isDouble = true) },
                onPack = { manager?.userPack() },
                onShow = { manager?.userShow() },
                onStartGame = { manager?.startNewRound() }
            )
        }

        // Showdown / Round Winner Dialog
        if (state.phase == TablePhase.ROUND_OVER && state.winnerSeat != null) {
            RoundWinnerDialog(
                winner = state.winnerSeat!!,
                handTitle = state.winningHandTitle ?: "Winning Hand",
                pot = state.potAmount,
                rakePercent = platformSettings.commissionPercent,
                onNextRound = { manager?.startNewRound() },
                onExit = onLeaveTable
            )
        }
    }
}

@Composable
fun TableTopBar(
    tableName: String,
    boot: Double,
    rakePercent: Double,
    onLeave: () -> Unit
) {
    Surface(
        color = Color(0x99000000),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLeave, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit Table",
                        tint = GoldLight
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = tableName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Boot: ₹${boot.toInt()} • Platform Rake: ${rakePercent}%",
                        color = GoldAccent,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x3300E676))
                    .border(1.dp, Color(0x6600E676), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "LIVE TABLE",
                    color = Color(0xFF00E676),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun OvalTableSurface(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(120.dp))
            .background(
                Brush.radialGradient(
                    listOf(CasinoGreenLight.copy(alpha = 0.8f), CasinoTableFelt, Color(0xFF072415))
                )
            )
            .border(8.dp, Color(0xFF3E2723), RoundedCornerShape(120.dp)) // Wooden table rim
            .border(2.dp, Color(0x66FFD700), RoundedCornerShape(120.dp)) // Gold pinstripe
    )
}

@Composable
fun CenterPotDisplay(
    potAmount: Double,
    roundMessage: String,
    rakePercent: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xBB000000))
            .border(1.5.dp, Color(0x88FFD700), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("₹", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "TOTAL POT: ₹${potAmount.toInt()}",
                color = GoldPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Platform Rake: ${rakePercent}% deducted on win",
            color = Color(0xFFA5D6A7),
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = roundMessage,
            color = TextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PlayerSeatView(
    seat: PlayerSeat,
    isCurrentTurn: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseBorder by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Player Avatar with turn indicator
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (seat.isPacked) Color.DarkGray else Color(0xFF1E3A2F))
                .border(
                    width = if (isCurrentTurn) 2.5.dp else 1.dp,
                    color = if (isCurrentTurn) GoldPrimary.copy(alpha = pulseBorder) else Color(0x44FFFFFF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = seat.name.take(1),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = seat.name,
            color = if (seat.isPacked) Color.Gray else TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "₹${seat.chips.toInt()}",
            color = GoldLight,
            fontSize = 10.sp
        )

        // Status Tag
        if (seat.lastAction.isNotBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (seat.isPacked) StatusRejected else CasinoGreenPrimary)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = seat.lastAction,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Cards (Face down unless showdown/winner)
        Row(horizontalArrangement = Arrangement.spacedBy((-16).dp)) {
            val cardsToShow = if (seat.cards.size == 3) seat.cards else listOf(null, null, null)
            cardsToShow.forEach { card ->
                PlayingCardView(
                    card = card,
                    isFaceUp = seat.isSeen || seat.isWinner,
                    cardWidth = 36.dp,
                    cardHeight = 52.dp,
                    isWinnerHighlight = seat.isWinner
                )
            }
        }
    }
}

@Composable
fun HumanPlayerSeatView(
    userSeat: PlayerSeat,
    isCurrentTurn: Boolean,
    onSeeCards: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hand type title if user has seen cards
        if (userSeat.isSeen && userSeat.cards.size == 3) {
            val eval = CardEvaluator.evaluateHand(userSeat.cards)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCC000000))
                    .border(1.dp, GoldPrimary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = eval.title,
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 3 Cards Fan
        Row(
            horizontalArrangement = Arrangement.spacedBy((-18).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val cards = if (userSeat.cards.size == 3) userSeat.cards else listOf(null, null, null)
            cards.forEach { card ->
                PlayingCardView(
                    card = card,
                    isFaceUp = userSeat.isSeen,
                    cardWidth = 58.dp,
                    cardHeight = 84.dp,
                    isWinnerHighlight = userSeat.isWinner
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Human status pill & See Cards toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xCC000000))
                    .border(1.dp, if (isCurrentTurn) GoldPrimary else Color.Transparent, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "You (${userSeat.name}) • ₹${userSeat.chips.toInt()}",
                    color = if (isCurrentTurn) GoldLight else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!userSeat.isSeen && userSeat.isActiveInRound) {
                Button(
                    onClick = onSeeCards,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "See Cards",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SEE", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun TableActionBar(
    state: TableState,
    isUserTurn: Boolean,
    onChaal: () -> Unit,
    onChaalDouble: () -> Unit,
    onPack: () -> Unit,
    onShow: () -> Unit,
    onStartGame: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (state.phase == TablePhase.IDLE || state.phase == TablePhase.ROUND_OVER) {
                // Button to start next hand
                Button(
                    onClick = onStartGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (state.phase == TablePhase.IDLE) "START NEW GAME" else "PLAY NEXT ROUND",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                val userSeat = state.seats[0]
                val multiplier = if (userSeat.isSeen) 2.0 else 1.0
                val singleBet = (state.currentBaseBet * multiplier).toInt()
                val doubleBet = singleBet * 2
                val activeCount = state.seats.count { it.isActiveInRound }
                val canShow = activeCount == 2 && isUserTurn

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pack Button
                    Button(
                        onClick = onPack,
                        enabled = isUserTurn,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusRejected,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0x33FF5252)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PACK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Chaal / Blind 1x
                    Button(
                        onClick = onChaal,
                        enabled = isUserTurn,
                        modifier = Modifier
                            .weight(1.4f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0x44FFD700)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (userSeat.isSeen) "CHAAL ₹$singleBet" else "BLIND ₹$singleBet",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // 2x Chaal
                    Button(
                        onClick = onChaalDouble,
                        enabled = isUserTurn,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CasinoGreenLight,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0x33198754)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("2X ₹$doubleBet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Show Button (Only when 2 players remain)
                    if (canShow) {
                        Button(
                            onClick = onShow,
                            modifier = Modifier
                                .weight(1.2f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7B1FA2),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("SHOW", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoundWinnerDialog(
    winner: PlayerSeat,
    handTitle: String,
    pot: Double,
    rakePercent: Double,
    onNextRound: () -> Unit,
    onExit: () -> Unit
) {
    val commission = (pot * rakePercent) / 100.0
    val netPrize = pot - commission

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2618)),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, GoldPrimary, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = GoldPrimary,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (winner.isHuman) "🎉 CONGRATULATIONS! YOU WON! 🎉" else "${winner.name} WINS THE ROUND!",
                    color = GoldLight,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Winning Hand: $handTitle",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown: Pot - Commission = Net
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66000000))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Gross Pot Amount:", color = TextSecondary, fontSize = 12.sp)
                        Text("₹${pot.toInt()}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("House Platform Rake (${rakePercent}%):", color = TextSecondary, fontSize = 12.sp)
                        Text("-₹${commission.toInt()}", color = StatusRejected, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .height(1.dp)
                            .background(Color(0x33FFFFFF))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Net Winner Payout:", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("₹${netPrize.toInt()}", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onExit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Lobby")
                    }

                    Button(
                        onClick = onNextRound,
                        modifier = Modifier.weight(1.4f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Play Again", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
