package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Scheme
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary

@Composable
fun DocumentChecklistDialog(
    scheme: Scheme,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val readyMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            scheme.requiredDocuments.forEach { doc ->
                put(doc.titleEnglish, false)
            }
        }
    }

    val readyCount = readyMap.values.count { it }
    val totalCount = scheme.requiredDocuments.size
    val progress = if (totalCount > 0) readyCount.toFloat() / totalCount.toFloat() else 0f

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "आवश्यक दस्तावेज चेकलिस्ट",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeep
                            )
                            Text(
                                text = scheme.schemeNameHindi,
                                fontSize = 12.sp,
                                color = Color(0xFF66756D)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "दस्तावेज तैयारी प्रगति:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldDeep
                            )
                            Text(
                                text = "$readyCount / $totalCount तैयार",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            color = EmeraldPrimary,
                            trackColor = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "आवेदन से पूर्व इन दस्तावेजों को तैयार रखें:",
                    fontSize = 12.sp,
                    color = Color(0xFF66756D)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(scheme.requiredDocuments) { doc ->
                        val isChecked = readyMap[doc.titleEnglish] ?: false
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChecked) Color(0xFFEBF7F1) else Color(0xFFF9FBF9)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        readyMap[doc.titleEnglish] = checked
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = doc.titleHindi,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldDeep
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        if (doc.isMandatory) {
                                            Surface(
                                                color = Color(0xFFFFECEC),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "अनिवार्य",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFC74747),
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    if (doc.description.isNotBlank()) {
                                        Text(
                                            text = doc.description,
                                            fontSize = 11.sp,
                                            color = Color(0xFF66756D)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action: Share Checklist
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareBody = buildString {
                                appendLine("📋 ${scheme.schemeNameHindi} - आवश्यक दस्तावेज सूची:")
                                scheme.requiredDocuments.forEachIndexed { i, d ->
                                    val mark = if (readyMap[d.titleEnglish] == true) "✅" else "⬜"
                                    appendLine("$mark ${i + 1}. ${d.titleHindi} (${d.description})")
                                }
                                appendLine("\nअधिक जानकारी हेतु संपर्क करें: YojanaMitra")
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "दस्तावेज चेकलिस्ट - ${scheme.schemeName}")
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                            }
                            context.startActivity(Intent.createChooser(intent, "चेकलिस्ट साझा करें"))
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("सूची शेयर करें", fontSize = 12.sp, color = EmeraldPrimary)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("सम्पन्न (Done)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
