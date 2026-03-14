package com.example.steptracker.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.steptracker.presentation.components.BottomNavBar
import com.example.steptracker.presentation.components.NavTab
import com.example.steptracker.presentation.viewmodel.ProfileViewModel
import com.example.steptracker.ui.theme.BgPrimary
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.BorderPrimary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.SurfaceGreen
import com.example.steptracker.ui.theme.SurfacePink
import com.example.steptracker.ui.theme.TextDarkGrey
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

@Composable
fun ProfileScreen(
    onTabSelected: (NavTab) -> Unit = {},
    onSignOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) onSignOut()
    }

    LaunchedEffect(uiState.isAccountDeleted) {
        if (uiState.isAccountDeleted) onDeleteAccount()
    }

    if (uiState.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteAccountDialog() },
            containerColor = BgSecondary,
            title = {
                Text(
                    text = "Delete Account",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            text = {
                Text(
                    text = "This action is permanent and cannot be undone. All your data will be erased.",
                    color = TextGrey,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteAccount() },
                    colors = ButtonDefaults.textButtonColors(contentColor = SurfacePink),
                ) {
                    Text(text = "Delete", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissDeleteAccountDialog() },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextGrey),
                ) {
                    Text(text = "Cancel")
                }
            },
        )
    }

    Scaffold(
        containerColor = BgPrimary,
        bottomBar = {
            BottomNavBar(
                selectedTab = NavTab.PROFILE,
                onTabSelected = onTabSelected,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "Profile",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                )
            }

            // User Card
            item {
                UserCard(
                    name = uiState.userName,
                    email = uiState.userEmail,
                )
            }

            // Units Section
            item {
                SettingsSectionCard(
                    sectionTitle = "Units",
                    sectionIcon = Icons.Outlined.Settings,
                ) {
                    ToggleRow(
                        title = "Steps + Distance",
                        subtitle = "Display preferences",
                        checked = uiState.stepsDistanceEnabled,
                        onCheckedChange = { viewModel.toggleStepsDistance(it) },
                        showDivider = false,
                    )
                }
            }

            // Notifications Section
            item {
                SettingsSectionCard(
                    sectionTitle = "Notifications",
                    sectionIcon = Icons.Outlined.Notifications,
                ) {
                    ToggleRow(
                        title = "Daily reminder",
                        subtitle = "Get reminded to reach your goal",
                        checked = uiState.dailyReminderEnabled,
                        onCheckedChange = { viewModel.toggleDailyReminder(it) },
                        showDivider = true,
                    )
                    ToggleRow(
                        title = "Goal reached notification",
                        subtitle = "Celebrate when you hit your target",
                        checked = uiState.goalReachedNotificationEnabled,
                        onCheckedChange = { viewModel.toggleGoalReachedNotification(it) },
                        showDivider = true,
                    )
                    ToggleRow(
                        title = "Inactivity nudge",
                        subtitle = "Reminder to move after sitting",
                        checked = uiState.inactivityNudgeEnabled,
                        onCheckedChange = { viewModel.toggleInactivityNudge(it) },
                        showDivider = false,
                    )
                }
            }

            // Permissions Section
            item {
                SettingsSectionCard(
                    sectionTitle = "Permissions",
                    sectionIcon = Icons.Outlined.Lock,
                ) {
                    PermissionToggleRow(
                        icon = Icons.Outlined.Fingerprint,
                        title = "Biometric authentication",
                        subtitle = "Use fingerprint to open app",
                        checked = uiState.biometricAuthEnabled,
                        onCheckedChange = { viewModel.toggleBiometricAuth(it) },
                        showDivider = true,
                    )
                    PermissionToggleRow(
                        icon = Icons.Outlined.LocationOn,
                        title = "Location tracking",
                        subtitle = "Track outdoor activities",
                        checked = uiState.locationTrackingEnabled,
                        onCheckedChange = { viewModel.toggleLocationTracking(it) },
                        showDivider = false,
                    )
                }
            }

            // Data Source Section
            item {
                SettingsSectionCard(
                    sectionTitle = "Data Source",
                    sectionIcon = Icons.Outlined.Storage,
                ) {
                    DataSourceRow(
                        title = "Primary device",
                        subtitle = "Phone sensors",
                        isActive = true,
                        showDivider = true,
                    )
                    DataSourceRow(
                        title = "Wearable device",
                        subtitle = "Not connected",
                        isActive = false,
                        showDivider = false,
                    )
                }
            }

            // Data & Privacy Section
            item {
                SettingsSectionCard(
                    sectionTitle = "Data & Privacy",
                    sectionIcon = Icons.Outlined.Security,
                ) {
                    ActionRow(
                        icon = Icons.Outlined.DeleteOutline,
                        title = "Reset all data",
                        subtitle = "Clear activity history",
                        showDivider = true,
                    )
                    ActionRow(
                        icon = Icons.Outlined.Policy,
                        title = "Privacy policy",
                        subtitle = "How we protect your data",
                        showDivider = false,
                    )
                }
            }

            // Delete Account Section
            item {
                DeleteAccountCard(
                    isDeleting = uiState.isDeletingAccount,
                    onDeleteAccount = { viewModel.showDeleteAccountDialog() },
                )
            }

            uiState.deleteAccountError?.let { error ->
                item {
                    Text(
                        text = error,
                        color = SurfacePink,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Sign Out Section
            item {
                SignOutCard(onSignOut = { viewModel.signOut() })
            }
        }
    }
}

@Composable
private fun UserCard(
    name: String,
    email: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BgSecondary)
            .padding(24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BtnPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                )
                Text(
                    text = email,
                    color = TextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "Edit profile",
                tint = TextGrey,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    sectionTitle: String,
    sectionIcon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgSecondary)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = sectionIcon,
                contentDescription = null,
                tint = TextGrey,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = sectionTitle,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 28.sp,
            )
        }
        content()
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp,
                )
                Text(
                    text = subtitle,
                    color = TextDarkGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            ProfileSwitch(checked = checked, onCheckedChange = onCheckedChange)
        }
        if (showDivider) {
            HorizontalDivider(color = BorderPrimary, thickness = 1.dp)
        }
    }
}

