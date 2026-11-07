package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import com.example.ui.MainViewModel
import com.example.ui.WorkspaceView
import com.example.ui.components.CitizenTopBar
import com.example.ui.components.VoiceAssistantBottomSheet
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CitizenScreen
import com.example.ui.screens.OperatorLoginScreen
import com.example.ui.screens.OperatorScreen
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

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
        // 1. Citizen Mobile Application (Primary Public Workspace - No Admin/Operator UI)
        WorkspaceView.CITIZEN -> {
            Scaffold(
                topBar = {
                    CitizenTopBar(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { viewModel.setLanguage(it) },
                        userEmail = currentUser?.email,
                        onProfileClick = {}
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
