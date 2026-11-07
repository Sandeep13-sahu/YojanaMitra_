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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftBackground
import com.example.ui.theme.WarmAmber

@Composable
fun OperatorLoginScreen(
    viewModel: MainViewModel,
    onBackToCitizen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var employeeId by remember { mutableStateOf("CSC-BR-PAT-042") }
    var passcode by remember { mutableStateOf("1234") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SoftBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("operator_login_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar with Back Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackToCitizen,
                        modifier = Modifier.testTag("operator_login_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Citizen App",
                            tint = EmeraldDeep
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CSC / ग्राम पंचायत ऑपरेटर",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(EmeraldContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupervisorAccount,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ऑपरेटर कार्यस्थल प्रवेश",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDeep
                )

                Text(
                    text = "यह पोर्टल केवल सुपर एडमिन द्वारा अधिकृत पंचायत ऑपरेटरों के लिए आरक्षित है।",
                    fontSize = 12.sp,
                    color = Color(0xFF66756D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Error Banner
                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .testTag("operator_login_error_banner")
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFFC74747),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Employee ID or Operator Email
                OutlinedTextField(
                    value = employeeId,
                    onValueChange = { employeeId = it; errorMessage = null },
                    label = { Text("कर्मचारी आईडी / ऑपरेटर ईमेल") },
                    leadingIcon = {
                        Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5D0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("operator_id_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Passcode
                OutlinedTextField(
                    value = passcode,
                    onValueChange = { passcode = it; errorMessage = null },
                    label = { Text("सुरक्षा पासकोड (PIN)") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5D0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("operator_passcode_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Login Submit Button
                Button(
                    onClick = {
                        val result = viewModel.loginOperator(employeeId, passcode)
                        if (result.isFailure) {
                            errorMessage = result.exceptionOrNull()?.message ?: "Authorization failed"
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("operator_login_submit_btn")
                ) {
                    Text("ऑपरेटर लॉगिन करें", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cancel Button
                OutlinedButton(
                    onClick = onBackToCitizen,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("operator_cancel_btn")
                ) {
                    Text("नागरिक पोर्टल पर वापस जाएं", color = EmeraldDeep)
                }
            }
        }
    }
}
