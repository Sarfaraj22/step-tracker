package com.example.steptracker.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steptracker.R
import com.example.steptracker.presentation.components.BottomNavBar
import com.example.steptracker.presentation.components.GoalProgressBar
import com.example.steptracker.presentation.components.NavTab
import com.example.steptracker.presentation.components.StatCard
import com.example.steptracker.presentation.components.StepProgressRing
import com.example.steptracker.presentation.components.TodayActivityChart
import com.example.steptracker.presentation.components.WeeklyActivityChart
import com.example.steptracker.presentation.viewmodel.GoalItem
import com.example.steptracker.presentation.viewmodel.HomeViewModel
import com.example.steptracker.ui.theme.BgPrimary
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

@Composable
fun HomeScreen(
    onActivityClick: () -> Unit = {},
    onGoalsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(NavTab.HOME) }

    Scaffold(
        containerColor = BgPrimary,
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        NavTab.ACTIVITY -> onActivityClick()
                        NavTab.GOALS -> onGoalsClick()
                        NavTab.PROFILE -> onProfileClick()
                        else -> {}
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = 28.dp,
                vertical = 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Dashboard heading
            item {
                Text(
                    text = "Dashboard",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                )
            }

            // Step progress ring card
            item {
                StepProgressRing(
                    stepCount = uiState.stepCount,
                    stepGoal = uiState.stepGoal,
                )
            }

            // Date navigator
            item {
                DateNavigator(date = uiState.date)
            }

            // Stat cards row 1: Distance + Calories
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    StatCard(
                        value = uiState.distanceKm.toString(),
                        unit = "km",
                        label = "Distance Walked",
                        trendValue = "12.5",
                        trendLabel = "12.5",
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        value = uiState.caloriesBurned.toString(),
                        unit = "kcal",
                        label = "Calories Burned",
                        trendValue = "8.2",
                        trendLabel = "8.2",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Stat cards row 2: Active Time + Heart Rate
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    StatCard(
                        value = uiState.activeMinutes.toString(),
                        unit = "min",
                        label = "Active Time",
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        value = uiState.avgHeartRate.toString(),
                        unit = "bpm",
                        label = "Avg Heart Rate",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Weekly activity chart
            item {
                WeeklyActivityChart(weeklySteps = uiState.weeklySteps)
            }

            // Today's activity chart
            item {
                TodayActivityChart(hourlySteps = uiState.todayHourlySteps)
            }

            // Goals progress
            item {
                GoalsProgressCard(goals = uiState.goals)
            }
        }
    }
}

@Composable
private fun DateNavigator(date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_chevron_left),
            contentDescription = "Previous day",
            tint = TextGrey,
            modifier = Modifier
                .size(20.dp)
                .clickable { /* TODO: navigate date */ },
        )
        Text(
            text = date,
            color = TextGrey,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
        )
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_chevron_right),
            contentDescription = "Next day",
            tint = TextGrey,
            modifier = Modifier
                .size(20.dp)
                .clickable { /* TODO: navigate date */ },
        )
    }
}

@Composable
private fun GoalsProgressCard(goals: List<GoalItem>) {
    Surface(
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Goals Progress",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            goals.forEach { goal ->
                GoalProgressBar(
                    label = goal.label,
                    current = goal.current,
                    target = goal.target,
                    unit = goal.unit,
                    isGreen = goal.isGreen,
                )
            }
        }
    }
}
