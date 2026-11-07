package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Jurisdiction
import com.example.data.model.OperatorAccount
import com.example.data.model.Scheme
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.StatusEligible
import com.example.ui.theme.StatusNotMet
import com.example.ui.theme.WarmAmber

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val schemes by viewModel.schemes.collectAsState()
    val operators by viewModel.operatorAccounts.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val sources by viewModel.governmentSources.collectAsState()
    val announcements by viewModel.discoveredAnnouncements.collectAsState()
    val scanRun by viewModel.latestScanRun.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val coverageStats by viewModel.nationalCoverageStats.collectAsState()

    var selectedTab by remember { mutableStateOf("intelligence") } // "intelligence", "schemes", "operators", "audit"
    var isAddOperatorDialogOpen by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Dedicated Super Admin Top Bar
        Surface(
            color = Color(0xFF0F2B20),
            contentColor = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_top_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(WarmAmber.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = WarmAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "राष्ट्रीय योजना प्रबंधन पोर्टल",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "सुपर एडमिन सत्र • सुरक्षा निगरानी सक्रिय",
                            fontSize = 11.sp,
                            color = Color(0xFFD2EBE0)
                        )
                    }
                }

                // Exit / Logout Button
                Surface(
                    onClick = { viewModel.exitStaffToCitizen() },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("admin_exit_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "लॉग आउट",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Navigation Tabs for Admin Workspace
        TabRow(
            selectedTabIndex = when (selectedTab) {
                "intelligence" -> 0
                "schemes" -> 1
                "operators" -> 2
                else -> 3
            },
            containerColor = EmeraldDeep,
            contentColor = Color.White,
            modifier = Modifier.testTag("admin_tab_row")
        ) {
            Tab(
                selected = selectedTab == "intelligence",
                onClick = { selectedTab = "intelligence" },
                text = { Text("राष्ट्रीय स्कैन (${sources.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("admin_tab_intelligence")
            )
            Tab(
                selected = selectedTab == "schemes",
                onClick = { selectedTab = "schemes" },
                text = { Text("योजनाएं (${schemes.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("admin_tab_schemes")
            )
            Tab(
                selected = selectedTab == "operators",
                onClick = { selectedTab = "operators" },
                text = { Text("ऑपरेटर (${operators.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("admin_tab_operators")
            )
            Tab(
                selected = selectedTab == "audit",
                onClick = { selectedTab = "audit" },
                text = { Text("ऑडिट", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("admin_tab_audit")
            )
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTab) {
                "intelligence" -> {
                    // National Overview Cards
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B20)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "ALL-INDIA SCHEME INTELLIGENCE",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WarmAmber
                                        )
                                        Text(
                                            text = "राष्ट्रीय सरकारी योजना निगरानी तंत्र",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    // Real-Time Trigger Scan Button
                                    Button(
                                        onClick = { viewModel.triggerNationalScan() },
                                        enabled = !isScanning,
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("admin_scan_now_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isScanning) "स्कैनिंग..." else "अब स्कैन करें",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // National Coverage Metrics Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = Color.White.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("सक्रिय स्रोत", fontSize = 10.sp, color = Color(0xFFD2EBE0))
                                            Text(
                                                "${coverageStats.activeSources} / ${coverageStats.totalConfiguredSources}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Surface(
                                        color = Color.White.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("राज्य व UT कवरेज", fontSize = 10.sp, color = Color(0xFFD2EBE0))
                                            Text(
                                                "${coverageStats.coveredStatesCount + coverageStats.coveredUtsCount} / 36",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Surface(
                                        color = Color.White.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("नई खोजें", fontSize = 10.sp, color = Color(0xFFD2EBE0))
                                            Text(
                                                "${announcements.size}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WarmAmber
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Newly Discovered Announcements Section Header
                    item {
                        Text(
                            text = "स्वतः खोजी गई नवीनतम योजनाएं व घोषणाएं (${announcements.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Render Discovered Announcements
                    items(announcements) { ann ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFE0F2FE)
                                    ) {
                                        Text(
                                            text = ann.classification,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0369A1),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "विश्वास स्कोर: ${(ann.confidenceScore * 100).toInt()}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EmeraldPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = ann.titleHindi.ifBlank { ann.title },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDeep
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = ann.rawSummary,
                                    fontSize = 11.sp,
                                    color = Color(0xFF5A6E63),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "स्रोत: ${ann.sourceName} • ${ann.jurisdictionState}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF88978F)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = "✓ स्वतः सत्यापित एवं लाइव",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Configured Sources Registry Header
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "पंजीकृत आधिकारिक सरकारी स्रोत (${sources.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Render Sources List
                    items(sources) { src ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = src.nameHindi.ifBlank { src.name },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDeep
                                    )
                                    Text(
                                        text = "${src.governmentLevel} • ${src.jurisdictionState} • ${src.sourceType}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF75867E)
                                    )
                                    Text(
                                        text = "अंतिम स्कैन: ${src.lastScanTimestamp} • कुल खोजें: ${src.totalDiscoveredCount}",
                                        fontSize = 10.sp,
                                        color = EmeraldPrimary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (src.healthStatus == "HEALTHY") Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                                ) {
                                    Text(
                                        text = if (src.healthStatus == "HEALTHY") "सक्रिय (200 OK)" else src.healthStatus,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (src.healthStatus == "HEALTHY") Color(0xFF15803D) else Color(0xFFB91C1C),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                "schemes" -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "योजना सत्यापन व प्रकाशन नियंत्रण",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = "केवल सुपर एडमिन द्वारा प्रकाशित योजनाएं ही नागरिकों को प्रदर्शित होती हैं।",
                                    fontSize = 11.sp,
                                    color = Color(0xFF4C6156)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    items(schemes) { scheme ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("admin_scheme_${scheme.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = scheme.schemeNameHindi,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldDeep
                                        )
                                        Text(
                                            text = scheme.schemeName,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (scheme.isPublished) "प्रकाशित" else "अप्रकाशित",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (scheme.isPublished) StatusEligible else StatusNotMet
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Switch(
                                            checked = scheme.isPublished,
                                            onCheckedChange = { checked ->
                                                viewModel.toggleSchemePublication(scheme.id, checked)
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = EmeraldPrimary
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "विभाग: ${scheme.department}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF55655D)
                                    )
                                    Text(
                                        text = "सत्यापित तिथि: ${scheme.lastVerifiedDate}",
                                        fontSize = 11.sp,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                "operators" -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "अधिकृत पंचायत ऑपरेटर सूची (${operators.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeep
                            )

                            Button(
                                onClick = { isAddOperatorDialogOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("admin_add_operator_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("नया ऑपरेटर जोड़ें", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(operators) { op ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("admin_op_${op.operatorId}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = op.fullName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldDeep
                                        )
                                        Text(
                                            text = "${op.employeeId} • ${op.email}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    // Status Badge
                                    Surface(
                                        color = when (op.status) {
                                            "ACTIVE" -> StatusEligible.copy(alpha = 0.15f)
                                            "SUSPENDED" -> WarmAmber.copy(alpha = 0.15f)
                                            else -> Color(0xFFFFEBEE)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = op.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (op.status) {
                                                "ACTIVE" -> StatusEligible
                                                "SUSPENDED" -> WarmAmber
                                                else -> Color(0xFFC74747)
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    color = EmeraldContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "क्षेत्राधिकार: ग्राम पंचायत ${op.jurisdiction.panchayat}, प्रखंड ${op.jurisdiction.block} (${op.jurisdiction.district})",
                                            fontSize = 11.sp,
                                            color = EmeraldDeep,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Action Buttons: Suspend / Activate / Deactivate
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (op.status == "ACTIVE") {
                                        OutlinedButton(
                                            onClick = { viewModel.toggleOperatorStatus(op.operatorId, "SUSPENDED") },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("निलंबित करें (Suspend)", fontSize = 11.sp, color = WarmAmber)
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.toggleOperatorStatus(op.operatorId, "ACTIVE") },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("सक्रिय करें (Activate)", fontSize = 11.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    OutlinedButton(
                                        onClick = { viewModel.toggleOperatorStatus(op.operatorId, "DEACTIVATED") },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("निष्क्रिय (Deactivate)", fontSize = 11.sp, color = Color(0xFFC74747))
                                    }
                                }
                            }
                        }
                    }
                }

                "audit" -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = EmeraldDeep,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "सुरक्षा ऑडिट लॉग (Immutable Security Audit Trail)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDeep
                                    )
                                }
                                Text(
                                    text = "सभी संवेदनशील प्रशासनिक क्रियाएं, ऑपरेटर प्रमाणीकरण व क्षेत्राधिकार परिवर्तन रिकॉर्ड किए जाते हैं।",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(auditLogs) { log ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.action,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDeep
                                    )
                                    Text(
                                        text = log.actorRole,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EmeraldPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = log.details,
                                    fontSize = 11.sp,
                                    color = Color(0xFF55655D)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Entity: ${log.targetEntity} • Actor: ${log.actorId}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Operator Dialog
    if (isAddOperatorDialogOpen) {
        var opName by remember { mutableStateOf("") }
        var opEmail by remember { mutableStateOf("") }
        var opPhone by remember { mutableStateOf("") }
        var opEmpId by remember { mutableStateOf("CSC-BR-PAT-") }
        var opPanchayat by remember { mutableStateOf("Phulwari_East") }
        var opBlock by remember { mutableStateOf("Phulwari Sharif") }
        var opDistrict by remember { mutableStateOf("Patna") }

        AlertDialog(
            onDismissRequest = { isAddOperatorDialogOpen = false },
            title = { Text("नया पंचायत ऑपरेटर अधिकृत करें", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = opName,
                        onValueChange = { opName = it },
                        label = { Text("पूरा नाम") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = opEmail,
                        onValueChange = { opEmail = it },
                        label = { Text("ऑपरेटर ईमेल") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = opEmpId,
                        onValueChange = { opEmpId = it },
                        label = { Text("कर्मचारी / CSC आईडी") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = opPanchayat,
                        onValueChange = { opPanchayat = it },
                        label = { Text("असाइन की गई ग्राम पंचायत (Jurisdiction)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (opName.isNotBlank() && opEmail.isNotBlank()) {
                            viewModel.addOperatorAccount(
                                fullName = opName,
                                email = opEmail,
                                phoneNumber = opPhone,
                                employeeId = opEmpId,
                                jurisdiction = Jurisdiction(
                                    state = "Bihar",
                                    district = opDistrict,
                                    block = opBlock,
                                    panchayat = opPanchayat
                                )
                            )
                            isAddOperatorDialogOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("अधिकृत करें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { isAddOperatorDialogOpen = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}
