package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.WarmAmber

/**
 * CitizenTopBar
 *
 * Clean, modern public-facing top bar exclusively for Citizens:
 * - Brand emblem and verified service badge.
 * - Language switcher dropdown supporting Hindi, English, Bhojpuri, and Maithili.
 * - User account / profile quick action.
 * - Zero administrative tabs or internal role-switching options.
 */
@Composable
fun CitizenTopBar(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    userEmail: String? = null,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var languageDropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        color = EmeraldDeep,
        contentColor = Color.White,
        shadowElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("citizen_top_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.yojanamitra_icon_1793970169334),
                        contentDescription = "YojanaMitra Logo",
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "YojanaMitra",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Verified Public Service",
                            tint = WarmAmber,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Text(
                        text = "आपका सरकारी योजना साथी",
                        fontSize = 11.sp,
                        color = Color(0xFFD2EBE0)
                    )
                }
            }

            // Controls: Language Selector and Profile/Sign-In
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Dropdown Selector
                Box {
                    Surface(
                        onClick = { languageDropdownExpanded = true },
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("language_selector_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentLanguage.displayName.substringBefore(" "),
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
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

                // Profile / Account Icon Button
                Surface(
                    onClick = onProfileClick,
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("citizen_profile_top_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Citizen Profile",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
