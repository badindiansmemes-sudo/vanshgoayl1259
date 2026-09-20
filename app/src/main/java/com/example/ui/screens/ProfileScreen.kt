package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.UserEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CasinoGreenDark
import com.example.ui.theme.CasinoGreenLight
import com.example.ui.theme.CasinoGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.TeenPattiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: TeenPattiViewModel,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onNavigateWallet: () -> Unit
) {
    val transactions by viewModel.userTransactions.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showEditDialog by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf(currentUser?.name ?: "") }
    var editPhone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var editEmail by remember { mutableStateOf(currentUser?.email ?: "") }
    var editAvatarId by remember { mutableIntStateOf(currentUser?.avatarId ?: 1) }

    val filteredTransactions = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            "DEPOSITS" -> transactions.filter { it.type == TransactionType.DEPOSIT.name }
            "WITHDRAWALS" -> transactions.filter { it.type == TransactionType.WITHDRAWAL.name }
            "WINNINGS" -> transactions.filter { it.type == TransactionType.GAME_WIN.name }
            "LOSSES" -> transactions.filter { it.type == TransactionType.GAME_LOSS.name }
            else -> transactions
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CasinoGreenDark, Color(0xFF04120A), Color.Black)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = GoldLight
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "My Profile & Passbook",
                            color = GoldLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Log Out",
                            tint = Color(0xFFFF5252)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    // Profile Overview Card
                    ProfileOverviewCard(
                        currentUser = currentUser,
                        onEditClick = {
                            editName = currentUser?.name ?: ""
                            editPhone = currentUser?.phone ?: ""
                            editEmail = currentUser?.email ?: ""
                            editAvatarId = currentUser?.avatarId ?: 1
                            showEditDialog = true
                        }
                    )
                }

                item {
                    // Balance & Bonus Points Breakdown
                    BalancePointsCard(
                        currentUser = currentUser,
                        onAddCashClick = onNavigateWallet,
                        onWithdrawClick = onNavigateWallet
                    )
                }

                item {
                    // Player Career & Stats
                    CareerStatsCard(currentUser = currentUser)
                }

                item {
                    // Transaction History Section Header & Filter Tabs
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Transaction History",
                                color = GoldLight,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${filteredTransactions.size} Records",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 2.dp)
                        ) {
                            val filters = listOf(
                                "ALL" to "All",
                                "DEPOSITS" to "Deposits (+)",
                                "WITHDRAWALS" to "Withdrawals (⇪)",
                                "WINNINGS" to "Winnings (🏆)",
                                "LOSSES" to "Losses (🎲)"
                            )
                            items(filters) { (id, label) ->
                                FilterChip(
                                    selected = selectedFilter == id,
                                    onClick = { selectedFilter = id },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldPrimary,
                                        selectedLabelColor = Color.Black,
                                        containerColor = SurfaceCard,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selectedFilter == id,
                                        borderColor = if (selectedFilter == id) GoldPrimary else Color(0x33FFD700)
                                    )
                                )
                            }
                        }
                    }
                }

                if (filteredTransactions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No transactions found in this category",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(filteredTransactions) { txn ->
                        PassbookTransactionItem(transaction = txn)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Edit Profile Dialog
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                containerColor = SurfaceCard,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "Update Profile Information",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Choose Your Avatar",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            (1..5).forEach { avatarId ->
                                val isSelected = editAvatarId == avatarId
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) GoldPrimary else SurfaceElevated
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Color.White else Color(0x33FFD700),
                                            shape = CircleShape
                                        )
                                        .clickable { editAvatarId = avatarId },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (avatarId) {
                                            1 -> Icons.Default.Person
                                            2 -> Icons.Default.Star
                                            3 -> Icons.Default.EmojiEvents
                                            4 -> Icons.Default.Casino
                                            else -> Icons.Default.Security
                                        },
                                        contentDescription = "Avatar $avatarId",
                                        tint = if (isSelected) Color.Black else GoldLight,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Display Name", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0x33FFD700),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it.trim() },
                            label = { Text("Mobile Number", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0x33FFD700),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it.trim() },
                            label = { Text("Email Address", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0x33FFD700),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editName.isNotBlank() && editPhone.isNotBlank()) {
                                viewModel.updateProfile(editName, editPhone, editEmail, editAvatarId)
                                showEditDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileOverviewCard(
    currentUser: UserEntity?,
    onEditClick: () -> Unit
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
                        listOf(Color(0xFF133621), Color(0xFF1B5E20), Color(0xFF0D2817))
                    )
                )
                .border(1.dp, Color(0x44FFD700), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(GoldDark, GoldPrimary)
                                )
                            )
                            .border(2.dp, GoldLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (currentUser?.avatarId ?: 1) {
                                1 -> Icons.Default.Person
                                2 -> Icons.Default.Star
                                3 -> Icons.Default.EmojiEvents
                                4 -> Icons.Default.Casino
                                else -> Icons.Default.Security
                            },
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.name ?: "Player",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x33FFD700))
                                    .border(1.dp, GoldPrimary, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentUser?.isAdmin == true) "ADMIN" else "VIP PRO",
                                    color = GoldPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "+91 ${currentUser?.phone ?: "9876543210"}",
                            color = Color(0xFFA5D6A7),
                            fontSize = 12.sp
                        )
                        if (!currentUser?.email.isNullOrBlank()) {
                            Text(
                                text = currentUser?.email ?: "",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x33FFFFFF),
                        contentColor = GoldLight
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BalancePointsCard(
    currentUser: UserEntity?,
    onAddCashClick: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x22FFD700), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL PLAY BALANCE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "₹${"%.2f".format(currentUser?.totalAvailableBalance ?: 0.0)}",
                        color = GoldPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAddCashClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CasinoGreenLight,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("+ Deposit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onWithdrawClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = GoldLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Withdraw", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Three Balances Breakdown: Deposit Cash | Withdrawable | Bonus Points
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x55000000))
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Deposit Wallet", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "₹${currentUser?.walletBalance?.toInt() ?: 0}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Game Bets", color = Color(0xFFA5D6A7), fontSize = 9.sp)
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(Color(0x33FFFFFF))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Withdrawable", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "₹${currentUser?.winningBalance?.toInt() ?: 0}",
                        color = Color(0xFF00E676),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Instant Payout", color = Color(0xFFB9F6CA), fontSize = 9.sp)
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(Color(0x33FFFFFF))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bonus Points", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "₹${currentUser?.bonusBalance?.toInt() ?: 0}",
                        color = GoldAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("20% / Hand", color = GoldLight, fontSize = 9.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bonus explanation banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x22FFD700))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Bonus Points are promo chips automatically utilized up to 20% on each hand.",
                    color = GoldLight,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun CareerStatsCard(currentUser: UserEntity?) {
    val gamesPlayed = currentUser?.totalGamesPlayed ?: 0
    val gamesWon = currentUser?.totalGamesWon ?: 0
    val winRate = if (gamesPlayed > 0) ((gamesWon.toDouble() / gamesPlayed) * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Gaming Performance & Stats",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBox(label = "Games Played", value = "$gamesPlayed", color = Color.White)
                StatBox(label = "Games Won", value = "$gamesWon", color = Color(0xFF00E676))
                StatBox(label = "Win Rate", value = "$winRate%", color = GoldPrimary)
                StatBox(
                    label = "Player Tier",
                    value = if (gamesPlayed > 10) "Diamond" else "Silver",
                    color = Color(0xFF64B5F6)
                )
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElevated)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = TextMuted, fontSize = 9.sp)
    }
}

