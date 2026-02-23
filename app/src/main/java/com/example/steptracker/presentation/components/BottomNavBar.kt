package com.example.steptracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.steptracker.ui.theme.BgPrimary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.TextGrey

enum class NavTab { HOME, ACTIVITY, GOALS, PROFILE }

@Composable
fun BottomNavBar(
    selectedTab: NavTab = NavTab.HOME,
    onTabSelected: (NavTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BgPrimary)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavTab.entries.forEach { tab ->
            NavBarItem(
                tab = tab,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun NavBarItem(
    tab: NavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = when (tab) {
        NavTab.HOME -> "Home"
        NavTab.ACTIVITY -> "Activity"
        NavTab.GOALS -> "Goals"
        NavTab.PROFILE -> "Profile"
    }
    val icon: ImageVector = when (tab) {
        NavTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
        NavTab.ACTIVITY -> if (isSelected) Icons.AutoMirrored.Filled.ShowChart else Icons.AutoMirrored.Outlined.ShowChart
        NavTab.GOALS -> if (isSelected) Icons.Filled.TrackChanges else Icons.Outlined.TrackChanges
        NavTab.PROFILE -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
    }
    val labelColor = if (isSelected) BtnPrimary else TextGrey

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(14.dp))
                .then(
                    if (isSelected) Modifier.background(BtnPrimary)
                    else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) BgPrimary else TextGrey,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = label,
            color = labelColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
