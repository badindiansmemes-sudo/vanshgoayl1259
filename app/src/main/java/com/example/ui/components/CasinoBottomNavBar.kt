package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CasinoGreenDark
import com.example.ui.theme.CasinoGreenLight
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceDark
import com.example.viewmodel.AppScreen

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0,
    val isSpecialAdmin: Boolean = false
)

@Composable
fun CasinoBottomNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(AppScreen.LOBBY, "Lobby", Icons.Default.Home),
        NavItem(AppScreen.RULES_GUIDE, "Tables", Icons.Default.Casino),
        NavItem(AppScreen.WALLET, "Add Cash", Icons.Default.AccountBalanceWallet),
        NavItem(AppScreen.PROFILE, "Profile", Icons.Default.Person)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF07140B),
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color(0x55FFD700), Color(0x11000000))
                    ),
                    shape = RoundedCornerShape(0.dp)
                )
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(bounded = true, color = GoldLight),
                                onClick = { onNavigate(item.screen) }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFFF1744),
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = item.badgeCount.toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else if (item.isSpecialAdmin) {
                                    Badge(
                                        containerColor = Color(0xFFE53935),
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = "PRO",
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            item.isSpecialAdmin && isSelected ->
                                                Brush.radialGradient(listOf(Color(0xFFFF5252), Color(0xFFB71C1C)))
                                            item.isSpecialAdmin ->
                                                Brush.radialGradient(listOf(Color(0xFF421200), Color(0xFF1B0000)))
                                            isSelected ->
                                                Brush.radialGradient(listOf(Color(0x66FFD700), Color(0x221B5E20)))
                                            else ->
                                                Brush.radialGradient(listOf(Color.Transparent, Color.Transparent))
                                        }
                                    )
                                    .border(
                                        width = if (isSelected || item.isSpecialAdmin) 1.dp else 0.dp,
                                        color = if (item.isSpecialAdmin) Color(0xFFFF5252) else if (isSelected) GoldPrimary else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = when {
                                        item.isSpecialAdmin -> Color(0xFFFFD54F)
                                        isSelected -> GoldPrimary
                                        else -> Color(0xFF889988)
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.label,
                            color = when {
                                item.isSpecialAdmin -> Color(0xFFFFB74D)
                                isSelected -> GoldLight
                                else -> Color(0xFF889988)
                            },
                            fontSize = 10.sp,
                            fontWeight = if (isSelected || item.isSpecialAdmin) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