@Composable
fun PassbookTransactionItem(transaction: TransactionEntity) {
    val isCredit = transaction.type == TransactionType.DEPOSIT.name || transaction.type == TransactionType.GAME_WIN.name
    val isLoss = transaction.type == TransactionType.GAME_LOSS.name

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(transaction.timestamp) { dateFormat.format(Date(transaction.timestamp)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (transaction.type) {
                                TransactionType.DEPOSIT.name -> Color(0x3300E676)
                                TransactionType.WITHDRAWAL.name -> Color(0x332196F3)
                                TransactionType.GAME_WIN.name -> Color(0x33FFD700)
                                else -> Color(0x33E53935)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (transaction.type) {
                            TransactionType.DEPOSIT.name -> Icons.Default.ArrowDownward
                            TransactionType.WITHDRAWAL.name -> Icons.Default.ArrowUpward
                            TransactionType.GAME_WIN.name -> Icons.Default.EmojiEvents
                            else -> Icons.Default.Casino
                        },
                        contentDescription = null,
                        tint = when (transaction.type) {
                            TransactionType.DEPOSIT.name -> Color(0xFF00E676)
                            TransactionType.WITHDRAWAL.name -> Color(0xFF64B5F6)
                            TransactionType.GAME_WIN.name -> GoldPrimary
                            else -> Color(0xFFFF5252)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = when (transaction.type) {
                            TransactionType.DEPOSIT.name -> "Deposit Cash"
                            TransactionType.WITHDRAWAL.name -> "Payout Withdrawal"
                            TransactionType.GAME_WIN.name -> "Game Won 🏆"
                            TransactionType.GAME_LOSS.name -> "Game Bet / Loss"
                            else -> transaction.type
                        },
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.accountOrUpiDetail.ifBlank { transaction.paymentMethod },
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                    Text(
                        text = formattedDate,
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}₹${transaction.amount.toInt()}",
                    color = if (isCredit) Color(0xFF00E676) else if (isLoss) Color(0xFFFF5252) else Color(0xFF64B5F6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = transaction.status)
            }
        }
    }
}
