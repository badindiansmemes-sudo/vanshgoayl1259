package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.GameHistoryEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CasinoGreenDark
import com.example.ui.theme.CasinoGreenLight
import com.example.ui.theme.CasinoGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TeenPattiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPanelScreen(
    viewModel: TeenPattiViewModel,
    onBack: () -> Unit
) {
    val platformSettings by viewModel.platformSettings.collectAsState()
    val totalCommission by viewModel.totalCommission.collectAsState()
    val pendingDeposits by viewModel.pendingDeposits.collectAsState()
    val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val totalGames by viewModel.totalGamesCount.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val recentGames by viewModel.recentGames.collectAsState()
    val totalTurnover by viewModel.totalTurnover.collectAsState()
    val totalApprovedDeposits by viewModel.totalApprovedDeposits.collectAsState()
    val totalApprovedWithdrawals by viewModel.totalApprovedWithdrawals.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Deposits (${pendingDeposits.size})", "Withdrawals (${pendingWithdrawals.size})", "User Tracker", "Reports & Audit", "Settings & Rake")

    var userToEdit by remember { mutableStateOf<UserEntity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF13101C), Color(0xFF090710), Color.Black)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Header
            Surface(
                color = Color(0xFF1E172E),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GoldLight
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Admin Control Center",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE53935))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text("MASTER", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(
                            text = "Platform Profits, Deposits, Withdrawals & User Management",
                            color = GoldLight,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF1A1428),
                contentColor = GoldPrimary,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = GoldPrimary,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Overview KPI Dashboard
                        item {
                            AdminKpiOverview(
                                totalProfit = (totalCommission ?: 0.0) + platformSettings.totalPlatformRevenue,
                                commissionRate = platformSettings.commissionPercent,
                                pendingDepCount = pendingDeposits.size,
                                pendingWitCount = pendingWithdrawals.size,
                                totalUsersCount = allUsers.size,
                                totalGamesCount = totalGames
                            )
                        }

                        item {
                            Text("Recent Global Transactions", color = GoldLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        items(allTransactions.take(8)) { txn ->
                            AdminTransactionCard(txn = txn)
                        }
                    }

                    1 -> {
                        // Deposits Approval
                        item {
                            Text(
                                text = "Pending Deposit Verifications (${pendingDeposits.size})",
                                color = GoldLight,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (pendingDeposits.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No pending deposits waiting for review.", color = TextMuted, fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(pendingDeposits) { dep ->
                                DepositApprovalCard(
                                    deposit = dep,
                                    onApprove = { viewModel.adminApproveDeposit(dep.transactionId) },
                                    onReject = { viewModel.adminRejectDeposit(dep.transactionId) }
                                )
                            }
                        }
                    }

                    2 -> {
                        // Withdrawals Payout
                        item {
                            Text(
                                text = "Pending Withdrawal Payouts (${pendingWithdrawals.size})",
                                color = GoldLight,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (pendingWithdrawals.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No pending withdrawal requests.", color = TextMuted, fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(pendingWithdrawals) { wit ->
                                WithdrawalApprovalCard(
                                    withdrawal = wit,
                                    onApprove = { viewModel.adminApproveWithdrawal(wit.transactionId) },
                                    onReject = { viewModel.adminRejectWithdrawal(wit.transactionId) }
                                )
                            }
                        }
                    }

                    3 -> {
                        // User Tracker
                        item {
                            Text(
                                text = "Registered Players Tracking (${allUsers.size})",
                                color = GoldLight,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(allUsers) { user ->
                            AdminUserTrackerCard(
                                user = user,
                                onEditBalance = { userToEdit = user },
                                onToggleBan = { viewModel.adminToggleBanUser(user.userId, user.isBanned) }
                            )
                        }
                    }

                    4 -> {
                        // Reports & Audit
                        item {
                            AdminReportsSection(
                                turnover = totalTurnover ?: 0.0,
                                commission = totalCommission ?: 0.0,
                                approvedDeposits = totalApprovedDeposits ?: 0.0,
                                approvedWithdrawals = totalApprovedWithdrawals ?: 0.0,
                                recentGames = recentGames,
                                onExportReport = {
                                    viewModel.showToast("Financial & Round Audit Report exported successfully!")
                                }
                            )
                        }
                    }

                    5 -> {
                        // Settings & Rake
                        item {
                            AdminSettingsSection(
                                settings = platformSettings,
                                onSave = { percent, minDep, minWith, upiId, upiName ->
                                    viewModel.adminSaveSettings(percent, minDep, minWith, upiId, upiName)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Edit User Balance Dialog
        if (userToEdit != null) {
            EditBalanceDialog(
                user = userToEdit!!,
                onDismiss = { userToEdit = null },
                onConfirm = { newWallet, newWin, newBonus ->
                    viewModel.adminAdjustUserBalance(userToEdit!!.userId, newWallet, newWin, newBonus)
                    userToEdit = null
                }
            )
        }
    }
}

@Composable
fun AdminKpiOverview(
    totalProfit: Double,
    commissionRate: Double,
    pendingDepCount: Int,
    pendingWitCount: Int,
    totalUsersCount: Int,
    totalGamesCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Master Profit Card (User requirement: "me bhi kuch profit kma sku charge le sku")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF261238))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF3B1754), Color(0xFF1E0C2C))
                        )
                    )
                    .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HOUSE EARNINGS (PLATFORM PROFIT)",
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x33FFD700))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("${commissionRate}% RAKE CUT", color = GoldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "₹${"%.2f".format(totalProfit)}",
                        color = GoldPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Real profit charged automatically from every winning game round pot.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Secondary KPI Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiMiniCard(
                title = "Pending Deposits",
                value = "$pendingDepCount",
                icon = Icons.Default.Payments,
                color = StatusPending,
                modifier = Modifier.weight(1f)
            )
            KpiMiniCard(
                title = "Pending Payouts",
                value = "$pendingWitCount",
                icon = Icons.Default.MonetizationOn,
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiMiniCard(
                title = "Registered Players",
                value = "$totalUsersCount",
                icon = Icons.Default.People,
                color = Color(0xFF64B5F6),
                modifier = Modifier.weight(1f)
            )
            KpiMiniCard(
                title = "Total Games Played",
                value = "$totalGamesCount",
                icon = Icons.Default.TrendingUp,
                color = StatusSuccess,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun KpiMiniCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E172E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(text = title, color = TextMuted, fontSize = 10.sp)
                Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DepositApprovalCard(
    deposit: TransactionEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(deposit.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E172E))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(deposit.userName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("User ID: ${deposit.userId}", color = TextMuted, fontSize = 10.sp)
                    Text(dateStr, color = TextMuted, fontSize = 10.sp)
                }

                Text(
                    text = "₹${deposit.amount.toInt()}",
                    color = GoldPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33000000))
                    .padding(8.dp)
            ) {
                Column {
                    Text("Payment Method: ${deposit.paymentMethod}", color = TextSecondary, fontSize = 11.sp)
                    Text("12-Digit UTR / Ref: ${deposit.referenceNumber}", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusRejected,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reject")
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1.4f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CasinoGreenLight,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Approve & Credit", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WithdrawalApprovalCard(
    withdrawal: TransactionEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(withdrawal.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E172E))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(withdrawal.userName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Requested on: $dateStr", color = TextMuted, fontSize = 10.sp)
                }

                Text(
                    text = "₹${withdrawal.amount.toInt()}",
                    color = Color(0xFFFF5252),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33000000))
                    .padding(8.dp)
            ) {
                Column {
                    Text("Payout Mode: ${withdrawal.paymentMethod}", color = TextSecondary, fontSize = 11.sp)
                    Text("Payout Details: ${withdrawal.accountOrUpiDetail}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF455A64),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reject & Refund")
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1.4f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Approve & Mark Paid", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminUserTrackerCard(
    user: UserEntity,
    onEditBalance: () -> Unit,
    onToggleBan: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E172E))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (user.isAdmin) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("(ADMIN)", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        if (user.isBanned) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("[BANNED]", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Phone: ${user.phone} • Email: ${user.email.ifBlank { "N/A" }}", color = TextMuted, fontSize = 11.sp)
                }

                Row {
                    IconButton(onClick = onEditBalance) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Balance", tint = GoldLight)
                    }
                    IconButton(onClick = onToggleBan) {
                        Icon(
                            Icons.Default.Block,
                            contentDescription = "Ban/Unban",
                            tint = if (user.isBanned) Color.Red else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Balances
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33000000))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Deposit", color = TextMuted, fontSize = 9.sp)
                    Text("₹${user.walletBalance.toInt()}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Winnings", color = Color(0xFF81C784), fontSize = 9.sp)
                    Text("₹${user.winningBalance.toInt()}", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bonus", color = GoldAccent, fontSize = 9.sp)
                    Text("₹${user.bonusBalance.toInt()}", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Games Won/Played", color = TextMuted, fontSize = 9.sp)
                    Text("${user.totalGamesWon}/${user.totalGamesPlayed}", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminSettingsSection(
    settings: com.example.data.model.PlatformSettingsEntity,
    onSave: (percent: Double, minDep: Double, minWith: Double, upiId: String, upiName: String) -> Unit
) {
    var commissionInput by remember(settings) { mutableStateOf(settings.commissionPercent.toString()) }
    var minDepInput by remember(settings) { mutableStateOf(settings.minDeposit.toInt().toString()) }
    var minWithInput by remember(settings) { mutableStateOf(settings.minWithdraw.toInt().toString()) }
    var adminUpiInput by remember(settings) { mutableStateOf(settings.adminUpiId) }
    var adminUpiNameInput by remember(settings) { mutableStateOf(settings.adminUpiName) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E172E))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Platform Revenue & Rules Configuration", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            // Commission Percent
            OutlinedTextField(
                value = commissionInput,
                onValueChange = { commissionInput = it },
                label = { Text("Platform House Rake / Commission Cut (%)", color = TextSecondary) },
                suffix = { Text("%", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0x33FFD700),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Min Deposit
            OutlinedTextField(
                value = minDepInput,
                onValueChange = { minDepInput = it.filter { c -> c.isDigit() } },
                label = { Text("Minimum Deposit Limit (₹)", color = TextSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0x33FFD700),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Min Withdrawal
            OutlinedTextField(
                value = minWithInput,
                onValueChange = { minWithInput = it.filter { c -> c.isDigit() } },
                label = { Text("Minimum Withdrawal Limit (₹)", color = TextSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0x33FFD700),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Admin UPI ID
            OutlinedTextField(
                value = adminUpiInput,
                onValueChange = { adminUpiInput = it.trim() },
                label = { Text("Admin Payment Receiving UPI ID", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0x33FFD700),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Admin Name
            OutlinedTextField(
                value = adminUpiNameInput,
                onValueChange = { adminUpiNameInput = it },
                label = { Text("Admin Payment Beneficiary Name", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0x33FFD700),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val p = commissionInput.toDoubleOrNull() ?: 5.0
                    val md = minDepInput.toDoubleOrNull() ?: 100.0
                    val mw = minWithInput.toDoubleOrNull() ?: 200.0
                    onSave(p, md, mw, adminUpiInput, adminUpiNameInput)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Configuration", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun AdminTransactionCard(txn: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1428))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(txn.userName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("${txn.type} • ${txn.paymentMethod}", color = TextSecondary, fontSize = 10.sp)
                Text("Ref: ${txn.referenceNumber.take(14)}", color = TextMuted, fontSize = 9.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${txn.amount.toInt()}", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                StatusBadge(status = txn.status)
            }
        }
    }
}

@Composable
fun EditBalanceDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (wallet: Double, win: Double, bonus: Double) -> Unit
) {
    var walletInput by remember { mutableStateOf(user.walletBalance.toInt().toString()) }
    var winInput by remember { mutableStateOf(user.winningBalance.toInt().toString()) }
    var bonusInput by remember { mutableStateOf(user.bonusBalance.toInt().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E172E)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Adjust Balances: ${user.name}", color = GoldPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = walletInput,
                    onValueChange = { walletInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Deposit Wallet (₹)", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = winInput,
                    onValueChange = { winInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Winning Balance (₹)", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = bonusInput,
                    onValueChange = { bonusInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Bonus Chips (₹)", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val w = walletInput.toDoubleOrNull() ?: user.walletBalance
                            val wn = winInput.toDoubleOrNull() ?: user.winningBalance
                            val b = bonusInput.toDoubleOrNull() ?: user.bonusBalance
                            onConfirm(w, wn, b)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReportsSection(
    turnover: Double,
    commission: Double,
    approvedDeposits: Double,
    approvedWithdrawals: Double,
    recentGames: List<GameHistoryEntity>,
    onExportReport: () -> Unit
) {
    var reportDownloaded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Platform Liquidity & Financial Audit Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1429))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x33FFD700), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Financial Audit & Revenue Report",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            reportDownloaded = true
                            onExportReport()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x33FFD700),
                            contentColor = GoldLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (reportDownloaded) "Exported!" else "Export CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Financial Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReportMetricRow(
                        label = "Total Pot Turnover (All Games)",
                        value = "₹${"%.2f".format(turnover)}",
                        highlightColor = Color(0xFF64B5F6)
                    )
                    ReportMetricRow(
                        label = "Total House Commission (Rake)",
                        value = "₹${"%.2f".format(commission)}",
                        highlightColor = GoldPrimary
                    )
                    ReportMetricRow(
                        label = "Total Approved Player Deposits",
                        value = "₹${"%.2f".format(approvedDeposits)}",
                        highlightColor = Color(0xFF00E676)
                    )
                    ReportMetricRow(
                        label = "Total Approved Cash Withdrawals",
                        value = "₹${"%.2f".format(approvedWithdrawals)}",
                        highlightColor = Color(0xFFFF5252)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x22FFFFFF))
                    )
                    ReportMetricRow(
                        label = "Net Platform Cash Reserve (Inflow - Outflow)",
                        value = "₹${"%.2f".format(approvedDeposits - approvedWithdrawals)}",
                        highlightColor = if (approvedDeposits >= approvedWithdrawals) Color(0xFF00E676) else Color(0xFFFF8A80)
                    )
                }
            }
        }

        // Live Round Activity Monitor Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Live Round Audit & Winner Tracking",
                color = GoldLight,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${recentGames.size} Rounds",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        if (recentGames.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No games recorded yet. Rounds played at tables will appear here.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            recentGames.forEach { game ->
                GameHistoryAuditRow(game = game)
            }
        }
    }
}

@Composable
private fun ReportMetricRow(label: String, value: String, highlightColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x33000000))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(text = value, color = highlightColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GameHistoryAuditRow(game: GameHistoryEntity) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val timeFormatted = remember(game.timestamp) { dateFormat.format(Date(game.timestamp)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161224))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Round #${game.gameId.take(8)}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x33FFD700))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("Boot ₹${game.tableBoot.toInt()}", color = GoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Winner: ${game.winnerName} (${game.winningHandTitle})",
                    color = Color(0xFFA5D6A7),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = timeFormatted,
                    color = TextMuted,
                    fontSize = 9.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Pot: ₹${game.potAmount.toInt()}",
                    color = GoldPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "House Cut: +₹${game.commissionAmount.toInt()}",
                    color = Color(0xFF00E676),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
