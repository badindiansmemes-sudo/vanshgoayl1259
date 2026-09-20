package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.GameHistoryEntity
import com.example.data.model.PlatformSettingsEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.data.repository.AuthResult
import com.example.data.repository.FirebaseAuthRepository
import com.example.data.repository.TeenPattiRepository
import com.example.game.TableConfig
import com.example.game.TableState
import com.example.game.TeenPattiGameManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    AUTH,
    LOBBY,
    GAME_TABLE,
    WALLET,
    ADMIN_PANEL,
    PROFILE,
    RULES_GUIDE
}

class TeenPattiViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = TeenPattiRepository(db)
    val firebaseAuthRepository = FirebaseAuthRepository(application)

    val firebaseUser: StateFlow<FirebaseUser?> = firebaseAuthRepository.observeAuthState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), firebaseAuthRepository.currentUser)

    private val _currentScreen = MutableStateFlow(AppScreen.LOBBY)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>("USER_101") // Start with demo player
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getUserFlow(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val platformSettings: StateFlow<PlatformSettingsEntity> = repository.getPlatformSettingsFlow()
        .flatMapLatest { flowOf(it ?: PlatformSettingsEntity()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlatformSettingsEntity())

    val userTransactions: StateFlow<List<TransactionEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getTransactionsForUser(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin flows
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingDeposits: StateFlow<List<TransactionEntity>> = repository.getPendingDeposits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingWithdrawals: StateFlow<List<TransactionEntity>> = repository.getPendingWithdrawals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCommission: StateFlow<Double?> = repository.getTotalPlatformCommission()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalGamesCount: StateFlow<Int> = repository.getTotalGamesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentGames: StateFlow<List<GameHistoryEntity>> = repository.getRecentGamesFlow(25)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalTurnover: StateFlow<Double?> = repository.getTotalPotTurnover()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalApprovedDeposits: StateFlow<Double?> = repository.getTotalApprovedDeposits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalApprovedWithdrawals: StateFlow<Double?> = repository.getTotalApprovedWithdrawals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    // Game Manager
    var gameManager: TeenPattiGameManager? = null
        private set

    val tableState: StateFlow<TableState?>
        get() = gameManager?.tableState ?: _nullTableState

    private val _nullTableState = MutableStateFlow<TableState?>(null)

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun login(loginId: String, pass: String) {
        viewModelScope.launch {
            if (loginId.isBlank() || pass.isBlank()) {
                _toastEvent.emit("Please enter credentials!")
                return@launch
            }
            val user = repository.getUserByCredentials(loginId.trim())
            if (user == null || user.password != pass.trim()) {
                _toastEvent.emit("Invalid phone/email or password!")
                return@launch
            }
            if (user.isBanned) {
                _toastEvent.emit("This account is suspended by Admin!")
                return@launch
            }
            _currentUserId.value = user.userId
            _currentScreen.value = if (user.isAdmin) AppScreen.ADMIN_PANEL else AppScreen.LOBBY
            _toastEvent.emit("Welcome back, ${user.name}!")
        }
    }

    fun register(name: String, phone: String, email: String, pass: String) {
        viewModelScope.launch {
            if (phone.isBlank() || pass.isBlank()) {
                _toastEvent.emit("Phone and password required!")
                return@launch
            }
            val existing = repository.getUserByCredentials(phone.trim())
            if (existing != null) {
                _toastEvent.emit("An account with this phone already exists!")
                return@launch
            }
            val user = repository.registerUser(name, phone.trim(), email.trim(), pass.trim())
            _currentUserId.value = user.userId
            _currentScreen.value = AppScreen.LOBBY
            _toastEvent.emit("Account created! ₹500 welcome chips added.")
        }
    }

    fun updateProfile(name: String, phone: String, email: String, avatarId: Int) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            if (name.isBlank() || phone.isBlank()) {
                _toastEvent.emit("Name and phone cannot be empty!")
                return@launch
            }
            repository.updateProfile(user.userId, name.trim(), phone.trim(), email.trim(), avatarId)
            _toastEvent.emit("Profile updated successfully!")
        }
    }

    fun recoverPassword(login: String, newPass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (login.isBlank() || newPass.isBlank()) {
                _toastEvent.emit("Please enter phone/email and new password!")
                onResult(false)
                return@launch
            }
            val success = repository.resetPassword(login.trim(), newPass.trim())
            if (success) {
                _toastEvent.emit("Password reset successfully! Please sign in.")
                onResult(true)
            } else {
                _toastEvent.emit("No user found with this phone or email!")
                onResult(false)
            }
        }
    }

    fun loginWithFirebase(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            if (email.isBlank() || pass.isBlank()) {
                _toastEvent.emit("Please enter your email and password!")
                return@launch
            }
            when (val result = firebaseAuthRepository.signInWithEmail(email, pass)) {
                is AuthResult.Success -> {
                    val user = result.data
                    val localUser = repository.getOrCreateFirebaseUser(
                        uid = user.uid,
                        email = user.email ?: email.trim(),
                        displayName = user.displayName
                    )
                    _currentUserId.value = localUser.userId
                    _currentScreen.value = AppScreen.LOBBY
                    _toastEvent.emit("Firebase login successful! Welcome, ${localUser.name}")
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _toastEvent.emit(result.message)
                }
                AuthResult.Loading -> Unit
            }
        }
    }

    fun registerWithFirebase(name: String, email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            if (email.isBlank() || pass.isBlank()) {
                _toastEvent.emit("Please enter email and password!")
                return@launch
            }
            when (val result = firebaseAuthRepository.signUpWithEmail(email, pass, name)) {
                is AuthResult.Success -> {
                    val user = result.data
                    val localUser = repository.getOrCreateFirebaseUser(
                        uid = user.uid,
                        email = user.email ?: email.trim(),
                        displayName = name.ifBlank { user.displayName }
                    )
                    _currentUserId.value = localUser.userId
                    _currentScreen.value = AppScreen.LOBBY
                    _toastEvent.emit("Account created via Firebase! ₹1,000 welcome chips added.")
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _toastEvent.emit(result.message)
                }
                AuthResult.Loading -> Unit
            }
        }
    }

    fun sendFirebasePasswordReset(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _toastEvent.emit("Please enter an email address!")
                onResult(false)
                return@launch
            }
            when (val result = firebaseAuthRepository.sendPasswordResetEmail(email)) {
                is AuthResult.Success -> {
                    _toastEvent.emit("Password reset email sent to $email!")
                    onResult(true)
                }
                is AuthResult.Error -> {
                    _toastEvent.emit(result.message)
                    onResult(false)
                }
                AuthResult.Loading -> Unit
            }
        }
    }

    fun signOutFirebase() {
        firebaseAuthRepository.signOut()
        _toastEvent.tryEmit("Signed out from Firebase")
    }

    fun switchUserQuick(asAdmin: Boolean) {
        viewModelScope.launch {
            if (asAdmin) {
                _currentUserId.value = "ADMIN_001"
                _currentScreen.value = AppScreen.ADMIN_PANEL
                _toastEvent.emit("Switched to House Admin Mode")
            } else {
                _currentUserId.value = "USER_101"
                _currentScreen.value = AppScreen.LOBBY
                _toastEvent.emit("Switched to Player Mode (Vansh)")
            }
        }
    }

    fun openAdminPanel() {
        viewModelScope.launch {
            val user = currentUser.value
            if (user?.isAdmin != true) {
                // Ensure the user has administrative powers or seamlessly switch to ADMIN_001
                _currentUserId.value = "ADMIN_001"
                _toastEvent.emit("👑 Switched to Master Admin Mode")
            }
            _currentScreen.value = AppScreen.ADMIN_PANEL
        }
    }

    fun claimDailyBonus() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val bonus = repository.claimDailyBonus(user.userId, 500.0)
            _toastEvent.emit("🎉 Congratulations! ₹${bonus.toInt()} Free VIP Bonus Chips Credited!")
        }
    }

    fun logout() {
        _currentUserId.value = null
        _currentScreen.value = AppScreen.AUTH
    }

    // Table Management
    fun joinTable(config: TableConfig) {
        viewModelScope.launch {
            var user = currentUser.value
            if (user == null) {
                repository.seedInitialDataIfNeeded()
                user = repository.getOrCreateGuestUser()
                _currentUserId.value = user.userId
            }
            if (user.totalAvailableBalance < config.bootAmount) {
                val refill = maxOf(2000.0, config.bootAmount * 10)
                repository.addFreePlayChips(user.userId, refill)
                _toastEvent.emit("🎁 Free ₹${refill.toInt()} chips added to play!")
                user = repository.getUserById(user.userId) ?: user
            }

            val finalUser = user
            gameManager = TeenPattiGameManager(
                scope = viewModelScope,
                onRoundFinished = { boot, pot, winner, handTitle, isHumanWinner ->
                    viewModelScope.launch {
                        val netPrize = repository.recordGameRound(
                            bootAmount = boot,
                            potAmount = pot,
                            winnerId = winner.id,
                            winnerName = winner.name,
                            winningHandTitle = handTitle,
                            isUserWinner = isHumanWinner,
                            userId = finalUser.userId
                        )
                        if (isHumanWinner) {
                            _toastEvent.emit("🏆 YOU WON ₹${netPrize.toInt()}! (House rake deducted)")
                        }
                    }
                },
                onDeductBet = { bet ->
                    repository.deductBetFromUser(finalUser.userId, bet)
                }
            ).apply {
                setupTable(config, finalUser.name, maxOf(finalUser.totalAvailableBalance, config.bootAmount * 10))
            }

            _currentScreen.value = AppScreen.GAME_TABLE
        }
    }

    fun leaveTable() {
        gameManager = null
        _currentScreen.value = AppScreen.LOBBY
    }

    // Wallet actions
    fun depositCash(amount: Double, method: String, utr: String, isInstantGateway: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            if (amount <= 0) {
                _toastEvent.emit("Enter a valid deposit amount!")
                return@launch
            }
            repository.submitDeposit(
                userId = user.userId,
                userName = user.name,
                amount = amount,
                method = method,
                utrNumber = utr,
                instantApprove = isInstantGateway
            )
            if (isInstantGateway) {
                _toastEvent.emit("₹${amount.toInt()} deposited instantly via Gateway!")
            } else {
                _toastEvent.emit("Deposit request submitted with UTR: $utr. Awaiting Admin verification.")
            }
        }
    }

    fun withdrawWinnings(amount: Double, method: String, destination: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val settings = platformSettings.value
            if (amount < settings.minWithdraw) {
                _toastEvent.emit("Minimum withdrawal amount is ₹${settings.minWithdraw.toInt()}!")
                return@launch
            }
            if (destination.isBlank()) {
                _toastEvent.emit("Please enter valid UPI ID or Bank details!")
                return@launch
            }

            val result = repository.submitWithdrawal(
                userId = user.userId,
                userName = user.name,
                amount = amount,
                payoutMethod = method,
                accountDetail = destination
            )

            result.fold(
                onSuccess = {
                    _toastEvent.emit("Withdrawal request for ₹${amount.toInt()} submitted! Status: PENDING Admin payout.")
                },
                onFailure = { err ->
                    _toastEvent.emit(err.message ?: "Withdrawal failed")
                }
            )
        }
    }

    // Admin panel controls
    fun adminApproveDeposit(txnId: String) {
        viewModelScope.launch {
            repository.approveDeposit(txnId)
            _toastEvent.emit("Deposit approved and credited to user wallet!")
        }
    }

    fun adminRejectDeposit(txnId: String, reason: String = "UTR Not verified") {
        viewModelScope.launch {
            repository.rejectDeposit(txnId, reason)
            _toastEvent.emit("Deposit marked rejected.")
        }
    }

    fun adminApproveWithdrawal(txnId: String) {
        viewModelScope.launch {
            repository.approveWithdrawal(txnId, "Paid via Admin Banking")
            _toastEvent.emit("Withdrawal approved & marked paid!")
        }
    }

    fun adminRejectWithdrawal(txnId: String, reason: String = "Details mismatch / Refunded") {
        viewModelScope.launch {
            repository.rejectWithdrawal(txnId, reason)
            _toastEvent.emit("Withdrawal rejected and refunded to user's winning balance!")
        }
    }

    fun adminAdjustUserBalance(userId: String, newWallet: Double, newWinning: Double, newBonus: Double) {
        viewModelScope.launch {
            repository.updateUserBalances(userId, newWallet, newWinning, newBonus)
            _toastEvent.emit("User balance updated!")
        }
    }

    fun adminToggleBanUser(userId: String, currentBan: Boolean) {
        viewModelScope.launch {
            repository.setUserBanned(userId, !currentBan)
            _toastEvent.emit(if (!currentBan) "User suspended!" else "User unsuspended!")
        }
    }

    fun adminSaveSettings(
        commissionPercent: Double,
        minDeposit: Double,
        minWithdraw: Double,
        adminUpiId: String,
        adminUpiName: String
    ) {
        viewModelScope.launch {
            repository.updatePlatformSettings(
                commissionPercent, minDeposit, minWithdraw, adminUpiId, adminUpiName
            )
            _toastEvent.emit("Platform settings & commission cut updated successfully!")
        }
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }
}
