package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.EligibilityEvaluationResult
import com.example.data.model.EligibilityStatus
import com.example.data.model.Scheme
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.StatusEligible
import com.example.ui.theme.StatusEligibleContainer
import com.example.ui.theme.StatusNeedInfo
import com.example.ui.theme.StatusNeedInfoContainer
import com.example.ui.theme.StatusNotMet
import com.example.ui.theme.StatusNotMetContainer

@Composable
fun EligibilityBreakdownDialog(
    scheme: Scheme,
    evaluation: EligibilityEvaluationResult,
    onApplyTrack: () -> Unit,
    onEditProfile: () -> Unit,
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val statusContainer = when (evaluation.status) {
        EligibilityStatus.POTENTIALLY_ELIGIBLE -> StatusEligibleContainer
        EligibilityStatus.MORE_INFO_NEEDED -> StatusNeedInfoContainer
        EligibilityStatus.CRITERIA_NOT_MET -> StatusNotMetContainer
    }

    val statusColor = when (evaluation.status) {
        EligibilityStatus.POTENTIALLY_ELIGIBLE -> StatusEligible
        EligibilityStatus.MORE_INFO_NEEDED -> StatusNeedInfo
        EligibilityStatus.CRITERIA_NOT_MET -> StatusNotMet
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "पारदर्शी पात्रता रिपोर्ट (Eligibility Report)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = scheme.schemeNameHindi,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Overall Evaluation Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (evaluation.status) {
                                EligibilityStatus.POTENTIALLY_ELIGIBLE -> Icons.Default.CheckCircle
                                EligibilityStatus.MORE_INFO_NEEDED -> Icons.Default.Help
                                EligibilityStatus.CRITERIA_NOT_MET -> Icons.Default.Cancel
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = evaluation.status.labelHindi,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = evaluation.summaryHindi,
                            fontSize = 13.sp,
                            color = EmeraldDeep,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "शर्तों का विस्तृत विश्लेषण (Criteria Breakdown):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDeep
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    // Satisfied
                    items(evaluation.satisfiedList) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Satisfied",
                                tint = StatusEligible,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = item.criterion.labelHindi,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = item.messageHindi,
                                    fontSize = 12.sp,
                                    color = StatusEligible
                                )
                            }
                        }
                    }

                    // Missing
                    items(evaluation.missingList) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Help,
                                contentDescription = "Missing",
                                tint = StatusNeedInfo,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = item.criterion.labelHindi,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = "प्रोफाइल में विवरण दर्ज करें।",
                                    fontSize = 12.sp,
                                    color = StatusNeedInfo
                                )
                            }
                        }
                    }

                    // Failed
                    items(evaluation.failedList) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Failed",
                                tint = StatusNotMet,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = item.criterion.labelHindi,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = item.messageHindi,
                                    fontSize = 12.sp,
                                    color = StatusNotMet
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Authority & Version Note
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F6F3),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "प्राधिकरण: ${scheme.sourceReference}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = EmeraldDeep
                        )
                        Text(
                            text = "सत्यापित संस्करण: v${evaluation.ruleVersion} • दिनांक: ${evaluation.lastVerifiedDate}",
                            fontSize = 10.sp,
                            color = Color(0xFF66756D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (evaluation.status == EligibilityStatus.MORE_INFO_NEEDED) {
                        Button(
                            onClick = {
                                onDismiss()
                                onEditProfile()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text("प्रोफाइल पूर्ण करें", fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                onDismiss()
                                onApplyTrack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text("आवेदन ट्रैक/दर्ज करें", fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            if (scheme.officialLink.isNotBlank()) {
                                try {
                                    uriHandler.openUri(scheme.officialLink)
                                } catch (_: Exception) {}
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("आधिकारिक पोर्टल", fontSize = 12.sp, color = EmeraldPrimary)
                    }
                }
            }
        }
    }
}
