package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CitizenProfile
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary

@Composable
fun ProfileEditDialog(
    initialProfile: CitizenProfile?,
    onSaveProfile: (CitizenProfile) -> Unit,
    onDismiss: () -> Unit
) {
    var fullName by remember { mutableStateOf(initialProfile?.fullName.orEmpty()) }
    var phoneNumber by remember { mutableStateOf(initialProfile?.phoneNumber.orEmpty()) }
    var state by remember { mutableStateOf(initialProfile?.state ?: "Bihar") }
    var district by remember { mutableStateOf(initialProfile?.district ?: "Patna") }
    var block by remember { mutableStateOf(initialProfile?.block ?: "Phulwari Sharif") }
    var panchayat by remember { mutableStateOf(initialProfile?.panchayat ?: "Nohsa") }
    var village by remember { mutableStateOf(initialProfile?.village ?: "Nohsa") }

    var ageText by remember { mutableStateOf(initialProfile?.age?.toString().orEmpty()) }
    var gender by remember { mutableStateOf(initialProfile?.gender ?: "Male") }
    var occupation by remember { mutableStateOf(initialProfile?.occupation ?: "Farmer") }
    var landText by remember { mutableStateOf(initialProfile?.landholdingAcres?.toString().orEmpty()) }
    var incomeText by remember { mutableStateOf(initialProfile?.householdIncome?.toString().orEmpty()) }
    var category by remember { mutableStateOf(initialProfile?.category ?: "OBC") }
    var hasDisability by remember { mutableStateOf(initialProfile?.hasDisability ?: false) }
    var hasBplCard by remember { mutableStateOf(initialProfile?.hasBplRationCard ?: false) }
    var consentGiven by remember { mutableStateOf(initialProfile?.consentGiven ?: true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 12.dp)
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
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "नागरिक प्रोफाइल विवरण (Citizen Profile)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Privacy Notice Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "गोपनीयता आश्वासन: यह जानकारी केवल योजना पात्रता जांचने हेतु है। हम आधार नंबर या बैंक पासवर्ड कभी नहीं मांगते।",
                            fontSize = 11.sp,
                            color = EmeraldDeep,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("पूरा नाम (Full Name)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_fullname_input"),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("मोबाइल नंबर (Mobile Number)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = { ageText = it },
                                label = { Text("आयु (Age)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_age_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = district,
                                onValueChange = { district = it },
                                label = { Text("जिला (District)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = block,
                                onValueChange = { block = it },
                                label = { Text("प्रखंड (Block)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = panchayat,
                                onValueChange = { panchayat = it },
                                label = { Text("पंचायत (Panchayat)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Text(
                            text = "लिंग (Gender):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldDeep
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Male" to "पुरुष", "Female" to "महिला", "Other" to "अन्य").forEach { (key, label) ->
                                FilterChip(
                                    selected = gender == key,
                                    onClick = { gender = key },
                                    label = { Text(label, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "व्यवसाय (Occupation):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldDeep
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Farmer" to "किसान", "Daily Wage Laborer" to "मजदूर", "Student" to "विद्यार्थी", "Small Business" to "दुकानदार").forEach { (key, label) ->
                                FilterChip(
                                    selected = occupation == key,
                                    onClick = { occupation = key },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = landText,
                                onValueChange = { landText = it },
                                label = { Text("भूमि एकड़ (Land Acres)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_land_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = incomeText,
                                onValueChange = { incomeText = it },
                                label = { Text("वार्षिक आय ₹ (Annual Income)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_income_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Text(
                            text = "सामाजिक वर्ग (Category):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldDeep
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("OBC", "General", "SC", "ST", "EWS").forEach { cat ->
                                FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(cat, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "राशन कार्ड / बीपीएल सूची में नाम",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = "खाद्य सुरक्षा या अंत्योदय योजना के तहत राशन कार्ड",
                                    fontSize = 11.sp,
                                    color = Color(0xFF66756D)
                                )
                            }
                            Switch(
                                checked = hasBplCard,
                                onCheckedChange = { hasBplCard = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "दिव्यांगजन प्रमाण पत्र",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = "40% या अधिक दिव्यांगता प्रमाण पत्र उपलब्ध है",
                                    fontSize = 11.sp,
                                    color = Color(0xFF66756D)
                                )
                            }
                            Switch(
                                checked = hasDisability,
                                onCheckedChange = { hasDisability = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = consentGiven,
                                onCheckedChange = { consentGiven = it },
                                colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "मैं सरकारी योजना पात्रता निर्धारण हेतु इस जानकारी के सत्यापन की सहमति देता/देती हूँ।",
                                fontSize = 11.sp,
                                color = EmeraldDeep,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val updated = (initialProfile ?: CitizenProfile()).copy(
                                fullName = fullName.ifBlank { "नागरिक" },
                                phoneNumber = phoneNumber,
                                state = state,
                                district = district,
                                block = block,
                                panchayat = panchayat,
                                village = village,
                                age = ageText.toIntOrNull(),
                                gender = gender,
                                occupation = occupation,
                                landholdingAcres = landText.toDoubleOrNull(),
                                householdIncome = incomeText.toIntOrNull(),
                                category = category,
                                hasDisability = hasDisability,
                                hasBplRationCard = hasBplCard,
                                consentGiven = consentGiven
                            )
                            onSaveProfile(updated)
                            onDismiss()
                        },
                        enabled = consentGiven && fullName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("save_profile_btn")
                    ) {
                        Text("प्रोफाइल सहेजें (Save)", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
