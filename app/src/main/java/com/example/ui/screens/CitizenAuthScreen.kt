package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.ui.MainViewModel
import com.example.ui.components.GoogleSignInCard
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftBackground
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.launch

@Composable
fun CitizenAuthScreen(
    viewModel: MainViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Register
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetLoading by remember { mutableStateOf(false) }
    var resetStatusMessage by remember { mutableStateOf<String?>(null) }

    var languageDropdownExpanded by remember { mutableStateOf(false) }

    val isHindi = currentLanguage == AppLanguage.HINDI || currentLanguage == AppLanguage.BHOJPURI || currentLanguage == AppLanguage.MAITHILI

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        color = SoftBackground
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Language Picker
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Government Service",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "राष्ट्रीय योजना पोर्टल" else "National Scheme Portal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldDeep
                        )
                    }

                    // Language Selector
                    Box {
                        Surface(
                            onClick = { languageDropdownExpanded = true },
                            shape = RoundedCornerShape(16.dp),
                            color = EmeraldContainer,
                            modifier = Modifier.testTag("auth_language_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = EmeraldDeep,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentLanguage.displayName.substringBefore(" "),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDeep
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = languageDropdownExpanded,
                            onDismissRequest = { languageDropdownExpanded = false }
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang.displayName) },
                                    onClick = {
                                        viewModel.setLanguage(lang)
                                        languageDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Brand Emblem and App Title
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.yojanamitra_icon_1793970169334),
                        contentDescription = "YojanaMitra Logo",
                        modifier = Modifier.size(62.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "YojanaMitra",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldDeep,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = if (isHindi) "आपका विश्वसनीय सरकारी योजना साथी" else "Your Trusted Government Scheme Companion",
                    fontSize = 13.sp,
                    color = Color(0xFF5C6F65),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Google Sign-In Card (Primary 1-Tap Auth)
            item {
                GoogleSignInCard(
                    onAuthSuccess = {
                        viewModel.onAuthCompleted()
                        onAuthSuccess()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD4DFD8))
                    Text(
                        text = if (isHindi) " अथवा ईमेल से " else " or with email ",
                        fontSize = 12.sp,
                        color = Color(0xFF7B8D83),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD4DFD8))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Login / Register Tabs and Form Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color(0xFFF1F6F3),
                            indicator = { tabPositions ->
                                SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = EmeraldPrimary,
                                    height = 3.dp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = {
                                    selectedTab = 0
                                    errorMessage = null
                                    successMessage = null
                                },
                                text = {
                                    Text(
                                        text = if (isHindi) "लॉगिन (Login)" else "Sign In",
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == 0) EmeraldDeep else Color(0xFF75857B)
                                    )
                                },
                                modifier = Modifier.testTag("login_tab")
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = {
                                    selectedTab = 1
                                    errorMessage = null
                                    successMessage = null
                                },
                                text = {
                                    Text(
                                        text = if (isHindi) "नया खाता (Register)" else "Register",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == 1) EmeraldDeep else Color(0xFF75857B)
                                    )
                                },
                                modifier = Modifier.testTag("register_tab")
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Success / Error Messages
                        if (errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFEBEE),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = errorMessage.orEmpty(),
                                    color = Color(0xFFC62828),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        if (successMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8F5E9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = successMessage.orEmpty(),
                                    color = Color(0xFF2E7D32),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        // Registration Extra Fields: Name & Phone
                        if (selectedTab == 1) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text(if (isHindi) "पूरा नाम (Full Name)" else "Full Name") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("full_name_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = Color(0xFFCFDBD5)
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text(if (isHindi) "मोबाइल नंबर (Mobile Number)" else "Mobile Number") },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldPrimary)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = Color(0xFFCFDBD5)
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it.trim() },
                            label = { Text(if (isHindi) "ईमेल आईडी (Email ID)" else "Email ID") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = EmeraldPrimary)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = Color(0xFFCFDBD5)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(if (isHindi) "पासवर्ड (Password)" else "Password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Password"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = Color(0xFFCFDBD5)
                            )
                        )

                        // Registration: Confirm Password
                        if (selectedTab == 1) {
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text(if (isHindi) "पासवर्ड पुनः दर्ज करें (Confirm Password)" else "Confirm Password") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary)
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("confirm_password_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = Color(0xFFCFDBD5)
                                )
                            )
                        }

                        // Forgot Password Link (for Login Tab)
                        if (selectedTab == 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        resetEmail = email
                                        resetStatusMessage = null
                                        showForgotPasswordDialog = true
                                    },
                                    modifier = Modifier.testTag("forgot_password_btn")
                                ) {
                                    Text(
                                        text = if (isHindi) "पासवर्ड भूल गए? (Forgot Password?)" else "Forgot Password?",
                                        fontSize = 12.sp,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                errorMessage = null
                                successMessage = null

                                if (email.isBlank() || !email.contains("@")) {
                                    errorMessage = if (isHindi) "कृपया एक मान्य ईमेल दर्ज करें।" else "Please enter a valid email."
                                    return@Button
                                }
                                if (password.length < 6) {
                                    errorMessage = if (isHindi) "पासवर्ड कम से कम 6 अक्षरों का होना चाहिए।" else "Password must be at least 6 characters."
                                    return@Button
                                }

                                if (selectedTab == 1) {
                                    if (fullName.isBlank()) {
                                        errorMessage = if (isHindi) "कृपया अपना पूरा नाम दर्ज करें।" else "Please enter your full name."
                                        return@Button
                                    }
                                    if (password != confirmPassword) {
                                        errorMessage = if (isHindi) "पासवर्ड मेल नहीं खाते।" else "Passwords do not match."
                                        return@Button
                                    }

                                    isLoading = true
                                    viewModel.registerWithEmail(
                                        email = email,
                                        password = password,
                                        fullName = fullName,
                                        phoneNumber = phoneNumber
                                    ) { result ->
                                        isLoading = false
                                        result.onSuccess {
                                            successMessage = if (isHindi) "पंजीकरण सफल! आपका स्वागत है।" else "Registration successful! Welcome."
                                            onAuthSuccess()
                                        }.onFailure { err ->
                                            errorMessage = err.localizedMessage ?: "Registration failed."
                                        }
                                    }
                                } else {
                                    isLoading = true
                                    viewModel.loginWithEmail(
                                        email = email,
                                        password = password
                                    ) { result ->
                                        isLoading = false
                                        result.onSuccess {
                                            onAuthSuccess()
                                        }.onFailure { err ->
                                            errorMessage = err.localizedMessage ?: "Invalid email or password."
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag(if (selectedTab == 0) "login_submit_btn" else "register_submit_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (selectedTab == 0) {
                                        if (isHindi) "लॉगिन करें (Sign In)" else "Sign In"
                                    } else {
                                        if (isHindi) "खाता बनाएं (Create Account)" else "Create Account"
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Benefits Points Footer
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = EmeraldContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isHindi) "नागरिक सुविधाएँ (Citizen Benefits)" else "Citizen Benefits",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isHindi) "• 250+ सत्यापित केंद्र और राज्य योजनाओं की सटीक जानकारी" else "• 250+ Verified Central & State Scheme details",
                            fontSize = 12.sp,
                            color = EmeraldDeep
                        )
                        Text(
                            text = if (isHindi) "• भूमि, आय और श्रेणी के आधार पर तुरंत पात्रता जांच" else "• Instant eligibility calculation based on your profile",
                            fontSize = 12.sp,
                            color = EmeraldDeep
                        )
                        Text(
                            text = if (isHindi) "• ऑफ़लाइन सुविधा - बिना इंटरनेट के योजनाओं की खोज" else "• Offline support - search schemes without internet",
                            fontSize = 12.sp,
                            color = EmeraldDeep
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = {
                    Text(
                        text = if (isHindi) "पासवर्ड रीसेट करें (Reset Password)" else "Reset Password",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                },
                text = {
                    Column {
                        Text(
                            text = if (isHindi) "अपना पंजीकृत ईमेल दर्ज करें। हम आपको पासवर्ड रीसेट लिंक भेजेंगे।" else "Enter your registered email. We will send you a password reset link.",
                            fontSize = 13.sp,
                            color = Color(0xFF55655D)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it.trim() },
                            label = { Text("Email ID") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_email_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        if (resetStatusMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = resetStatusMessage.orEmpty(),
                                fontSize = 12.sp,
                                color = EmeraldPrimary
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (resetEmail.isBlank() || !resetEmail.contains("@")) {
                                resetStatusMessage = if (isHindi) "मान्य ईमेल दर्ज करें" else "Enter a valid email"
                                return@Button
                            }
                            resetLoading = true
                            viewModel.sendPasswordReset(resetEmail) { res ->
                                resetLoading = false
                                res.onSuccess {
                                    resetStatusMessage = if (isHindi) "पासवर्ड रीसेट लिंक ईमेल पर भेजा गया!" else "Password reset link sent to your email!"
                                }.onFailure { err ->
                                    resetStatusMessage = err.localizedMessage ?: "Failed to send reset email"
                                }
                            }
                        },
                        enabled = !resetLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        if (resetLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                        } else {
                            Text(if (isHindi) "लिंक भेजें" else "Send Link")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text(if (isHindi) "रद्द करें" else "Cancel")
                    }
                }
            )
        }
    }
}
