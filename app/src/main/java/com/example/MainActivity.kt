package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.GameTableScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.TeenPattiViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: TeenPattiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: TeenPattiViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val pendingDeposits by viewModel.pendingDeposits.collectAsState()
    val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsState()
    val pendingAdminCount = pendingDeposits.size + pendingWithdrawals.size
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Handle back button
    BackHandler(enabled = currentScreen != AppScreen.LOBBY && currentScreen != AppScreen.AUTH) {
        when (currentScreen) {
            AppScreen.GAME_TABLE -> viewModel.leaveTable()
            else -> viewModel.navigateTo(AppScreen.LOBBY)
        }
    }

    val showBottomBar = currentScreen != AppScreen.AUTH && currentScreen != AppScreen.GAME_TABLE

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                com.example.ui.components.CasinoBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.AUTH -> {
                    AuthScreen(viewModel = viewModel)
                }
                AppScreen.LOBBY -> {
                    LobbyScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onNavigate = { screen ->
                            if (screen == AppScreen.ADMIN_PANEL) {
                                viewModel.openAdminPanel()
                            } else {
                                viewModel.navigateTo(screen)
                            }
                        }
                    )
                }
                AppScreen.GAME_TABLE -> {
                    GameTableScreen(
                        viewModel = viewModel,
                        onLeaveTable = { viewModel.leaveTable() }
                    )
                }
                AppScreen.WALLET -> {
                    WalletScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onBack = { viewModel.navigateTo(AppScreen.LOBBY) }
                    )
                }
                AppScreen.ADMIN_PANEL -> {
                    AdminPanelScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppScreen.LOBBY) }
                    )
                }
                AppScreen.RULES_GUIDE -> {
                    LobbyScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onNavigate = { screen ->
                            if (screen == AppScreen.ADMIN_PANEL) {
                                viewModel.openAdminPanel()
                            } else {
                                viewModel.navigateTo(screen)
                            }
                        }
                    )
                }
                AppScreen.PROFILE -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onBack = { viewModel.navigateTo(AppScreen.LOBBY) },
                        onNavigateWallet = { viewModel.navigateTo(AppScreen.WALLET) }
                    )
                }
            }
        }
    }
}
