package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.ApplicationCase
import com.example.data.model.Scheme
import com.example.data.model.ServiceCentre
import com.example.ui.MainViewModel
import com.example.ui.components.DocumentChecklistDialog
import com.example.ui.components.EligibilityBreakdownDialog
import com.example.ui.components.ProfileEditDialog
import com.example.ui.components.SchemeCard
import com.example.ui.components.VoiceInteractionComponent
import com.example.ui.components.VoiceToTextInput
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftBackground
import com.example.ui.theme.StatusEligible

enum class CitizenTab(val labelHindi: String, val labelEnglish: String) {
    SCHEMES("योजनाएं", "Schemes"),
    APPLICATIONS("आवेदन ट्रैक", "Applications"),
    CENTRES("सहायता केंद्र", "Centres")
}

@Composable
fun CitizenScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.currentLanguage.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val schemes by viewModel.filteredSchemes.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val savedIds by viewModel.savedSchemeIds.collectAsState()
    val applications by viewModel.applicationCases.collectAsState()
    val centres by viewModel.serviceCentres.collectAsState()

    val voiceStatus by viewModel.voiceStatus.collectAsState()
    val spokenText by viewModel.speechManager.spokenText.collectAsState()
    val voiceMatchedSchemes by viewModel.voiceMatchedSchemes.collectAsState()
    val jurisdictionScope by viewModel.selectedJurisdictionScope.collectAsState()
    val selectedState by viewModel.selectedState.collectAsState()

    var activeTab by remember { mutableStateOf(CitizenTab.SCHEMES) }
    var selectedSchemeForEligibility by remember { mutableStateOf<Scheme?>(null) }
    var selectedSchemeForDocs by remember { mutableStateOf<Scheme?>(null) }
    var isProfileDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SoftBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == CitizenTab.SCHEMES,
                    onClick = { activeTab = CitizenTab.SCHEMES },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Schemes") },
                    label = { Text(if (language == AppLanguage.ENGLISH) "Schemes" else "योजनाएं") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("tab_schemes")
                )
                NavigationBarItem(
                    selected = activeTab == CitizenTab.APPLICATIONS,
                    onClick = { activeTab = CitizenTab.APPLICATIONS },
                    icon = { Icon(Icons.Default.TrackChanges, contentDescription = "Applications") },
                    label = { Text(if (language == AppLanguage.ENGLISH) "Tracking" else "आवेदन स्थिति") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("tab_applications")
                )
                NavigationBarItem(
                    selected = activeTab == CitizenTab.CENTRES,
                    onClick = { activeTab = CitizenTab.CENTRES },
                    icon = { Icon(Icons.Default.NearMe, contentDescription = "Centres") },
                    label = { Text(if (language == AppLanguage.ENGLISH) "Help Centres" else "सहायता केंद्र") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("tab_centres")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                CitizenTab.SCHEMES -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        // Hero Greeting Section
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = language.greeting,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "बताइए, आपको किस सरकारी योजना की जानकारी चाहिए?",
                                                fontSize = 13.sp,
                                                color = Color(0xFFD2EBE0)
                                            )
                                        }

                                        // Large Voice Action Button
                                        Button(
                                            onClick = { viewModel.openVoiceAssistant() },
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4B400)),
                                            contentPadding = PaddingValues(0.dp),
                                            modifier = Modifier
                                                .size(54.dp)
                                                .testTag("hero_voice_mic_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Mic,
                                                contentDescription = "Voice Search",
                                                tint = EmeraldDeep,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Voice & Text Action CTAs
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.openVoiceAssistant() },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .testTag("cta_speak_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Mic,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("बोलकर पूछें", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        Surface(
                                            color = Color.White.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .clickable { viewModel.setSearchQuery("") }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "लिखकर खोजें",
                                                    fontSize = 13.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Central Voice Interaction Component (Hero Feature for Rural Citizens)
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            VoiceInteractionComponent(
                                status = voiceStatus,
                                spokenText = spokenText,
                                currentLanguage = language,
                                onMicClick = {
                                    viewModel.toggleVoiceListening()
                                },
                                onQuickPromptClick = { prompt ->
                                    viewModel.processVoiceQuery(prompt)
                                },
                                isCardStyle = true
                            )
                        }

                        // Citizen Profile Quick Card
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(EmeraldContainer, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = EmeraldPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = profile?.fullName ?: "नागरिक प्रोफाइल",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldDeep
                                            )
                                            Text(
                                                text = "${profile?.occupation ?: "किसान"} • ${profile?.district ?: "पटना"} (${profile?.landholdingAcres ?: 2.0} एकड़ भूमि)",
                                                fontSize = 11.sp,
                                                color = Color(0xFF66756D)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { isProfileDialogOpen = true },
                                        modifier = Modifier.testTag("edit_profile_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Profile",
                                            tint = EmeraldPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // Voice-to-Text Search Input Component for Initial User Queries
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            VoiceToTextInput(
                                query = searchQuery,
                                onQueryChange = { viewModel.setSearchQuery(it) },
                                onQuerySubmit = { viewModel.setSearchQuery(it) },
                                voiceStatus = voiceStatus,
                                spokenText = spokenText,
                                currentLanguage = language,
                                onStartVoiceInput = {
                                    viewModel.speechManager.startListening(language) { recognizedText ->
                                        viewModel.setSearchQuery(recognizedText)
                                        viewModel.processVoiceQuery(recognizedText)
                                    }
                                },
                                onStopVoiceInput = {
                                    viewModel.speechManager.stopListening()
                                }
                            )
                        }

                        // All-India National / Central / State Jurisdiction Scope Bar
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F8F5)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "योजना का दायरा (Jurisdiction Scope)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldDeep
                                        )
                                        // Quick State Selector Pill
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EmeraldContainer,
                                            modifier = Modifier.clickable {
                                                // Cycle through representative states
                                                val states = listOf("Bihar", "Uttar Pradesh", "Maharashtra", "Madhya Pradesh", "Rajasthan", "Karnataka")
                                                val next = states[(states.indexOf(selectedState) + 1).coerceAtLeast(0) % states.size]
                                                viewModel.setSelectedState(next)
                                            }
                                        ) {
                                            Text(
                                                text = "📍 $selectedState ▾",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldPrimary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        FilterChip(
                                            selected = jurisdictionScope == "ALL_INDIA",
                                            onClick = { viewModel.setJurisdictionScope("ALL_INDIA") },
                                            label = { Text("🇮🇳 अखिल भारतीय (All India)", fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = EmeraldPrimary,
                                                selectedLabelColor = Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChip(
                                            selected = jurisdictionScope == "CENTRAL",
                                            onClick = { viewModel.setJurisdictionScope("CENTRAL") },
                                            label = { Text("केन्द्रीय (Central)", fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF1E40AF),
                                                selectedLabelColor = Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChip(
                                            selected = jurisdictionScope == "MY_STATE",
                                            onClick = { viewModel.setJurisdictionScope("MY_STATE") },
                                            label = { Text("राज्य: $selectedState", fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF7C3AED),
                                                selectedLabelColor = Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // Category Filter Chips
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            val categories = listOf(
                                "All" to "सभी योजनाएं",
                                "Agriculture" to "खेती (Agriculture)",
                                "Healthcare" to "स्वास्थ्य (Health)",
                                "Housing" to "आवास (Housing)",
                                "Education" to "शिक्षा (Education)",
                                "Women & Child" to "महिला एवं बाल",
                                "Pension" to "पेंशन (Pension)",
                                "Employment" to "रोजगार व व्यवसाय"
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(categories) { (key, label) ->
                                    FilterChip(
                                        selected = selectedCategory == key,
                                        onClick = { viewModel.setCategory(key) },
                                        label = { Text(label, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EmeraldPrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        // Schemes List Section Title
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "उपलब्ध सरकारी योजनाएं (${schemes.size})",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = "सत्यापित स्रोत",
                                    fontSize = 11.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // List of Schemes
                        items(schemes) { scheme ->
                            val evaluation = viewModel.evaluateScheme(scheme)
                            val isSaved = savedIds.contains(scheme.id)

                            SchemeCard(
                                scheme = scheme,
                                evaluation = evaluation,
                                isSaved = isSaved,
                                onSaveToggle = { viewModel.toggleSaveScheme(scheme) },
                                onCheckEligibility = { selectedSchemeForEligibility = scheme },
                                onViewDocuments = { selectedSchemeForDocs = scheme }
                            )
                        }

                        // Discreet Staff Gateway Link in Footer (Separating Workspaces)
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "विभागीय कर्मचारी? ",
                                    fontSize = 11.sp,
                                    color = Color(0xFF889890)
                                )
                                Text(
                                    text = "ऑपरेटर लॉगिन",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    modifier = Modifier
                                        .clickable { viewModel.navigateToWorkspace(com.example.ui.WorkspaceView.OPERATOR_LOGIN) }
                                        .testTag("official_portal_link")
                                )
                                Text(
                                    text = " • ",
                                    fontSize = 11.sp,
                                    color = Color(0xFF889890)
                                )
                                Text(
                                    text = "एडमिन पोर्टल",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDeep,
                                    modifier = Modifier
                                        .clickable { viewModel.navigateToWorkspace(com.example.ui.WorkspaceView.ADMIN_LOGIN) }
                                        .testTag("admin_portal_link")
                                )
                            }
                        }
                    }
                }

                CitizenTab.APPLICATIONS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Text(
                                text = "मेरे आवेदन एवं स्थिति (Application Tracking)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeep
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "यहां आपके द्वारा जमा किए गए आवेदनों की वर्तमान आधिकारिक स्थिति प्रदर्शित है।",
                                fontSize = 12.sp,
                                color = Color(0xFF66756D)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        if (applications.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = Color(0xFF88978F),
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "कोई सक्रिय आवेदन नहीं मिला",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EmeraldDeep
                                        )
                                        Text(
                                            text = "योजना पृष्ठ पर जाकर पात्रता जांचें और नया आवेदन दर्ज करें।",
                                            fontSize = 12.sp,
                                            color = Color(0xFF66756D)
                                        )
                                    }
                                }
                            }
                        } else {
                            items(applications) { appCase ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = appCase.schemeName,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldDeep
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = when (appCase.status) {
                                                    "Approved" -> Color(0xFFDCF3E8)
                                                    "Submitted" -> Color(0xFFE8F1FC)
                                                    else -> Color(0xFFFDF4E5)
                                                }
                                            ) {
                                                Text(
                                                    text = appCase.status,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (appCase.status) {
                                                        "Approved" -> StatusEligible
                                                        "Submitted" -> Color(0xFF1E40AF)
                                                        else -> Color(0xFFD99A32)
                                                    },
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "संदर्भ संख्या: ${appCase.referenceNumber} • आवेदन माध्यम: ${appCase.channel}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF66756D)
                                        )

                                        if (appCase.operatorNotes.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFFF9FBF9),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "ऑपरेटर टिप्पणी: ${appCase.operatorNotes}",
                                                    fontSize = 11.sp,
                                                    color = EmeraldDeep,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "स्थिति स्रोत: ${appCase.statusSource} • तारीख: ${appCase.submissionDate}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF88978F)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                CitizenTab.CENTRES -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Text(
                                text = "निकटतम सरकारी सहायता केंद्र (Nearby Centres)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeep
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ऑफलाइन आवेदन, दस्तावेज अपलोड व ई-केवाईसी हेतु निकटतम अधिकृत केंद्र:",
                                fontSize = 12.sp,
                                color = Color(0xFF66756D)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        items(centres) { centre ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = centre.name,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldDeep
                                                )
                                                if (centre.isVerified) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.Verified,
                                                        contentDescription = "Verified",
                                                        tint = EmeraldPrimary,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = centre.address,
                                                fontSize = 12.sp,
                                                color = Color(0xFF66756D)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EmeraldContainer
                                        ) {
                                            Text(
                                                text = "${centre.distanceKm} km",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "समय: ${centre.openingHours} • संपर्क: ${centre.contactPhone}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF66756D)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (centre.contactPhone.isNotBlank()) {
                                            Button(
                                                onClick = {
                                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${centre.contactPhone}"))
                                                    context.startActivity(intent)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("कॉल करें", fontSize = 11.sp)
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val uri = Uri.parse("geo:0,0?q=${Uri.encode(centre.name + ", " + centre.address)}")
                                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                                context.startActivity(mapIntent)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Directions, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("दिशा-निर्देश", fontSize = 11.sp, color = EmeraldPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Eligibility Breakdown Dialog
    selectedSchemeForEligibility?.let { scheme ->
        val evaluation = viewModel.evaluateScheme(scheme)
        EligibilityBreakdownDialog(
            scheme = scheme,
            evaluation = evaluation,
            onApplyTrack = {
                viewModel.createNewApplication(scheme, "", "Citizen Online")
                activeTab = CitizenTab.APPLICATIONS
            },
            onEditProfile = { isProfileDialogOpen = true },
            onDismiss = { selectedSchemeForEligibility = null }
        )
    }

    // Documents Checklist Dialog
    selectedSchemeForDocs?.let { scheme ->
        DocumentChecklistDialog(
            scheme = scheme,
            onDismiss = { selectedSchemeForDocs = null }
        )
    }

    // Profile Edit Dialog
    if (isProfileDialogOpen) {
        ProfileEditDialog(
            initialProfile = profile,
            onSaveProfile = { updated ->
                viewModel.updateProfile(updated)
            },
            onDismiss = { isProfileDialogOpen = false }
        )
    }
}
