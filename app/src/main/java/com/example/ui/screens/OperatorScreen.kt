package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationCase
import com.example.data.model.CitizenProfile
import com.example.ui.MainViewModel
import com.example.ui.components.ProfileEditDialog
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.StatusEligible
import com.example.ui.theme.StatusNeedInfo
import com.example.ui.theme.WarmAmber

@Composable
fun OperatorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentOperator by viewModel.currentOperator.collectAsState()
    val applications by viewModel.jurisdictionCases.collectAsState()
    val schemes by viewModel.schemes.collectAsState()

    var isRegisterDialogOpen by remember { mutableStateOf(false) }
    var selectedCaseForNote by remember { mutableStateOf<ApplicationCase?>(null) }
    var noteText by remember { mutableStateOf("") }
    var statusMenuCaseId by remember { mutableStateOf<String?>(null) }

    val operatorName = currentOperator?.fullName ?: "रमेश कुमार (Ramesh Kumar)"
    val employeeId = currentOperator?.employeeId ?: "CSC-BR-PAT-042"
    val jurisdictionPanchayat = currentOperator?.jurisdiction?.panchayat ?: "Nohsa"
    val jurisdictionDistrict = currentOperator?.jurisdiction?.district ?: "Patna"

    Column(modifier = modifier.fillMaxSize()) {
        // Protected Operator Top Bar
        Surface(
            color = EmeraldDeep,
            contentColor = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("operator_top_bar")
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
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = null,
                            tint = WarmAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CSC / ऑपरेटर कार्यस्थल",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "$operatorName • $employeeId",
                            fontSize = 11.sp,
                            color = Color(0xFFD2EBE0)
                        )
                    }
                }

                // Exit to Citizen Portal Button
                Surface(
                    onClick = { viewModel.exitStaffToCitizen() },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("operator_exit_btn")
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Jurisdiction Badge & Notice Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("operator_jurisdiction_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "अधिकृत क्षेत्राधिकार (Assigned Jurisdiction):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeep
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ग्राम पंचायत: $jurisdictionPanchayat • जिला: $jurisdictionDistrict • स्थिति: सक्रिय (Active)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "सुरक्षा नीति: डेटा पृथक्करण सक्रिय है। आप केवल अपनी ग्राम पंचायत ($jurisdictionPanchayat) के आवेदन देख व सत्यापित कर सकते हैं।",
                            fontSize = 11.sp,
                            color = Color(0xFF4C6156)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Assisted Walk-in Registration Button
                Button(
                    onClick = { isRegisterDialogOpen = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("operator_register_citizen_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = EmeraldDeep,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "नए नागरिक का पंजीकरण व सहमति लें (Walk-in Citizen)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Overview Metrics in this Jurisdiction
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "क्षेत्रीय आवेदन", fontSize = 11.sp, color = Color(0xFF66756D))
                            Text(
                                text = "${applications.size}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeep
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "दस्तावेज लंबित", fontSize = 11.sp, color = Color(0xFF66756D))
                            Text(
                                text = "${applications.count { it.status == "Preparing Documents" }}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "स्वीकृत योजनाएं", fontSize = 11.sp, color = Color(0xFF66756D))
                            Text(
                                text = "${applications.count { it.status == "Approved" }}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusEligible
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "$jurisdictionPanchayat पंचायत के आवेदन (${applications.size}):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDeep
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // List of Jurisdiction-Isolated Cases
            items(applications) { caseItem ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("operator_case_${caseItem.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = caseItem.citizenName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = caseItem.schemeName,
                                    fontSize = 12.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Status Tag
                            Surface(
                                color = when (caseItem.status) {
                                    "Approved" -> StatusEligible.copy(alpha = 0.15f)
                                    "Submitted" -> EmeraldContainer
                                    else -> StatusNeedInfo.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = caseItem.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (caseItem.status) {
                                        "Approved" -> StatusEligible
                                        "Submitted" -> EmeraldPrimary
                                        else -> StatusNeedInfo
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "रिफरेंस: ${caseItem.referenceNumber}",
                                fontSize = 11.sp,
                                color = Color(0xFF66756D)
                            )
                            Text(
                                text = "पंचायत: ${caseItem.panchayat}",
                                fontSize = 11.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (caseItem.operatorNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = Color(0xFFF9FCFA),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "नोट: ${caseItem.operatorNotes}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF4C6156),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions: Update Status & Add Note
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    selectedCaseForNote = caseItem
                                    noteText = caseItem.operatorNotes
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("नोट जोड़ें", fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box {
                                Button(
                                    onClick = { statusMenuCaseId = caseItem.id },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("स्थिति बदलें", fontSize = 11.sp)
                                }

                                DropdownMenu(
                                    expanded = statusMenuCaseId == caseItem.id,
                                    onDismissRequest = { statusMenuCaseId = null }
                                ) {
                                    listOf("Preparing Documents", "Submitted", "Under Review", "Approved", "Rejected").forEach { newStatus ->
                                        DropdownMenuItem(
                                            text = { Text(newStatus, fontSize = 12.sp) },
                                            onClick = {
                                                viewModel.updateApplicationStatus(caseItem.id, newStatus)
                                                statusMenuCaseId = null
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Citizen Registration Dialog
    if (isRegisterDialogOpen) {
        ProfileEditDialog(
            initialProfile = CitizenProfile(
                userId = "cit_${System.currentTimeMillis()}",
                fullName = "",
                panchayat = jurisdictionPanchayat,
                district = jurisdictionDistrict,
                consentGiven = true
            ),
            onDismiss = { isRegisterDialogOpen = false },
            onSaveProfile = { newProfile ->
                val targetScheme = schemes.firstOrNull()
                if (targetScheme != null) {
                    viewModel.createNewApplication(
                        scheme = targetScheme,
                        referenceNumber = "CSC-REG-${(1000..9999).random()}",
                        channel = "CSC Center $jurisdictionPanchayat"
                    )
                }
                isRegisterDialogOpen = false
            }
        )
    }

    // Add Note Dialog
    selectedCaseForNote?.let { targetCase ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { selectedCaseForNote = null },
            title = { Text("सत्यापन नोट दर्ज करें", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "${targetCase.citizenName} • ${targetCase.schemeName}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("ऑपरेटर टिप्पणी") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateApplicationStatus(targetCase.id, targetCase.status, noteText)
                        selectedCaseForNote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("सहेजें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedCaseForNote = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}
