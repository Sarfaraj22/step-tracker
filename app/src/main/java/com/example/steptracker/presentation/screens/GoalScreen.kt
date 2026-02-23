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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Canvas
import com.example.steptracker.presentation.components.BottomNavBar
import com.example.steptracker.presentation.components.NavTab
import com.example.steptracker.presentation.viewmodel.GoalUiState
import com.example.steptracker.presentation.viewmodel.GoalViewModel
import com.example.steptracker.presentation.viewmodel.RecommendationItem
import com.example.steptracker.ui.theme.BgPrimary
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.BorderPrimary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.BtnTextPrimary
import com.example.steptracker.ui.theme.SurfaceGreen
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

@Composable
fun GoalScreen(
    onHomeClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: GoalViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BgPrimary,
        bottomBar = {
            BottomNavBar(
                selectedTab = NavTab.GOALS,
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.HOME -> onHomeClick()
                        NavTab.ACTIVITY -> onActivityClick()
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
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Text(
                    text = "Goals",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                )
            }

            item {
                GoalRingCard(
                    dailyGoal = uiState.dailyGoal,
                    currentSteps = uiState.currentSteps,
                    progress = uiState.progress,
                    percentComplete = uiState.percentComplete,
                )
            }

            item {
                QuickPresetsSection(
                    presets = uiState.presets,
                    selectedPreset = uiState.selectedPreset,
                    onPresetClick = { viewModel.selectPreset(it) },
                )
            }

            item {
                CustomGoalCard(uiState = uiState)
            }

            item {
                RecommendationsCard(recommendations = uiState.recommendations)
            }
        }
    }
}

@Composable
private fun GoalRingCard(
    dailyGoal: Int,
    currentSteps: Int,
    progress: Float,
    percentComplete: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BgSecondary,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 32.dp, end = 32.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(224.dp),
            ) {
                Canvas(modifier = Modifier.size(224.dp)) {
                    val strokePx = 16.dp.toPx()
                    val diameter = size.minDimension - strokePx
                    val topLeft = Offset(strokePx / 2, strokePx / 2)
                    val arcSize = Size(diameter, diameter)
                    val startAngle = 135f
                    val sweepAngle = 270f

                    drawArc(
                        color = BorderPrimary,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(
                            width = strokePx,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 10f), 0f),
                            cap = StrokeCap.Round,
                        ),
                    )

                    if (progress > 0f) {
                        drawArc(
                            color = BtnPrimary,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle * progress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(
                                width = strokePx,
                                cap = StrokeCap.Round,
                            ),
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "%,d".format(dailyGoal),
                        color = TextPrimary,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 48.sp,
                    )
                    Text(
                        text = "Daily Goal",
                        color = TextGrey,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Current: %,d steps".format(currentSteps),
                    color = TextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = "$percentComplete% Complete",
                    color = SurfaceGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun QuickPresetsSection(
    presets: List<Int>,
    selectedPreset: Int,
    onPresetClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Quick Presets",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            presets.forEach { preset ->
                val isSelected = preset == selectedPreset
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) BtnPrimary else BorderPrimary)
                        .clickable { onPresetClick(preset) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = when (preset) {
                            5000 -> "5K"
                            8000 -> "8K"
                            10000 -> "10K"
                            else -> "%,d".format(preset)
                        },
                        color = if (isSelected) BtnTextPrimary else TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomGoalCard(uiState: GoalUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Custom Goal",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 28.sp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = TextPrimary,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        ) {
                            append("%,d".format(uiState.dailyGoal))
                        }
                        withStyle(
                            SpanStyle(
                                color = TextGrey,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        ) {
                            append(" steps")
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "Edit",
                    color = BtnPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* TODO: open edit dialog */ },
                )
            }
        }
    }
}

@Composable
private fun RecommendationsCard(recommendations: List<RecommendationItem>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Recommendations",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 28.sp,
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                recommendations.forEach { item ->
                    RecommendationRow(item = item)
                }
            }
        }
    }
}

@Composable
private fun RecommendationRow(item: RecommendationItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(50))
                .background(if (item.isYellow) BtnPrimary else SurfaceGreen),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = item.subtitle,
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}
