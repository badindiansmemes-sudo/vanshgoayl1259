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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CasinoGreenDark
import com.example.ui.theme.CasinoGreenLight
import com.example.ui.theme.CasinoGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TeenPattiViewModel

@Composable
fun AuthScreen(
    viewModel: TeenPattiViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Sign Up

    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    var showOtpDialog by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var otpInput by remember { mutableStateOf("") }

    var forgotLoginInput by remember { mutableStateOf("") }
    var forgotOtpInput by remember { mutableStateOf("") }
    var forgotNewPassInput by remember { mutableStateOf("") }
    var forgotStep by remember { mutableIntStateOf(1) } // 1: identify, 2: verify & reset

    val firebaseUser by viewModel.firebaseUser.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CasinoGreenDark, Color(0xFF04120A), Color.Black)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Branding Logo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(GoldLight, GoldDark, Color(0xFF3E2723))
                        )
                    )
                    .border(2.dp, GoldPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = "Teen Patti",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "TEEN PATTI ROYAL",
                color = GoldPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = "Real Gaming • Instant Payouts • House Admin Hub",
                color = Color(0xFFA5D6A7),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Card with Auth Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SurfaceDark,
                        contentColor = GoldPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = GoldPrimary,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Sign In", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Sign Up", fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTab == 1) {
                        // Sign Up - Name
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldLight) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0x33FFD700),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Phone Number
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it.trim() },
                        label = { Text(if (selectedTab == 0) "Phone or Email" else "Mobile Number", color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldLight) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0x33FFD700),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedTab == 1) {
                        // Sign Up - Email
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it.trim() },
                            label = { Text("Email (Optional)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0x33FFD700),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Password
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password", color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldLight) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color(0x33FFD700),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    if (selectedTab == 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Forgot Password?",
                                color = GoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable { showForgotDialog = true }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (selectedTab == 0) {
                                viewModel.login(phoneInput, passwordInput)
                            } else {
                                if (phoneInput.length < 10) {
                                    // Trigger quick validation
                                    errorMessage = "Please enter a valid 10-digit mobile number!"
                                    return@Button
                                }
                                if (passwordInput.length < 3) {
                                    errorMessage = "Password must be at least 3 characters!"
                                    return@Button
                                }
                                errorMessage = null
                                otpInput = "582914" // prefill simulation OTP for convenience
                                showOtpDialog = true
                            }
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
                            text = if (selectedTab == 0) "SIGN IN TO PLAY" else "VERIFY OTP & GET ₹500",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Firebase Email/Password Authentication Button
                    Button(
                        onClick = {
                            if (selectedTab == 0) {
                                val emailToUse = if (phoneInput.contains("@")) phoneInput else emailInput
                                viewModel.loginWithFirebase(emailToUse, passwordInput)
                            } else {
                                val emailToUse = if (emailInput.isNotBlank()) emailInput else phoneInput
                                viewModel.registerWithFirebase(nameInput, emailToUse, passwordInput)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD84315), // Deep Orange / Firebase
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Firebase",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedTab == 0) "SIGN IN WITH FIREBASE EMAIL" else "REGISTER WITH FIREBASE EMAIL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (firebaseUser != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33FF6F00))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Firebase: ${firebaseUser?.email ?: firebaseUser?.uid?.take(8)}",
                                color = Color(0xFFFFCC80),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Sign Out",
                                color = Color(0xFFFF5252),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { viewModel.signOutFirebase() }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "— OR QUICK 1-TAP DEMO ACCESS —",
                        color = TextMuted,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick 1-Tap Demo Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.switchUserQuick(asAdmin = false) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A2F),
                                contentColor = Color(0xFFA5D6A7)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Player Demo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.switchUserQuick(asAdmin = true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B1754),
                                contentColor = GoldPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("House Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 1. Phone & Email OTP Verification Dialog
        if (showOtpDialog) {
            AlertDialog(
                onDismissRequest = { showOtpDialog = false },
                containerColor = SurfaceCard,
                icon = {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "Verify Phone & Email",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "We have sent a 6-digit verification code to +91 $phoneInput ${if (emailInput.isNotBlank()) "and $emailInput" else ""}.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { if (it.length <= 6) otpInput = it },
                            label = { Text("Enter 6-Digit OTP", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = Color(0x33FFD700),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Code auto-filled for testing: 582914",
                                color = GoldLight,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Resend OTP",
                                color = CasinoGreenLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { otpInput = "582914" }
                                    .padding(4.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (otpInput.length >= 4) {
                                showOtpDialog = false
                                viewModel.register(nameInput, phoneInput, emailInput, passwordInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Verify & Finish Signup", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showOtpDialog = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }

        // 2. Password Recovery Flow Dialog
        if (showForgotDialog) {
            AlertDialog(
                onDismissRequest = {
                    showForgotDialog = false
                    forgotStep = 1
                    forgotLoginInput = ""
                    forgotOtpInput = ""
                    forgotNewPassInput = ""
                },
                containerColor = SurfaceCard,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = if (forgotStep == 1) "Password Recovery" else "Set New Password",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (forgotStep == 1) {
                            Text(
                                text = "Enter your registered mobile number or email address to receive a secure recovery code.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            OutlinedTextField(
                                value = forgotLoginInput,
                                onValueChange = { forgotLoginInput = it.trim() },
                                label = { Text("Registered Phone or Email", color = TextSecondary) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x33FFD700),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            if (forgotLoginInput.contains("@")) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.sendFirebasePasswordReset(forgotLoginInput) { success ->
                                            if (success) {
                                                showForgotDialog = false
                                                forgotStep = 1
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFFFB74D)
                                    )
                                ) {
                                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Send Firebase Reset Email", fontSize = 12.sp)
                                }
                            }
                        } else {
                            Text(
                                text = "Enter the 6-digit recovery code sent to $forgotLoginInput and choose your new password.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            OutlinedTextField(
                                value = forgotOtpInput,
                                onValueChange = { if (it.length <= 6) forgotOtpInput = it },
                                label = { Text("Recovery Code (e.g. 782491)", color = TextSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x33FFD700),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = forgotNewPassInput,
                                onValueChange = { forgotNewPassInput = it },
                                label = { Text("New Password", color = TextSecondary) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color(0x33FFD700),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (forgotStep == 1) {
                                if (forgotLoginInput.isNotBlank()) {
                                    forgotOtpInput = "782491"
                                    forgotStep = 2
                                }
                            } else {
                                if (forgotOtpInput.isNotBlank() && forgotNewPassInput.isNotBlank()) {
                                    viewModel.recoverPassword(forgotLoginInput, forgotNewPassInput) { success ->
                                        if (success) {
                                            showForgotDialog = false
                                            forgotStep = 1
                                        }
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = if (forgotStep == 1) "Send Recovery Code" else "Reset Password",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showForgotDialog = false
                            forgotStep = 1
                        }
                    ) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }
    }
}
