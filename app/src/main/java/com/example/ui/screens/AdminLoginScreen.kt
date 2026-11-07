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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
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
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftBackground
import com.example.ui.theme.WarmAmber

@Composable
fun AdminLoginScreen(
    viewModel: MainViewModel,
    onBackToCitizen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var adminKey by remember { mutableStateOf("Admin@2026!") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDeep)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_login_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                        modifier = Modifier.testTag("admin_login_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Citizen App",
                            tint = EmeraldDeep
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "राष्ट्रीय योजना प्रबंधन पोर्टल",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(Color(0xFFFFF3E0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = WarmAmber,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "सुपर एडमिन प्रमाणीकरण",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDeep
                )

                Text(
                    text = "यह पोर्टल केवल राष्ट्रीय और राज्य लोक कल्याण प्रशासकों के लिए आरक्षित है। बिना अनुमति प्रवेश निषिद्ध है।",
                    fontSize = 12.sp,
                    color = Color(0xFF66756D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                // Error Message
                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .testTag("admin_login_error_banner")
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

                // Security Passkey Input
                OutlinedTextField(
                    value = adminKey,
                    onValueChange = { adminKey = it; errorMessage = null },
                    label = { Text("प्रशासक सुरक्षा पासकी (Security Passkey)") },
                    leadingIcon = {
                        Icon(Icons.Default.Key, contentDescription = null, tint = WarmAmber)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmAmber,
                        unfocusedBorderColor = Color(0xFFCBD5D0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_passkey_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Enter Admin Portal Button
                Button(
                    onClick = {
                        val result = viewModel.loginAdmin(adminKey)
                        if (result.isFailure) {
                            errorMessage = result.exceptionOrNull()?.message ?: "Access Denied"
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_submit_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("एडमिन पोर्टल में प्रवेश करें", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cancel Button
                OutlinedButton(
                    onClick = onBackToCitizen,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("admin_cancel_btn")
                ) {
                    Text("नागरिक पोर्टल पर वापस जाएं", color = EmeraldDeep)
                }
            }
        }
    }
}
