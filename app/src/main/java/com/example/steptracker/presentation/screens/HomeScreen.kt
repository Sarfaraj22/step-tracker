package com.example.steptracker.presentation.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Umbrella
import androidx.compose.material.icons.outlined.WbCloudy
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.SurfaceGreen
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    onActivityClick: () -> Unit = {},
    onGoalsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(NavTab.HOME) }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                viewModel.loadWeather(location?.latitude, location?.longitude)
            }.addOnFailureListener {
                viewModel.loadWeather(null, null)
            }
        } else {
            viewModel.loadWeather(null, null)
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                viewModel.loadWeather(location?.latitude, location?.longitude)
            }.addOnFailureListener {
                viewModel.loadWeather(null, null)
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

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

            // Weather forecast card
            item {
                WeatherForecastCard(
                    temp = uiState.weatherTemp,
                    condition = uiState.weatherCondition,
                    message = uiState.weatherMessage,
                    isWalkSuitable = uiState.isWalkSuitable,
                    isLoading = uiState.weatherIsLoading,
                    conditionCode = uiState.weatherConditionCode,
                    isDay = uiState.weatherIsDay,
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
                DateNavigator(
                    date = uiState.date,
                    isAtLatestDay = uiState.isAtLatestDay,
                    onPreviousDay = { viewModel.navigateDate(-1) },
                    onNextDay = { viewModel.navigateDate(1) },
                )
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

private fun weatherIcon(conditionCode: Int, isDay: Boolean): ImageVector = when {
    conditionCode == 1000 -> if (isDay) Icons.Outlined.WbSunny else Icons.Outlined.NightsStay
    conditionCode in 1003..1009 -> if (isDay) Icons.Outlined.WbCloudy else Icons.Outlined.Cloud
    conditionCode == 1030 || conditionCode == 1135 || conditionCode == 1147 -> Icons.Outlined.Cloud
    conditionCode == 1087 || conditionCode >= 1273 -> Icons.Outlined.Bolt
    conditionCode in 1114..1264 -> Icons.Outlined.AcUnit
    else -> Icons.Outlined.Umbrella
}

@Composable
private fun WeatherForecastCard(
    temp: String,
    condition: String,
    message: String,
    isWalkSuitable: Boolean,
    isLoading: Boolean,
    conditionCode: Int,
    isDay: Boolean,
) {
    Surface(
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
    ) {
        Row(
            modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = BtnPrimary,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Today's Weather",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 28.sp,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = temp,
                            color = TextPrimary,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 36.sp,
                        )
                        Text(
                            text = "  $condition",
                            color = TextGrey,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isWalkSuitable) SurfaceGreen else TextGrey),
                        )
                        Text(
                            text = message,
                            color = TextGrey,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 24.sp,
                        )
                    }
                }
                Icon(
                    imageVector = weatherIcon(conditionCode, isDay),
                    contentDescription = "Weather icon",
                    tint = BtnPrimary,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
    }
}

@Composable
private fun DateNavigator(
    date: String,
    isAtLatestDay: Boolean,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
) {
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
                .clickable { onPreviousDay() },
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
            tint = if (isAtLatestDay) TextGrey.copy(alpha = 0.3f) else TextGrey,
            modifier = Modifier
                .size(20.dp)
                .clickable(enabled = !isAtLatestDay) { onNextDay() },
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