@Composable
private fun PermissionToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BorderPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextGrey,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp,
                    )
                    Text(
                        text = subtitle,
                        color = TextDarkGrey,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 18.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            ProfileSwitch(checked = checked, onCheckedChange = onCheckedChange)
        }
        if (showDivider) {
            HorizontalDivider(color = BorderPrimary, thickness = 1.dp)
        }
    }
}

@Composable
private fun DataSourceRow(
    title: String,
    subtitle: String,
    isActive: Boolean,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 24.sp,
                )
                Text(
                    text = subtitle,
                    color = TextDarkGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp,
                )
            }
            if (isActive) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceGreen)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "Active",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextGrey,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(color = BorderPrimary, thickness = 1.dp)
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BorderPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextGrey,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 24.sp,
                    )
                    Text(
                        text = subtitle,
                        color = TextDarkGrey,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 18.sp,
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = TextGrey,
                modifier = Modifier.size(20.dp),
            )
        }
        if (showDivider) {
            HorizontalDivider(color = BorderPrimary, thickness = 1.dp)
        }
    }
}

@Composable
private fun ProfileSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .height(24.dp)
            .width(44.dp),
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = BtnPrimary,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = BorderPrimary,
            uncheckedBorderColor = Color.Transparent,
        ),
    )
}

@Composable
private fun DeleteAccountCard(
    isDeleting: Boolean,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgSecondary)
            .clickable(enabled = !isDeleting, onClick = onDeleteAccount)
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isDeleting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = SurfacePink,
                strokeWidth = 2.dp,
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete account",
                    tint = SurfacePink,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Delete Account",
                    color = SurfacePink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp,
                )
            }
        }
    }
}

@Composable
private fun SignOutCard(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgSecondary)
            .clickable(onClick = onSignOut)
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = "Sign out",
                tint = SurfacePink,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "Sign Out",
                color = SurfacePink,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
            )
        }
    }
}
