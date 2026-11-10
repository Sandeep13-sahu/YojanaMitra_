package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.ui.theme.WarmAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SchemeDetailDialog(
    scheme: Scheme,
    evaluation: EligibilityEvaluationResult,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onApplyScheme: (Scheme) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("विवरण (Overview)", "पात्रता नियम (Eligibility)", "दस्तावेज (Documents)", "आवेदन विधि (Apply)")

    val documentCheckedState = remember { mutableStateMapOf<String, Boolean>() }

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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("scheme_detail_dialog_${scheme.id}")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldDeep)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = WarmAmber,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "योजना का संपूर्ण विवरण",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onSaveToggle,
                            modifier = Modifier.testTag("detail_save_btn")
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isSaved) WarmAmber else Color.White
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Scheme Primary Title & Badges
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldContainer
                        ) {
                            Text(
                                text = scheme.category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeep,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE9F0FD)
                        ) {
                            Text(
                                text = scheme.governmentLevel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E40AF),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (scheme.applicableState == "All India") Color(0xFFFFF7ED) else Color(0xFFF3E8FF)
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = scheme.schemeNameHindi,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep,
                        lineHeight = 24.sp
                    )
                    Text(
                        text = scheme.schemeName,
                        fontSize = 13.sp,
                        color = Color(0xFF5A6B62),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Eligibility Evaluation Status Strip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = badgeBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = badgeIcon,
                                contentDescription = null,
                                tint = badgeTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = evaluation.status.labelHindi,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${evaluation.satisfiedList.size}/${scheme.criteria.size} शर्ते पूर्ण)",
                                fontSize = 12.sp,
                                color = badgeTextColor
                            )
                        }
                    }
                }

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF3F7F5),
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = EmeraldPrimary,
                            height = 3.dp
                        )
                    },
                    edgePadding = 12.dp
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) EmeraldDeep else Color(0xFF64746B)
                                )
                            }
                        )
                    }
                }

                // Tab Content Body
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        // 0: Overview
                        0 -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                item {
                                    Text(
                                        text = "लाभ (Benefit Summary)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = EmeraldDeep
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = EmeraldContainer,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = scheme.benefitSummaryHindi.ifBlank { scheme.benefitSummary },
                                            fontSize = 13.sp,
                                            color = EmeraldDeep,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(12.dp),
                                            lineHeight = 20.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "योजना का संक्षिप्त विवरण (Description)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = EmeraldDeep
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = scheme.shortDescriptionHindi.ifBlank { scheme.shortDescription },
                                        fontSize = 13.sp,
                                        color = Color(0xFF4A5A52),
                                        lineHeight = 21.sp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "प्रशासनिक विवरण (Administration)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = EmeraldDeep
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    DetailItem(label = "विभाग / मंत्रालय", value = scheme.department)
                                    DetailItem(label = "सरकारी स्तर", value = scheme.governmentLevel)
                                    DetailItem(label = "सत्यापन तिथि", value = scheme.lastVerifiedDate)
                                    DetailItem(label = "नियम संस्करण", value = "v${scheme.version}")
                                    DetailItem(label = "स्रोत संदर्भ", value = scheme.sourceReference)
                                }
                            }
                        }

                        // 1: Eligibility Rules Breakdown
                        1 -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                item {
                                    Text(
                                        text = "आपकी प्रोफ़ाइल के अनुसार पात्रता समीक्षा",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = EmeraldDeep
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = evaluation.summaryHindi,
                                        fontSize = 13.sp,
                                        color = Color(0xFF55665D),
                                        lineHeight = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                }

                                if (evaluation.satisfiedList.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "✓ पूर्ण की गई शर्तें (${evaluation.satisfiedList.size})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = StatusEligible
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                    items(evaluation.satisfiedList) { item ->
                                        EvaluationCriterionCard(item.criterion.labelHindi, item.messageHindi, StatusEligibleContainer, StatusEligible)
                                    }
                                    item { Spacer(modifier = Modifier.height(12.dp)) }
                                }

                                if (evaluation.missingList.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "? अधिक जानकारी आवश्यक (${evaluation.missingList.size})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = StatusNeedInfo
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                    items(evaluation.missingList) { item ->
                                        EvaluationCriterionCard(item.criterion.labelHindi, item.messageHindi, StatusNeedInfoContainer, StatusNeedInfo)
                                    }
                                    item { Spacer(modifier = Modifier.height(12.dp)) }
                                }

                                if (evaluation.failedList.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "✕ अपूर्ण शर्तें (${evaluation.failedList.size})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = StatusNotMet
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                    items(evaluation.failedList) { item ->
                                        EvaluationCriterionCard(item.criterion.labelHindi, item.messageHindi, StatusNotMetContainer, StatusNotMet)
                                    }
                                }
                            }
                        }

                        // 2: Documents Checklist
                        2 -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                item {
                                    Text(
                                        text = "आवेदन हेतु आवश्यक दस्तावेज",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = EmeraldDeep
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "आवेदन करने से पूर्व निम्न दस्तावेजों की मूल और छायाप्रति तैयार रखें:",
                                        fontSize = 12.sp,
                                        color = Color(0xFF65756C)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                items(scheme.requiredDocuments) { doc ->
                                    val isChecked = documentCheckedState[doc.titleHindi] ?: false
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = if (isChecked) Color(0xFFF1F8F4) else Color(0xFFFAFAFA)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isChecked) EmeraldPrimary else Color(0xFFE2EBE5)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                documentCheckedState[doc.titleHindi] = !isChecked
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    documentCheckedState[doc.titleHindi] = checked
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = doc.titleHindi,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = EmeraldDeep
                                                    )
                                                    if (doc.isMandatory) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "(अनिवार्य)",
                                                            fontSize = 11.sp,
                                                            color = Color(0xFFC62828),
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                                if (doc.titleEnglish.isNotBlank()) {
                                                    Text(
                                                        text = doc.titleEnglish,
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF75857B)
                                                    )
                                                }
                                                if (doc.description.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = doc.description,
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF4A5A52)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3: Application Procedure & Helpline
                        3 -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                item {
                                    Text(
                                        text = "आवेदन की विधि (Step-by-Step Procedure)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = EmeraldDeep
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    ProcedureStep(
                                        stepNum = 1,
                                        title = "दस्तावेज तैयार करें (Gather Documents)",
                                        desc = "दस्तावेज टैब में दिए गए सभी अनिवार्य कागजात (आधार, बैंक पासबुक, आय/भूमि प्रमाण) तैयार रखें।"
                                    )
                                    ProcedureStep(
                                        stepNum = 2,
                                        title = "ऑनलाइन अथवा सीएससी केंद्र पर जाएं",
                                        desc = "आप आधिकारिक सरकारी पोर्टल पर ऑनलाइन आवेदन कर सकते हैं अथवा निकटतम कॉमन सर्विस सेंटर (CSC) या पंचायत भवन पर जाकर ऑपरेटर की सहायता ले सकते हैं।"
                                    )
                                    ProcedureStep(
                                        stepNum = 3,
                                        title = "आवेदन संदर्भ संख्या (Reference No) प्राप्त करें",
                                        desc = "आवेदन जमा करने के बाद पावती पर्ची लें और संदर्भ संख्या से YojanaMitra में आवेदन ट्रैक करें।"
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Official Link Button
                                    if (scheme.officialLink.isNotBlank()) {
                                        Button(
                                            onClick = {
                                                try {
                                                    uriHandler.openUri(scheme.officialLink)
                                                } catch (_: Exception) {}
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .testTag("open_official_portal_btn")
                                        ) {
                                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "आधिकारिक सरकारी पोर्टल खोलें",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    // Help & Support Card
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F6F3)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Call, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "सहायता और हेल्पलाइन (Helpline)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = EmeraldDeep
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "टोल फ्री राष्ट्रीय हेल्पलाइन: 1800-11-5526\nस्थानीय सीएससी संपर्क: 14599",
                                                fontSize = 12.sp,
                                                color = Color(0xFF3B4B42),
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFECEFF1))

                // Bottom Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("बंद करें", color = EmeraldDeep)
                    }

                    Button(
                        onClick = {
                            onApplyScheme(scheme)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                            .testTag("start_apply_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "आवेदन ट्रैक में जोड़ें",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF75857B))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldDeep)
    }
}

@Composable
private fun EvaluationCriterionCard(title: String, desc: String, bg: Color, textCol: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bg,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = textCol)
            if (desc.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = desc, fontSize = 11.sp, color = textCol)
            }
        }
    }
}

@Composable
private fun ProcedureStep(stepNum: Int, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$stepNum",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldDeep)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 12.sp, color = Color(0xFF55655D), lineHeight = 18.sp)
        }
    }
}
