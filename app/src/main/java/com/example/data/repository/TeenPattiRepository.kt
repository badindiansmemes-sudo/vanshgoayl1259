package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.GameHistoryEntity
import com.example.data.model.PlatformSettingsEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class TeenPattiRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val transactionDao = db.transactionDao()
    private val gameHistoryDao = db.gameHistoryDao()
    private val platformSettingsDao = db.platformSettingsDao()

    suspend fun seedInitialDataIfNeeded() {
        if (userDao.getUserCount() == 0) {
            // Seed Admin
            val admin = UserEntity(
                userId = "ADMIN_001",
                name = "House Master Admin",
                phone = "9999999999",
                email = "admin@teenpatti.com",
                password = "admin",
                walletBalance = 50000.0,
                winningBalance = 25000.0,
                bonusBalance = 1000.0,
                isAdmin = true,
                avatarId = 1
            )
            userDao.insertUser(admin)

            // Seed Demo Player
            val demoPlayer = UserEntity(
                userId = "USER_101",
                name = "Vansh (Player)",
                phone = "9876543210",
                email = "vansh@teenpatti.com",
                password = "123",
                walletBalance = 1200.0,
                winningBalance = 650.0,
                bonusBalance = 150.0,
                isAdmin = false,
                avatarId = 2
            )
            userDao.insertUser(demoPlayer)

            // Seed Platform Settings
            val initialSettings = PlatformSettingsEntity(
                id = 1,
                commissionPercent = 5.0, // 5% house cut
                minDeposit = 100.0,
                minWithdraw = 200.0,
                totalPlatformRevenue = 1450.0, // initial platform earnings
                adminUpiId = "royalteenpatti@icici",
                adminUpiName = "Teen Patti Royal House",
                supportPhone = "+91 98765 43210"
            )
            platformSettingsDao.insertOrUpdate(initialSettings)

            // Seed sample past transactions
            transactionDao.insertTransaction(
                TransactionEntity(
                    transactionId = "TXN_DEP_1001",
                    userId = "USER_101",
                    userName = "Vansh (Player)",
                    type = TransactionType.DEPOSIT.name,
                    amount = 1000.0,
                    status = TransactionStatus.APPROVED.name,
                    paymentMethod = "UPI_PHONEPE",
                    referenceNumber = "UTR938472918237",
                    accountOrUpiDetail = "vansh@ybl",
                    adminNote = "Verified & Credited instantly",
                    timestamp = System.currentTimeMillis() - 86400000L
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    transactionId = "TXN_DEP_1002",
                    userId = "USER_101",
                    userName = "Vansh (Player)",
                    type = TransactionType.DEPOSIT.name,
                    amount = 500.0,
                    status = TransactionStatus.PENDING.name,
                    paymentMethod = "DIRECT_UPI_QR",
                    referenceNumber = "UTR448192837465",
                    accountOrUpiDetail = "paytm-qr-deposit",
                    adminNote = "Pending admin verification",
                    timestamp = System.currentTimeMillis() - 3600000L
                )
            )

            transactionDao.insertTransaction(
                TransactionEntity(
                    transactionId = "TXN_WIT_2001",
                    userId = "USER_101",
                    userName = "Vansh (Player)",
                    type = TransactionType.WITHDRAWAL.name,
                    amount = 300.0,
                    status = TransactionStatus.PENDING.name,
                    paymentMethod = "UPI_GPAY",
                    referenceNumber = "REQ-WITHDRAW-8842",
                    accountOrUpiDetail = "vansh@okaxis",
                    adminNote = "Awaiting admin payout",
                    timestamp = System.currentTimeMillis() - 1800000L
                )
            )
        }
    }

    // User Operations
    fun getUserFlow(userId: String): Flow<UserEntity?> = userDao.getUserByIdFlow(userId)
    suspend fun getUser(userId: String): UserEntity? = userDao.getUserById(userId)
    suspend fun getUserByCredentials(login: String): UserEntity? = userDao.getUserByPhoneOrEmail(login)
    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun registerUser(name: String, phone: String, email: String, pass: String): UserEntity {
        val newId = "USR_" + UUID.randomUUID().toString().take(8).uppercase()
        val user = UserEntity(
            userId = newId,
            name = name.ifBlank { "Player $newId" },
            phone = phone,
            email = email,
            password = pass,
            walletBalance = 500.0, // ₹500 welcome bonus chips
            winningBalance = 0.0,
            bonusBalance = 100.0,
            isAdmin = false
        )
        userDao.insertUser(user)

        // Record welcome bonus transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                transactionId = "TXN_BONUS_" + UUID.randomUUID().toString().take(6).uppercase(),
                userId = newId,
                userName = user.name,
                type = TransactionType.DEPOSIT.name,
                amount = 500.0,
                status = TransactionStatus.APPROVED.name,
                paymentMethod = "WELCOME_BONUS",
                referenceNumber = "BONUS_SIGNUP",
                accountOrUpiDetail = "System Welcome Gift"
            )
        )
        return user
    }

    suspend fun updateUserBalances(userId: String, wallet: Double, winning: Double, bonus: Double) {
        userDao.updateBalances(userId, wallet, winning, bonus)
    }

    suspend fun updateProfile(userId: String, name: String, phone: String, email: String, avatarId: Int) {
        userDao.updateProfile(userId, name, phone, email, avatarId)
    }

    suspend fun resetPassword(login: String, newPass: String): Boolean {
        val count = userDao.updatePassword(login.trim(), newPass.trim())
        return count > 0
    }

    suspend fun setUserBanned(userId: String, banned: Boolean) {
        userDao.setBannedStatus(userId, banned)
    }

    suspend fun getOrCreateFirebaseUser(
        uid: String,
        email: String,
        displayName: String? = null
    ): UserEntity {
        val existing = userDao.getUserById(uid) ?: userDao.getUserByPhoneOrEmail(email)
        if (existing != null) {
            return existing
        }
        val newUser = UserEntity(
            userId = uid,
            name = displayName?.ifBlank { null } ?: email.substringBefore('@').replaceFirstChar { it.uppercase() },
            phone = "",
            email = email,
            password = "",
            walletBalance = 1000.0, // ₹1,000 welcome chips for Firebase sign-up
            winningBalance = 250.0,
            bonusBalance = 250.0,
            isAdmin = false,
            avatarId = (1..6).random()
        )
        userDao.insertUser(newUser)
        transactionDao.insertTransaction(
            TransactionEntity(
                transactionId = "TXN_FIREBASE_" + UUID.randomUUID().toString().take(6).uppercase(),
                userId = uid,
                userName = newUser.name,
                type = TransactionType.DEPOSIT.name,
                amount = 1000.0,
                status = TransactionStatus.APPROVED.name,
                paymentMethod = "FIREBASE_AUTH_BONUS",
                referenceNumber = "FIREBASE_WELCOME",
                accountOrUpiDetail = "Firebase Verified Account Welcome Chips"
            )
        )
        return newUser
    }

    suspend fun claimDailyBonus(userId: String, bonusAmount: Double = 500.0): Double {
        val user = userDao.getUserById(userId) ?: return 0.0
        val updatedBonus = user.bonusBalance + bonusAmount
        userDao.updateBalances(
            userId = user.userId,
            wallet = user.walletBalance,
            winning = user.winningBalance,
            bonus = updatedBonus
        )
        val txn = TransactionEntity(
            transactionId = "TXN_BONUS_" + UUID.randomUUID().toString().take(8).uppercase(),
            userId = userId,
            userName = user.name,
            type = TransactionType.DEPOSIT.name,
            amount = bonusAmount,
            status = TransactionStatus.APPROVED.name,
            paymentMethod = "DAILY_BONUS_WHEEL",
            referenceNumber = "BONUS_" + System.currentTimeMillis().toString().takeLast(6),
            accountOrUpiDetail = "VIP Daily Free Chips Reward",
            adminNote = "Daily Login Reward Credited"
        )
        transactionDao.insertTransaction(txn)
        return bonusAmount
    }

    suspend fun addFreePlayChips(userId: String, amount: Double = 1000.0) {
        val user = userDao.getUserById(userId) ?: return
        userDao.updateBalances(
            userId = user.userId,
            wallet = user.walletBalance + amount,
            winning = user.winningBalance,
            bonus = user.bonusBalance
        )
    }

    suspend fun getOrCreateGuestUser(): UserEntity {
        val existing = userDao.getUserById("USER_101")
        if (existing != null) return existing
        val guest = UserEntity(
            userId = "USER_101",
            name = "Player 1",
            phone = "9876543210",
            email = "player@teenpatti.com",
            password = "123",
            walletBalance = 5000.0,
            winningBalance = 1500.0,
            bonusBalance = 500.0,
            isAdmin = false,
            avatarId = 1
        )
        userDao.insertUser(guest)
        return guest
    }

    suspend fun getUserById(userId: String): UserEntity? = userDao.getUserById(userId)

    // Transaction Operations
    fun getTransactionsForUser(userId: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsForUser(userId)

    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    fun getPendingDeposits(): Flow<List<TransactionEntity>> = transactionDao.getPendingDeposits()
    fun getPendingWithdrawals(): Flow<List<TransactionEntity>> = transactionDao.getPendingWithdrawals()
    fun getTotalApprovedDeposits(): Flow<Double?> = transactionDao.getTotalApprovedDepositsFlow()
    fun getTotalApprovedWithdrawals(): Flow<Double?> = transactionDao.getTotalApprovedWithdrawalsFlow()

    suspend fun submitDeposit(
        userId: String,
        userName: String,
        amount: Double,
        method: String,
        utrNumber: String,
        instantApprove: Boolean = false
    ): TransactionEntity {
        val txnId = "TXN_DEP_" + UUID.randomUUID().toString().take(8).uppercase()
        val status = if (instantApprove) TransactionStatus.APPROVED.name else TransactionStatus.PENDING.name
        val txn = TransactionEntity(
            transactionId = txnId,
            userId = userId,
            userName = userName,
            type = TransactionType.DEPOSIT.name,
            amount = amount,
            status = status,
            paymentMethod = method,
            referenceNumber = utrNumber.ifBlank { "UTR" + System.currentTimeMillis() },
            accountOrUpiDetail = "Deposit to Platform",
            adminNote = if (instantApprove) "Instant Gateway Verified" else "Under Review by Admin"
        )
        transactionDao.insertTransaction(txn)

        if (instantApprove) {
            val user = userDao.getUserById(userId)
            if (user != null) {
                userDao.updateBalances(
                    userId = userId,
                    wallet = user.walletBalance + amount,
                    winning = user.winningBalance,
                    bonus = user.bonusBalance
                )
            }
        }
        return txn
    }

    suspend fun approveDeposit(transactionId: String, note: String = "Approved by Admin") {
        val txn = transactionDao.getTransactionById(transactionId) ?: return
        if (txn.status != TransactionStatus.APPROVED.name) {
            transactionDao.updateTransactionStatus(transactionId, TransactionStatus.APPROVED.name, note)
            val user = userDao.getUserById(txn.userId)
            if (user != null) {
                userDao.updateBalances(
                    userId = user.userId,
                    wallet = user.walletBalance + txn.amount,
                    winning = user.winningBalance,
                    bonus = user.bonusBalance
                )
            }
        }
    }

    suspend fun rejectDeposit(transactionId: String, reason: String = "Invalid UTR / Payment Not Received") {
        transactionDao.updateTransactionStatus(transactionId, TransactionStatus.REJECTED.name, reason)
    }

    suspend fun submitWithdrawal(
        userId: String,
        userName: String,
        amount: Double,
        payoutMethod: String,
        accountDetail: String
    ): Result<TransactionEntity> {
        val user = userDao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
        if (user.winningBalance < amount) {
            return Result.failure(Exception("Insufficient winning balance! (Available: ₹${user.winningBalance})"))
        }

        // Deduct from winning balance immediately into pending escrow
        userDao.updateBalances(
            userId = userId,
            wallet = user.walletBalance,
            winning = user.winningBalance - amount,
            bonus = user.bonusBalance
        )

        val txnId = "TXN_WIT_" + UUID.randomUUID().toString().take(8).uppercase()
        val txn = TransactionEntity(
            transactionId = txnId,
            userId = userId,
            userName = userName,
            type = TransactionType.WITHDRAWAL.name,
            amount = amount,
            status = TransactionStatus.PENDING.name,
            paymentMethod = payoutMethod,
            referenceNumber = "REQ-" + UUID.randomUUID().toString().take(6).uppercase(),
            accountOrUpiDetail = accountDetail,
            adminNote = "Pending Admin Transfer"
        )
        transactionDao.insertTransaction(txn)
        return Result.success(txn)
    }

    suspend fun approveWithdrawal(transactionId: String, payoutRef: String = "IMPS/UPI Completed") {
        transactionDao.updateTransactionStatus(transactionId, TransactionStatus.APPROVED.name, payoutRef)
    }

    suspend fun rejectWithdrawal(transactionId: String, reason: String = "Rejected by Admin - Refunded") {
        val txn = transactionDao.getTransactionById(transactionId) ?: return
        if (txn.status == TransactionStatus.PENDING.name) {
            transactionDao.updateTransactionStatus(transactionId, TransactionStatus.REJECTED.name, reason)
            // Refund to user's winning balance
            val user = userDao.getUserById(txn.userId)
            if (user != null) {
                userDao.updateBalances(
                    userId = user.userId,
                    wallet = user.walletBalance,
                    winning = user.winningBalance + txn.amount,
                    bonus = user.bonusBalance
                )
            }
        }
    }

    // Game Table settlement & House Commission (Rake Cut)
    suspend fun recordGameRound(
        bootAmount: Double,
        potAmount: Double,
        winnerId: String,
        winnerName: String,
        winningHandTitle: String,
        isUserWinner: Boolean,
        userId: String
    ): Double {
        val settings = platformSettingsDao.getSettings() ?: PlatformSettingsEntity()
        val commissionRate = settings.commissionPercent
        val commissionAmount = (potAmount * commissionRate) / 100.0
        val netPrize = potAmount - commissionAmount

        // 1. Credit Admin Revenue Pool
        platformSettingsDao.addCommissionRevenue(commissionAmount)

        // 2. Record Game History
        val gameRecord = GameHistoryEntity(
            gameId = "GAME_" + UUID.randomUUID().toString().take(8).uppercase(),
            tableBoot = bootAmount,
            potAmount = potAmount,
            winnerId = winnerId,
            winnerName = winnerName,
            winningHandTitle = winningHandTitle,
            commissionRate = commissionRate,
            commissionAmount = commissionAmount,
            netPrize = netPrize
        )
        gameHistoryDao.insertGame(gameRecord)

        // 3. Update User stats & balance if current user won
        if (isUserWinner) {
            val user = userDao.getUserById(userId)
            if (user != null) {
                userDao.updateBalances(
                    userId = userId,
                    wallet = user.walletBalance,
                    winning = user.winningBalance + netPrize,
                    bonus = user.bonusBalance
                )
                userDao.incrementGameStats(userId, isWon = 1)

                // Add Winning Transaction
                transactionDao.insertTransaction(
                    TransactionEntity(
                        transactionId = "TXN_WIN_" + UUID.randomUUID().toString().take(8).uppercase(),
                        userId = userId,
                        userName = winnerName,
                        type = TransactionType.GAME_WIN.name,
                        amount = netPrize,
                        status = TransactionStatus.SUCCESS.name,
                        paymentMethod = "GAME_TABLE",
                        referenceNumber = gameRecord.gameId,
                        accountOrUpiDetail = "Pot: ₹${potAmount.toInt()} (Net after ${commissionRate}% rake: ₹${netPrize.toInt()})"
                    )
                )
            }
        } else {
            // User lost
            userDao.incrementGameStats(userId, isWon = 0)
            val user = userDao.getUserById(userId)
            if (user != null) {
                transactionDao.insertTransaction(
                    TransactionEntity(
                        transactionId = "TXN_LOSS_" + UUID.randomUUID().toString().take(8).uppercase(),
                        userId = userId,
                        userName = user.name,
                        type = TransactionType.GAME_LOSS.name,
                        amount = bootAmount,
                        status = TransactionStatus.SUCCESS.name,
                        paymentMethod = "GAME_TABLE",
                        referenceNumber = gameRecord.gameId,
                        accountOrUpiDetail = "Won by $winnerName with $winningHandTitle"
                    )
                )
            }
        }

        return netPrize
    }

    suspend fun deductBetFromUser(userId: String, betAmount: Double): Boolean {
        val user = userDao.getUserById(userId) ?: return false
        if (user.totalAvailableBalance < betAmount) return false

        // Deduct priority: Bonus first (up to 20%), then Wallet, then Winning
        var remaining = betAmount
        var newBonus = user.bonusBalance
        var newWallet = user.walletBalance
        var newWinning = user.winningBalance

        // 20% max from bonus
        val bonusUsable = minOf(newBonus, betAmount * 0.2)
        newBonus -= bonusUsable
        remaining -= bonusUsable

        if (newWallet >= remaining) {
            newWallet -= remaining
            remaining = 0.0
        } else {
            remaining -= newWallet
            newWallet = 0.0
            newWinning = maxOf(0.0, newWinning - remaining)
        }

        userDao.updateBalances(userId, newWallet, newWinning, newBonus)
        return true
    }

    // Platform Settings
    fun getPlatformSettingsFlow(): Flow<PlatformSettingsEntity?> = platformSettingsDao.getSettingsFlow()
    suspend fun getPlatformSettings(): PlatformSettingsEntity =
        platformSettingsDao.getSettings() ?: PlatformSettingsEntity()

    suspend fun updatePlatformSettings(
        commissionPercent: Double,
        minDeposit: Double,
        minWithdraw: Double,
        adminUpiId: String,
        adminUpiName: String
    ) {
        platformSettingsDao.updateConfig(commissionPercent, minDeposit, minWithdraw, adminUpiId, adminUpiName)
    }

    fun getTotalPlatformCommission(): Flow<Double?> = gameHistoryDao.getTotalRakeEarningsFlow()
    fun getTotalGamesCount(): Flow<Int> = gameHistoryDao.getTotalGamesCountFlow()
    fun getAllGamesFlow(): Flow<List<GameHistoryEntity>> = gameHistoryDao.getAllGames()
    fun getRecentGamesFlow(limit: Int = 20): Flow<List<GameHistoryEntity>> = gameHistoryDao.getRecentGames(limit)
    fun getTotalPotTurnover(): Flow<Double?> = gameHistoryDao.getTotalPotTurnoverFlow()
}
