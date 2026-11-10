package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.ui.AuthState
import com.example.ui.MainViewModel
import com.example.ui.WorkspaceView
import com.example.ui.components.CitizenTopBar
import com.example.ui.components.VoiceAssistantBottomSheet
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CitizenAuthScreen
import com.example.ui.screens.CitizenProfileScreen
import com.example.ui.screens.CitizenScreen
import com.example.ui.screens.OperatorLoginScreen
import com.example.ui.screens.OperatorScreen
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftBackground
import com.example.ui.theme.YojanaMitraTheme
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure App Check before rendering Compose UI or Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            YojanaMitraTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = SoftBackground
                ) {
                    YojanaMitraApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun YojanaMitraApp(viewModel: MainViewModel) {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    val authState by viewModel.authState.collectAsState()
    val isProfileViewOpen by viewModel.isProfileViewOpen.collectAsState()
    val currentWorkspace by viewModel.currentWorkspace.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isVoiceAssistantOpen by viewModel.isVoiceAssistantOpen.collectAsState()
    val assistantMessage by viewModel.assistantMessage.collectAsState()
    val matchedSchemes by viewModel.voiceMatchedSchemes.collectAsState()

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    when (currentWorkspace) {
        // 1. Citizen Mobile Application (Primary Public Workspace - Gated by Authentication)
        WorkspaceView.CITIZEN -> {
            when (authState) {
                // Splash & Loading State: Checks existing Firebase token/session
                AuthState.CHECKING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SoftBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.yojanamitra_icon_1793970169334),
                                    contentDescription = "YojanaMitra Logo",
                                    modifier = Modifier.size(68.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "YojanaMitra",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeep
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "सत्र सत्यापन हो रहा है...",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7C72)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = EmeraldPrimary,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }

                // Unauthenticated State: Citizen Welcome, Onboarding, Login & Registration
                AuthState.UNAUTHENTICATED -> {
                    CitizenAuthScreen(
                        viewModel = viewModel,
                        onAuthSuccess = {
                            viewModel.onAuthCompleted()
                        }
                    )
                }

                // Authenticated State: Full Citizen Experience Protected by Auth
                AuthState.AUTHENTICATED -> {
                    if (isProfileViewOpen) {
                        BackHandler {
                            viewModel.closeProfileView()
                        }
                        CitizenProfileScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.closeProfileView() }
                        )
                    } else {
                        Scaffold(
                            topBar = {
                                CitizenTopBar(
                                    currentLanguage = currentLanguage,
                                    onLanguageSelected = { viewModel.setLanguage(it) },
                                    userEmail = currentUser?.email,
                                    onProfileClick = { viewModel.openProfileView() }
                                )
                            },
                            containerColor = SoftBackground
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                CitizenScreen(viewModel = viewModel)

                                if (isVoiceAssistantOpen) {
                                    VoiceAssistantBottomSheet(
                                        speechManager = viewModel.speechManager,
                                        currentLanguage = currentLanguage,
                                        assistantMessage = assistantMessage,
                                        matchedSchemes = matchedSchemes,
                                        onVoiceQuery = { query ->
                                            viewModel.processVoiceQuery(query)
                                        },
                                        onSchemeSelected = { scheme ->
                                            viewModel.setSearchQuery(scheme.schemeNameHindi)
                                            viewModel.closeVoiceAssistant()
                                        },
                                        onDismiss = {
                                            viewModel.closeVoiceAssistant()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Protected Operator Login Gateway
        WorkspaceView.OPERATOR_LOGIN -> {
            OperatorLoginScreen(
                viewModel = viewModel,
                onBackToCitizen = { viewModel.exitStaffToCitizen() }
            )
        }

        // 3. Protected Operator Dashboard (Jurisdiction Isolated)
        WorkspaceView.OPERATOR_DASHBOARD -> {
            OperatorScreen(viewModel = viewModel)
        }

        // 4. Protected Super Admin Login Gateway
        WorkspaceView.ADMIN_LOGIN -> {
            AdminLoginScreen(
                viewModel = viewModel,
                onBackToCitizen = { viewModel.exitStaffToCitizen() }
            )
        }

        // 5. Protected Super Admin Management Portal
        WorkspaceView.ADMIN_DASHBOARD -> {
            AdminScreen(viewModel = viewModel)
        }
    }
}
