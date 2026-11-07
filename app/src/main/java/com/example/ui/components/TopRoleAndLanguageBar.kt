package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.UserRole
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.WarmAmber

/**
 * TopRoleAndLanguageBar
 *
 * A persistent top bar UI component in the main activity layout featuring:
 * 1. Brand identity, title, and status badges.
 * 2. Dedicated tab-based role switcher toggling between Citizen, Panchayat Operator, and Admin modes.
 * 3. Compact dropdown-based role switcher for alternate navigation.
 * 4. Language selector dropdown supporting Hindi, English, Bhojpuri, and Maithili.
 * 5. User account / sign-out controls.
 */
@Composable
fun TopRoleAndLanguageBar(
    currentRole: UserRole,
    currentLanguage: AppLanguage,
    onRoleSelected: (UserRole) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    userEmail: String? = null,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var roleDropdownExpanded by remember { mutableStateOf(false) }
    var languageDropdownExpanded by remember { mutableStateOf(false) }
    var userMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = EmeraldDeep,
        contentColor = Color.White,
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("persistent_top_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Top Primary Header Row: Brand Logo, App Title, Language & Profile/Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Emblem and Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.yojanamitra_icon_1793970169334),
                            contentDescription = "YojanaMitra Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "YojanaMitra",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Verified Service",
                                tint = WarmAmber,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "आपका सरकारी योजना साथी",
                            fontSize = 10.sp,
                            color = Color(0xFFD2EBE0)
                        )
                    }
                }

                // Controls Row: Language Switcher, Role Dropdown, and User Menu
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Language Dropdown Selector
                    Box {
                        Surface(
                            onClick = { languageDropdownExpanded = true },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("language_selector_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentLanguage.displayName.substringBefore(" "),
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = languageDropdownExpanded,
                            onDismissRequest = { languageDropdownExpanded = false }
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = lang.displayName,
                                                fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal,
                                                color = if (lang == currentLanguage) EmeraldDeep else Color.Unspecified
                                            )
                                            if (lang == currentLanguage) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = EmeraldPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onLanguageSelected(lang)
                                        languageDropdownExpanded = false
                                    },
                                    modifier = Modifier.testTag("lang_option_${lang.code}")
                                )
                            }
                        }
                    }

                    // Role Dropdown Menu Button (alternate to tabs for compact testing)
                    Box {
                        Surface(
                            onClick = { roleDropdownExpanded = true },
                            shape = RoundedCornerShape(16.dp),
                            color = WarmAmber.copy(alpha = 0.22f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("role_switcher_dropdown_btn")
                                .testTag("role_switcher_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val roleIcon = when (currentRole) {
                                    UserRole.CITIZEN -> Icons.Default.Person
                                    UserRole.PANCHAYAT_OPERATOR -> Icons.Default.SupervisorAccount
                                    UserRole.SUPER_ADMIN -> Icons.Default.AdminPanelSettings
                                }
                                Icon(
                                    imageVector = roleIcon,
                                    contentDescription = "Active Role",
                                    tint = WarmAmber,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (currentRole) {
                                        UserRole.CITIZEN -> "नागरिक"
                                        UserRole.PANCHAYAT_OPERATOR -> "ऑपरेटर"
                                        UserRole.SUPER_ADMIN -> "एडमिन"
                                    },
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = roleDropdownExpanded,
                            onDismissRequest = { roleDropdownExpanded = false }
                        ) {
                            UserRole.values().forEach { role ->
                                val (icon, label) = when (role) {
                                    UserRole.CITIZEN -> Icons.Default.Person to "नागरिक (Citizen)"
                                    UserRole.PANCHAYAT_OPERATOR -> Icons.Default.SupervisorAccount to "पंचायत ऑपरेटर (Operator)"
                                    UserRole.SUPER_ADMIN -> Icons.Default.AdminPanelSettings to "सुपर एडमिन (Admin)"
                                }
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (role == currentRole) EmeraldPrimary else Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = label,
                                                fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal,
                                                color = if (role == currentRole) EmeraldDeep else Color.Unspecified
                                            )
                                            if (role == currentRole) {
                                                Spacer(modifier = Modifier.weight(1f))
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = EmeraldPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onRoleSelected(role)
                                        roleDropdownExpanded = false
                                    },
                                    modifier = Modifier.testTag("dropdown_role_${role.name.lowercase()}")
                                )
                            }
                        }
                    }

                    // Optional User Profile / Sign-Out Menu Button
                    if (userEmail != null) {
                        Box {
                            IconButton(
                                onClick = { userMenuExpanded = true },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .testTag("user_profile_menu_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User account",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = userMenuExpanded,
                                onDismissRequest = { userMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "लॉग-इन खाता (Account)",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = userEmail,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    },
                                    onClick = {}
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Sign Out",
                                                tint = Color(0xFFC74747),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("लॉग आउट (Sign Out)", color = Color(0xFFC74747))
                                        }
                                    },
                                    onClick = {
                                        userMenuExpanded = false
                                        onSignOut()
                                    },
                                    modifier = Modifier.testTag("sign_out_menu_item")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dedicated Persistent Tab-Based Role Switcher for Rapid Testing
            RoleSwitcherTabBar(
                currentRole = currentRole,
                onRoleSelected = onRoleSelected
            )
        }
    }
}

/**
 * Tab-based segmented role switcher:
 * Allows one-tap switching between Citizen, Panchayat Operator, and Admin modes to facilitate testing.
 */
@Composable
fun RoleSwitcherTabBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Black.copy(alpha = 0.22f),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("role_switcher_tabs")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoleTabItem(
                role = UserRole.CITIZEN,
                label = "Citizen (नागरिक)",
                icon = Icons.Default.Person,
                isSelected = currentRole == UserRole.CITIZEN,
                onClick = { onRoleSelected(UserRole.CITIZEN) },
                testTag = "role_tab_citizen",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            RoleTabItem(
                role = UserRole.PANCHAYAT_OPERATOR,
                label = "Operator (ऑपरेटर)",
                icon = Icons.Default.SupervisorAccount,
                isSelected = currentRole == UserRole.PANCHAYAT_OPERATOR,
                onClick = { onRoleSelected(UserRole.PANCHAYAT_OPERATOR) },
                testTag = "role_tab_operator",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            RoleTabItem(
                role = UserRole.SUPER_ADMIN,
                label = "Admin (एडमिन)",
                icon = Icons.Default.AdminPanelSettings,
                isSelected = currentRole == UserRole.SUPER_ADMIN,
                onClick = { onRoleSelected(UserRole.SUPER_ADMIN) },
                testTag = "role_tab_admin",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RoleTabItem(
    role: UserRole,
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "tab_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) EmeraldDeep else Color.White.copy(alpha = 0.85f),
        animationSpec = tween(durationMillis = 200),
        label = "tab_text_color"
    )

    val iconColor = if (isSelected) WarmAmber else Color.White.copy(alpha = 0.75f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(11.dp),
        color = backgroundColor,
        shadowElevation = if (isSelected) 3.dp else 0.dp,
        modifier = modifier
            .height(38.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}
