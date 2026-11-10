package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EligibilityEvaluationResult
import com.example.data.model.EligibilityStatus
import com.example.data.model.Scheme
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.StatusEligible
import com.example.ui.theme.StatusEligibleContainer
import com.example.ui.theme.StatusNeedInfo
import com.example.ui.theme.StatusNeedInfoContainer
import com.example.ui.theme.StatusNotMet
import com.example.ui.theme.StatusNotMetContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SchemeCard(
    scheme: Scheme,
    evaluation: EligibilityEvaluationResult,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onCheckEligibility: () -> Unit,
    onViewDocuments: () -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    val badgeBg = when (evaluation.status) {
        EligibilityStatus.POTENTIALLY_ELIGIBLE -> StatusEligibleContainer
        EligibilityStatus.MORE_INFO_NEEDED -> StatusNeedInfoContainer
        EligibilityStatus.CRITERIA_NOT_MET -> StatusNotMetContainer
    }

    val badgeTextColor = when (evaluation.status) {
        EligibilityStatus.POTENTIALLY_ELIGIBLE -> StatusEligible
        EligibilityStatus.MORE_INFO_NEEDED -> StatusNeedInfo
        EligibilityStatus.CRITERIA_NOT_MET -> StatusNotMet
    }

    val badgeIcon = when (evaluation.status) {
        EligibilityStatus.POTENTIALLY_ELIGIBLE -> Icons.Default.CheckCircle
        EligibilityStatus.MORE_INFO_NEEDED -> Icons.Default.Help
        EligibilityStatus.CRITERIA_NOT_MET -> Icons.Default.Warning
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
            .clickable { onClick() }
            .testTag("scheme_card_${scheme.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Category, Gov Level & Save Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Category Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F6F3),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = scheme.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Level Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE9F0FD),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = scheme.governmentLevel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E40AF),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Jurisdiction Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (scheme.applicableState == "All India") Color(0xFFFFF7ED) else Color(0xFFF3E8FF),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = if (scheme.applicableState == "All India") "🇮🇳 All India (राष्ट्रीय)" else "📍 ${scheme.applicableState}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (scheme.applicableState == "All India") Color(0xFFC2410C) else Color(0xFF6B21A8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("save_scheme_btn_${scheme.id}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save Scheme",
                        tint = if (isSaved) Color(0xFFE59819) else Color(0xFF88978F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Scheme Titles
            Text(
                text = scheme.schemeNameHindi,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldDeep,
                lineHeight = 22.sp
            )

            Text(
                text = scheme.schemeName,
                fontSize = 12.sp,
                color = Color(0xFF66756D),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Benefit Summary Callout Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EmeraldContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "लाभ (Benefit): ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                    Text(
                        text = scheme.benefitSummaryHindi,
                        fontSize = 12.sp,
                        color = EmeraldDeep,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Eligibility Status Badge (Deterministic from engine!)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = badgeBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = null,
                        tint = badgeTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = evaluation.status.labelHindi,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${evaluation.satisfiedList.size}/${scheme.criteria.size} शर्तें पूर्ण",
                        fontSize = 11.sp,
                        color = badgeTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Verified Source Line
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (scheme.officialLink.isNotBlank()) {
                            try {
                                uriHandler.openUri(scheme.officialLink)
                            } catch (_: Exception) {}
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified Source",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${scheme.department} • सत्यापित (v${scheme.version})",
                    fontSize = 11.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open Link",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(13.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCheckEligibility,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("check_eligibility_btn_${scheme.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "पात्रता विवरण", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onViewDocuments,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("view_docs_btn_${scheme.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "दस्तावेज सूची", fontSize = 12.sp, color = EmeraldPrimary)
                }
            }
        }
    }
}
