package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppLanguage
import com.example.data.model.CitizenProfile
import com.example.ui.MainViewModel
import com.example.ui.WorkspaceView
import com.example.ui.components.ProfileEditDialog
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftBackground
import com.example.ui.theme.WarmAmber
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val currentUser = Firebase.auth.currentUser

    val isHindi = language == AppLanguage.HINDI || language == AppLanguage.BHOJPURI || language == AppLanguage.MAITHILI

    var isEditDialogOpen by remember { mutableStateOf(false) }
    var isLogoutConfirmOpen by remember { mutableStateOf(false) }
    var staffTapCount by remember { mutableIntStateOf(0) }
    var showStaffGatewayDialog by remember { mutableStateOf(false) }

    // Android Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateProfilePhoto(uri.toString())
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("citizen_profile_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "नागरिक प्रोफ़ाइल (Profile)" else "Citizen Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("profile_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isEditDialogOpen = true },
                        modifier = Modifier.testTag("profile_edit_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDeep)
            )
        },
        containerColor = SoftBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Citizen Avatar Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar with Edit Overlay
                        Box(contentAlignment = Alignment.BottomEnd) {
                            val photoUrl = profile?.photoUrl ?: currentUser?.photoUrl?.toString()

                            if (!photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, EmeraldPrimary, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldContainer)
                                        .border(2.dp, EmeraldPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Avatar",
                                        tint = EmeraldDeep,
                                        modifier = Modifier.size(54.dp)
                                    )
                                }
                            }

                            // Camera / Photo Picker Button
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary,
                                shadowElevation = 3.dp,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("change_photo_btn")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Change Photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Full Name
                        Text(
                            text = profile?.fullName?.ifBlank { currentUser?.displayName.orEmpty() } ?: "सत्यापित नागरिक (Verified Citizen)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Verified Citizen Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHindi) "सत्यापित नागरिक खाता" else "Verified Citizen Account",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Contact Details Row
                        if (!currentUser?.email.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF6B7C72), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = currentUser.email.orEmpty(), fontSize = 12.sp, color = Color(0xFF6B7C72))
                            }
                        }

                        if (!profile?.phoneNumber.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF6B7C72), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = profile?.phoneNumber.orEmpty(), fontSize = 12.sp, color = Color(0xFF6B7C72))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Edit Button
                        Button(
                            onClick = { isEditDialogOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("edit_profile_main_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "विवरण संपादित करें (Edit Profile)" else "Edit Profile Details",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Location & Address Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "निवास क्षेत्र (Jurisdiction & Location)" else "Residence & Jurisdiction",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldDeep
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInfoRow(label = if (isHindi) "राज्य (State)" else "State", value = profile?.state ?: "Bihar")
                        ProfileInfoRow(label = if (isHindi) "ज़िला (District)" else "District", value = profile?.district ?: "Patna")
                        ProfileInfoRow(label = if (isHindi) "प्रखंड (Block)" else "Block", value = profile?.block?.ifBlank { "Phulwari Sharif" } ?: "Phulwari Sharif")
                        ProfileInfoRow(label = if (isHindi) "पंचायत (Panchayat)" else "Panchayat", value = profile?.panchayat?.ifBlank { "Nohsa" } ?: "Nohsa")
                        ProfileInfoRow(label = if (isHindi) "गाँव / वार्ड (Village/Ward)" else "Village / Ward", value = profile?.village?.ifBlank { "Nohsa" } ?: "Nohsa", isLast = true)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Socio-Economic & Eligibility Attributes Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "पात्रता कारक (Eligibility Factors)" else "Eligibility Criteria",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldDeep
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInfoRow(label = if (isHindi) "आयु (Age)" else "Age", value = "${profile?.age ?: 42} वर्ष")
                        ProfileInfoRow(label = if (isHindi) "लिंग (Gender)" else "Gender", value = profile?.gender ?: "Male")
                        ProfileInfoRow(label = if (isHindi) "व्यवसाय (Occupation)" else "Occupation", value = profile?.occupation ?: "Farmer")
                        ProfileInfoRow(label = if (isHindi) "सामाजिक श्रेणी (Category)" else "Social Category", value = profile?.category ?: "OBC")
                        ProfileInfoRow(
                            label = if (isHindi) "कृषि भूमि (Landholding)" else "Landholding",
                            value = "${profile?.landholdingAcres ?: 2.0} एकड़ (Acres)"
                        )
                        ProfileInfoRow(
                            label = if (isHindi) "वार्षिक आय (Annual Income)" else "Annual Income",
                            value = "₹${profile?.householdIncome ?: 120000}"
                        )
                        ProfileInfoRow(
                            label = if (isHindi) "बीपीएल राशन कार्ड (BPL Card)" else "BPL Ration Card",
                            value = if (profile?.hasBplRationCard == true) "हाँ (उपलब्ध)" else "नहीं"
                        )
                        ProfileInfoRow(
                            label = if (isHindi) "दिव्यांगजन (Disability)" else "Person with Disability",
                            value = if (profile?.hasDisability == true) "हाँ" else "नहीं",
                            isLast = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Logout Action Button
            item {
                OutlinedButton(
                    onClick = { isLogoutConfirmOpen = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFC62828)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "लॉगआउट करें (Sign Out)" else "Sign Out",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Security & Privacy Notice (Discrete Staff Access Gate)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "YojanaMitra • भारत सरकार व राज्य सरकार योजना मंच",
                        fontSize = 11.sp,
                        color = Color(0xFF889890)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Version 1.1 (Build 2) • डेटा पूर्णतः सुरक्षित",
                        fontSize = 11.sp,
                        color = Color(0xFFA0B0A8),
                        modifier = Modifier.clickable {
                            staffTapCount++
                            if (staffTapCount >= 5) {
                                staffTapCount = 0
                                showStaffGatewayDialog = true
                            }
                        }
                    )
                }
            }
        }

        // Edit Profile Dialog
        if (isEditDialogOpen) {
            ProfileEditDialog(
                initialProfile = profile,
                onSaveProfile = { updated ->
                    viewModel.updateProfile(updated)
                    isEditDialogOpen = false
                },
                onDismiss = { isEditDialogOpen = false }
            )
        }

        // Logout Confirmation Dialog
        if (isLogoutConfirmOpen) {
            AlertDialog(
                onDismissRequest = { isLogoutConfirmOpen = false },
                title = {
                    Text(
                        text = if (isHindi) "लॉगआउट की पुष्टि (Confirm Logout)" else "Confirm Logout",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                },
                text = {
                    Text(
                        text = if (isHindi) "क्या आप वाकई YojanaMitra खाते से लॉगआउट करना चाहते हैं?" else "Are you sure you want to sign out of YojanaMitra?",
                        fontSize = 14.sp,
                        color = Color(0xFF55655D)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isLogoutConfirmOpen = false
                            viewModel.signOutCitizen()
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        modifier = Modifier.testTag("confirm_logout_btn")
                    ) {
                        Text(if (isHindi) "हाँ, लॉगआउट करें" else "Yes, Sign Out")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isLogoutConfirmOpen = false }) {
                        Text(if (isHindi) "रद्द करें" else "Cancel")
                    }
                }
            )
        }

        // Intentional Staff Gateway (Discreetly Gated for Operators/Admins)
        if (showStaffGatewayDialog) {
            AlertDialog(
                onDismissRequest = { showStaffGatewayDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("विभागीय कार्मिक पोर्टल (Staff Gateway)")
                    }
                },
                text = {
                    Text("यह प्रवेश केवल अधिकृत पंचायत ऑपरेटर एवं सुपर एडमिन के लिए है।", fontSize = 13.sp)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showStaffGatewayDialog = false
                            viewModel.navigateToWorkspace(WorkspaceView.OPERATOR_LOGIN)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("ऑपरेटर लॉगिन")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showStaffGatewayDialog = false
                            viewModel.navigateToWorkspace(WorkspaceView.ADMIN_LOGIN)
                        }
                    ) {
                        Text("एडमिन लॉगिन")
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
    isLast: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color(0xFF6B7C72),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 13.sp,
                color = EmeraldDeep,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        }
        if (!isLast) {
            HorizontalDivider(color = Color(0xFFF0F4F2), thickness = 1.dp)
        }
    }
}
