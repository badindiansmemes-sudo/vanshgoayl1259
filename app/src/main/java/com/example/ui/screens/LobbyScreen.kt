package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.game.DEFAULT_TABLES
import com.example.game.TableConfig
import com.example.ui.components.TopBarHeader
import com.example.ui.theme.CasinoGreenDark
import com.example.ui.theme.CasinoGreenLight
import com.example.ui.theme.CasinoGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.TeenPattiViewModel
import kotlinx.coroutines.delay

@Composable
fun LobbyScreen(
    viewModel: TeenPattiViewModel,
    currentUser: UserEntity?,
    onNavigate: (AppScreen) -> Unit
) {
    var showRankings by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF021008), Color(0xFF04180D), Color(0xFF010603))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            TopBarHeader(
                currentUser = currentUser,
                onWalletClick = { onNavigate(AppScreen.WALLET) },
                onProfileClick = { onNavigate(AppScreen.PROFILE) }
            )

            // Live Winner Marquee / Ticker
            LiveWinnerTicker()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Total Play Balance Card
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    WalletBalanceCard(
                        currentUser = currentUser,
                        onDepositClick = { onNavigate(AppScreen.WALLET) },
                        onWithdrawClick = { onNavigate(AppScreen.WALLET) }
                    )
                }

                // 2. Quick Recharge Chips Row
                item {
                    QuickRechargeChipsRow(
                        onSelectAmount = { _ ->
                            onNavigate(AppScreen.WALLET)
                        }
                    )
                }

                // 3. Mega Jackpot Banner Card
                item {
                    MegaJackpotBannerCard(
                        onPlayJackpot = {
                            val vipTable = DEFAULT_TABLES.lastOrNull() ?: DEFAULT_TABLES.first()
                            viewModel.joinTable(vipTable)
                        }
                    )
                }

                // 4. Daily VIP Free Chips Bonus Card
                item {
                    DailyBonusCard(
                        onClaim = { viewModel.claimDailyBonus() }
                    )
                }

                // 5. Live Online Players & Security Trust Badge
                item {
                    LiveStatsAndTrustBadge()
                }

                // 6. Section Header for Tables
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Casino Tables",
                                color = GoldLight,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "🟢 1,420 Active Tables",
                            color = Color(0xFF69F0AE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // 7. Table Cards
                items(DEFAULT_TABLES) { table ->
                    TableSelectionCard(
                        table = table,
                        userBalance = currentUser?.totalAvailableBalance ?: 0.0,
                        onJoin = { viewModel.joinTable(table) },
                        onNeedCash = { onNavigate(AppScreen.WALLET) }
                    )
                }

                // 8. Teen Patti Hand Rankings Guide (Expandable)
                item {
                    HandRankingsCard(
                        isExpanded = showRankings,
                        onToggle = { showRankings = !showRankings }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
fun LiveWinnerTicker() {
    val announcements = remember {
        listOf(
            "🏆 Sunil K. (Mumbai) won ₹14,500 on Diamond Club with Trail of Kings!",
            "⚡ Instant UPI ₹5,000 withdrawal processed in 14 seconds for Pooja R.!",
            "🔥 Amit P. won ₹8,400 with Pure Sequence (A-K-Q) on Silver Table!",
            "💎 Rahul S. unlocked VIP Platinum Level & claimed ₹1,000 Bonus!",
            "⚡ Instant Payout: ₹12,000 credited to Bank via IMPS (Ref #TP9921)"
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3800)
            currentIndex = (currentIndex + 1) % announcements.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF2E1303), Color(0xFF421C00), Color(0xFF260F00))
                )
            )
            .border(
                width = 0.5.dp,
                brush = Brush.horizontalGradient(listOf(Color(0x88FFD700), Color(0x33FF9800))),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LIVE:",
                color = Color(0xFFFFD54F),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = announcements[currentIndex],
                color = Color(0xFFFFE0B2),
                fontSize = 11.sp,
                maxLines = 1,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MegaJackpotBannerCard(onPlayJackpot: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onPlayJackpot() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1005))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2E1705), Color(0xFF422108), Color(0xFF241203))
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        listOf(GoldPrimary, GoldLight, Color(0xFFFF9800), GoldPrimary)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(GoldLight, GoldDark))
                            )
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MEGA JACKPOT",
                                color = GoldLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE53935))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text("LIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(
                            text = "₹2,50,000",
                            color = Color(0xFFFFD54F),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Win with Trail of Aces (A-A-A) • Instant Payout",
                            color = Color(0xFFCFD8DC),
                            fontSize = 9.sp
                        )
                    }
                }

                Button(
                    onClick = onPlayJackpot,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("PLAY NOW", fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun DailyBonusCard(onClaim: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClaim() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2618))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0A2B19), Color(0xFF144527), Color(0xFF0E381F))
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(listOf(GoldPrimary, Color(0xFF00E676))),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(GoldLight, GoldDark))
                            )
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Bonus",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🎁 Daily VIP Login Bonus",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Get ₹500 Free Chips every day to play & win!",
                            color = Color(0xFFA5D6A7),
                            fontSize = 10.sp
                        )
                    }
                }

                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("CLAIM ₹500", fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun QuickRechargeChipsRow(onSelectAmount: (Double) -> Unit) {
    val quickOptions = listOf(
        Pair(100.0, "+10% EXTRA"),
        Pair(500.0, "🔥 +20% HOT"),
        Pair(1000.0, "+30% VIP"),
        Pair(5000.0, "👑 +50% KING")
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚡ Instant Recharge Offers",
                color = Color(0xFFFFD54F),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Zero Fee • Instant UPI",
                color = TextMuted,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(quickOptions) { (amt, tag) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E2822), Color(0xFF0F1A13))
                            )
                        )
                        .border(1.dp, Color(0x55FFD700), RoundedCornerShape(10.dp))
                        .clickable { onSelectAmount(amt) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "₹${amt.toInt()}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (tag.contains("HOT")) Color(0xFFFF3D00) else Color(0x33FFD700))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = tag,
                                color = if (tag.contains("HOT")) Color.White else GoldPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveStatsAndTrustBadge() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x3300C853))
            .border(1.dp, Color(0x4400E676), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Safe",
            tint = Color(0xFF00E676),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "100% Certified RNG Fair Play • Instant UPI Cashouts • 24/7 Support",
            color = Color(0xFFB9F6CA),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun WalletBalanceCard(
    currentUser: UserEntity?,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0D331B), Color(0xFF144D29), Color(0xFF0B2816))
                    )
                )
                .border(1.2.dp, Color(0x66FFD700), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "TOTAL PLAY BALANCE",
                            color = Color(0xFFA3E9C4),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${"%.2f".format(currentUser?.totalAvailableBalance ?: 0.0)}",
                            color = GoldPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // VIP Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FFD700))
                            .border(1.dp, GoldPrimary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ROYAL VIP",
                                color = GoldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Balance Breakdown
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x55000000))
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BalanceColumn(
                        label = "Deposit Cash",
                        amount = "₹${currentUser?.walletBalance?.toInt() ?: 0}",
                        color = Color.White
                    )
                    Box(modifier = Modifier.width(1.dp).height(22.dp).background(Color(0x33FFFFFF)))
                    BalanceColumn(
                        label = "Withdrawable",
                        amount = "₹${currentUser?.winningBalance?.toInt() ?: 0}",
                        color = Color(0xFF00E676)
                    )
                    Box(modifier = Modifier.width(1.dp).height(22.dp).background(Color(0x33FFFFFF)))
                    BalanceColumn(
                        label = "Free Bonus",
                        amount = "₹${currentUser?.bonusBalance?.toInt() ?: 0}",
                        color = GoldAccent
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons: Add Cash (Deposit) & Withdraw
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDepositClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CasinoGreenLight,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text("Add Cash +", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = onWithdrawClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = GoldLight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text("Withdraw ⇪", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceColumn(label: String, amount: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 9.sp)
        Text(text = amount, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TableSelectionCard(
    table: TableConfig,
    userBalance: Double,
    onJoin: () -> Unit,
    onNeedCash: () -> Unit
) {
    val canAfford = userBalance >= table.bootAmount
    val isVip = table.bootAmount >= 100.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (canAfford) onJoin() else onNeedCash()
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isVip) {
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1E1605), Color(0xFF291E07), Color(0xFF151004))
                        )
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0C1F15), Color(0xFF11291C), Color(0xFF0A1810))
                        )
                    }
                )
                .border(
                    width = if (isVip) 1.5.dp else 1.dp,
                    brush = if (isVip) {
                        Brush.horizontalGradient(listOf(GoldPrimary, GoldLight, GoldDark))
                    } else {
                        Brush.horizontalGradient(listOf(Color(0x4400E676), Color(0x33FFD700)))
                    },
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Table Icon and Details
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (isVip) listOf(GoldDark, Color(0xFF8D6E63))
                                    else listOf(CasinoGreenPrimary, CasinoGreenDark)
                                )
                            )
                            .border(1.dp, if (isVip) GoldLight else Color(0xFF00E676), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Table",
                            tint = if (isVip) GoldLight else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = table.name,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isVip) Color(0x44FFD700) else Color(0x3300E676))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = table.tag,
                                    color = if (isVip) GoldPrimary else Color(0xFF69F0AE),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Boot: ",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "₹${table.bootAmount.toInt()}",
                                color = GoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•  Max Pot: ₹${table.maxPotLimit.toInt()}",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(8.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "5/6 Players Seated  •  Fast Blind",
                                color = Color(0xFFB0BEC5),
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Action Button
                if (canAfford) {
                    Button(
                        onClick = onJoin,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isVip) GoldPrimary else CasinoGreenLight,
                            contentColor = if (isVip) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "PLAY",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Button(
                        onClick = onNeedCash,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF37474F),
                            contentColor = GoldLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Add Cash",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HandRankingsCard(
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Hand Rankings",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Official Teen Patti Hand Rankings",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    val rankings = listOf(
                        "1. Trail / Trio (Three of a Kind: A-A-A, K-K-K) [Highest]",
                        "2. Pure Sequence (Straight Flush: A-K-Q, A-2-3 same suit)",
                        "3. Sequence (Normal Run: A-K-Q, 8-7-6 mixed suits)",
                        "4. Color (Flush: 3 cards of same suit)",
                        "5. Pair (Two cards of same rank: J-J-4)",
                        "6. High Card (Highest card wins) [Lowest]"
                    )
                    rankings.forEach { rule ->
                        Text(
                            text = rule,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
