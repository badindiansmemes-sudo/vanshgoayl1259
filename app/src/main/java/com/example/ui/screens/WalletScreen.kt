package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.ui.components.RealTimePaymentGatewayDialog
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CasinoGreenDark
import com.example.ui.theme.CasinoGreenLight
import com.example.ui.theme.CasinoGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
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
fun WalletScreen(
    viewModel: TeenPattiViewModel,
    currentUser: UserEntity?,
    onBack: () -> Unit
) {
    val platformSettings by viewModel.platformSettings.collectAsState()
    val transactions by viewModel.userTransactions.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Deposit Cash", "Withdraw Winnings", "Passbook")

    var showGatewayModal by remember { mutableStateOf(false) }
    var gatewayAmount by remember { mutableStateOf(0.0) }

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
            // Header
            Surface(
                color = SurfaceDark,
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
                    Text(
                        text = "Wallet & Banking Hub",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = SurfaceDark,
                contentColor = GoldPrimary,
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Balance Highlight Card
                item {
                    BalanceOverviewBox(currentUser = currentUser)
                }

                when (selectedTabIndex) {
                    0 -> {
                        // Deposit Tab
                        item {
                            DepositSection(
                                currentUser = currentUser,
                                adminUpiId = platformSettings.adminUpiId,
                                adminUpiName = platformSettings.adminUpiName,
                                onInstantGateway = { amt ->
                                    gatewayAmount = amt
                                    showGatewayModal = true
                                },
                                onSubmitUtr = { amt, utr ->
                                    viewModel.depositCash(
                                        amount = amt,
                                        method = "DIRECT_UPI_UTR",
                                        utr = utr,
                                        isInstantGateway = false
                                    )
                                }
                            )
                        }
                    }
                    1 -> {
                        // Withdraw Tab
                        item {
                            WithdrawSection(
                                winningBalance = currentUser?.winningBalance ?: 0.0,
                                minWithdraw = platformSettings.minWithdraw,
                                onSubmitWithdraw = { amt, method, details ->
                                    viewModel.withdrawWinnings(
                                        amount = amt,
                                        method = method,
                                        destination = details
                                    )
                                }
                            )
                        }
                    }
                    2 -> {
                        // Passbook
                        item {
                            Text(
                                text = "Transaction History",
                                color = GoldLight,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (transactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No transactions yet.", color = TextMuted, fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(transactions) { txn ->
                                TransactionRowItem(transaction = txn)
                            }
                        }
                    }
                }
            }
        }

        // Real-Time Payment Gateway Modal
        if (showGatewayModal) {
            RealTimePaymentGatewayDialog(
                amount = gatewayAmount,
                onPaymentSuccess = { method, utr ->
                    showGatewayModal = false
                    viewModel.depositCash(
                        amount = gatewayAmount,
                        method = method,
                        utr = utr,
                        isInstantGateway = true
                    )
                },
                onDismiss = {
                    showGatewayModal = false
                }
            )
        }
    }
}

@Composable
fun BalanceOverviewBox(currentUser: UserEntity?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0F3820), Color(0xFF1B5E20))
                    )
                )
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "TOTAL WALLET LIQUIDITY",
                color = Color(0xFFA3E9C4),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "₹${"%.2f".format(currentUser?.totalAvailableBalance ?: 0.0)}",
                color = GoldPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x44000000))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Deposit Balance", color = TextMuted, fontSize = 9.sp)
                    Text("₹${currentUser?.walletBalance?.toInt() ?: 0}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Withdrawable Winnings", color = Color(0xFF81C784), fontSize = 9.sp)
                    Text("₹${currentUser?.winningBalance?.toInt() ?: 0}", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bonus Chips", color = GoldAccent, fontSize = 9.sp)
                    Text("₹${currentUser?.bonusBalance?.toInt() ?: 0}", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DepositSection(
    currentUser: UserEntity?,
    adminUpiId: String,
    adminUpiName: String,
    onInstantGateway: (Double) -> Unit,
    onSubmitUtr: (Double, String) -> Unit
) {
    var amountInput by remember { mutableStateOf("500") }
    var utrInput by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("GATEWAY") } // "GATEWAY" or "DIRECT_UPI"

    val presets = listOf(
        Pair(100.0, "₹100"),
        Pair(500.0, "₹500 (+₹50 Bonus)"),
        Pair(1000.0, "₹1000 (+₹150 Bonus)"),
        Pair(2500.0, "₹2500 (+₹400 Bonus)"),
        Pair(5000.0, "₹5000 (+₹1000 Bonus)")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Select Quick Add Amount",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Presets
            presets.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { (amt, label) ->
                        val isSelected = amountInput == amt.toInt().toString()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0x33FFD700) else Color(0x22FFFFFF))
                                .border(1.dp, if (isSelected) GoldPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { amountInput = amt.toInt().toString() }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) GoldPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Custom Amount input
            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it.filter { char -> char.isDigit() } },
                label = { Text("Deposit Amount (₹)", color = TextSecondary) },
                prefix = { Text("₹ ", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0x33FFD700),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Choose Payment Gateway",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 1: Instant Gateway
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedMethod == "GATEWAY") Color(0x2200E676) else Color(0x11FFFFFF))
                    .border(1.dp, if (selectedMethod == "GATEWAY") Color(0xFF00E676) else Color.Transparent, RoundedCornerShape(10.dp))
                    .clickable { selectedMethod = "GATEWAY" }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Instant Payment Gateway", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF00E676))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("FASTEST", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Text("UPI (GPay, PhonePe, Paytm), NetBanking & Cards", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option 2: Direct Admin UPI & QR Transfer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedMethod == "DIRECT_UPI") Color(0x33FFD700) else Color(0x11FFFFFF))
                    .border(1.dp, if (selectedMethod == "DIRECT_UPI") GoldPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                    .clickable { selectedMethod = "DIRECT_UPI" }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Direct Admin UPI Transfer & QR", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Send directly to Admin UPI & enter 12-digit UTR", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedMethod == "GATEWAY") {
                Button(
                    onClick = {
                        val amt = amountInput.toDoubleOrNull() ?: 0.0
                        if (amt > 0) onInstantGateway(amt)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CasinoGreenLight,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pay ₹${amountInput.ifBlank { "0" }} Securely",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                // Direct UPI details box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33000000))
                        .padding(12.dp)
                ) {
                    Text("Admin Payment Details:", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("UPI ID: $adminUpiId", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Beneficiary: $adminUpiName", color = TextSecondary, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = utrInput,
                        onValueChange = { utrInput = it.trim() },
                        label = { Text("12-Digit UTR / Reference Number", color = TextSecondary) },
                        placeholder = { Text("e.g. 428192837465", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0x33FFD700),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val amt = amountInput.toDoubleOrNull() ?: 0.0
                            if (amt > 0 && utrInput.isNotBlank()) {
                                onSubmitUtr(amt, utrInput)
                                utrInput = ""
                            }
                        },
                        enabled = utrInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Submit UTR for Admin Verification", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WithdrawSection(
    winningBalance: Double,
    minWithdraw: Double,
    onSubmitWithdraw: (amount: Double, method: String, details: String) -> Unit
) {
    var withdrawAmount by remember { mutableStateOf("") }
    var payoutMethod by remember { mutableStateOf("UPI") } // "UPI" or "BANK"
    var upiIdInput by remember { mutableStateOf("") }
    var bankAccountInput by remember { mutableStateOf("") }
    var bankIfscInput by remember { mutableStateOf("") }
    var bankHolderNameInput by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Instant Withdrawal to Bank / UPI",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Eligible Winning Balance: ₹${winningBalance.toInt()} (Min: ₹${minWithdraw.toInt()})",
                color = Color(0xFF00E676),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = withdrawAmount,
                onValueChange = { withdrawAmount = it.filter { c -> c.isDigit() } },
                label = { Text("Enter Withdrawal Amount", color = TextSecondary) },
                prefix = { Text("₹ ", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color(0x33FFD700),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Method Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (payoutMethod == "UPI") Color(0x33FFD700) else Color(0x22FFFFFF))
                        .border(1.dp, if (payoutMethod == "UPI") GoldPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { payoutMethod = "UPI" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("UPI ID (GPay/Paytm)", color = if (payoutMethod == "UPI") GoldPrimary else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (payoutMethod == "BANK") Color(0x33FFD700) else Color(0x22FFFFFF))
                        .border(1.dp, if (payoutMethod == "BANK") GoldPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { payoutMethod = "BANK" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Bank Account", color = if (payoutMethod == "BANK") GoldPrimary else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (payoutMethod == "UPI") {
                OutlinedTextField(
                    value = upiIdInput,
                    onValueChange = { upiIdInput = it.trim() },
                    label = { Text("Your UPI ID", color = TextSecondary) },
                    placeholder = { Text("e.g. mobile@paytm or user@okaxis", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0x33FFD700),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            } else {
                OutlinedTextField(
                    value = bankHolderNameInput,
                    onValueChange = { bankHolderNameInput = it },
                    label = { Text("Account Holder Name", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0x33FFD700),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = bankAccountInput,
                    onValueChange = { bankAccountInput = it.trim() },
                    label = { Text("Bank Account Number", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0x33FFD700),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = bankIfscInput,
                    onValueChange = { bankIfscInput = it.trim().uppercase() },
                    label = { Text("IFSC Code", color = TextSecondary) },
                    placeholder = { Text("e.g. SBIN0001234", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color(0x33FFD700),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amt = withdrawAmount.toDoubleOrNull() ?: 0.0
                    val details = if (payoutMethod == "UPI") upiIdInput
                    else "$bankHolderNameInput | A/C: $bankAccountInput | IFSC: $bankIfscInput"
                    onSubmitWithdraw(amt, payoutMethod, details)
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
                Text(
                    text = "Request Withdrawal ⇪",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun TransactionRowItem(transaction: TransactionEntity) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        .format(Date(transaction.timestamp))

    val isCredit = transaction.type in listOf("DEPOSIT", "GAME_WIN")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
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
                        text = transaction.type.replace("_", " "),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(status = transaction.status)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${transaction.paymentMethod} • Ref: ${transaction.referenceNumber.take(16)}",
                    color = TextSecondary,
                    fontSize = 10.sp
                )

                Text(
                    text = dateStr,
                    color = TextMuted,
                    fontSize = 9.sp
                )
            }

            Text(
                text = "${if (isCredit) "+" else "-"}₹${transaction.amount.toInt()}",
                color = if (isCredit) StatusSuccess else Color(0xFFFF5252),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
