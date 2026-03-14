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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.steptracker.presentation.components.StepTrackerTextField
import androidx.compose.foundation.Canvas
import com.example.steptracker.presentation.components.BottomNavBar
import com.example.steptracker.presentation.components.NavTab
import com.example.steptracker.presentation.viewmodel.GoalUiState
import com.example.steptracker.presentation.viewmodel.GoalViewModel
import com.example.steptracker.presentation.viewmodel.RecommendationAccent
import com.example.steptracker.presentation.viewmodel.RecommendationItem
import com.example.steptracker.ui.theme.BgPrimary
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.BorderPrimary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.BtnTextPrimary
import com.example.steptracker.ui.theme.SurfaceGreen
import com.example.steptracker.ui.theme.SurfacePink
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

@Composable
fun GoalScreen(
    onHomeClick: () -> Unit = {},
    onActivityClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: GoalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var customGoalText by remember(uiState.dailyGoal) {
        mutableStateOf(uiState.dailyGoal.toString())
    }

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
                CustomGoalCard(
                    uiState = uiState,
                    onEditClick = { showEditDialog = true },
                )
            }

            item {
                PersonalProfileCard(age = uiState.age, weight = uiState.weight)
            }

            item {
                RecommendationsCard(
                    recommendations = uiState.recommendations,
                    onSetGoalClick = { steps ->
                        viewModel.applyRecommendedGoal(steps)
                    },
                )
            }
        }

        if (showEditDialog) {
            EditGoalDialog(
                currentGoal = uiState.dailyGoal,
                initialText = customGoalText,
                onTextChange = { customGoalText = it },
                onConfirm = {
                    val value = customGoalText.toIntOrNull()
                    if (value != null && value > 0) {
                        viewModel.updateDailyGoal(value)
                        showEditDialog = false
                    }
                },
                onDismiss = { showEditDialog = false },
            )
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
private fun CustomGoalCard(
    uiState: GoalUiState,
    onEditClick: () -> Unit,
) {
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
                text = "Current Goal",
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
                    modifier = Modifier.clickable(onClick = onEditClick),
                )
            }
        }
    }
}

@Composable
private fun PersonalProfileCard(age: String, weight: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Your Profile",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 28.sp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ProfileInputField(
                    label = "Age (years)",
                    value = age,
                    modifier = Modifier.weight(1f),
                )
                ProfileInputField(
                    label = "Weight (kg)",
                    value = weight,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ProfileInputField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = TextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BorderPrimary)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = value,
                color = TextGrey,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun RecommendationsCard(
    recommendations: List<RecommendationItem>,
    onSetGoalClick: (Int) -> Unit,
) {
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
                    RecommendationRow(
                        item = item,
                        onSetGoalClick = onSetGoalClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationRow(
    item: RecommendationItem,
    onSetGoalClick: (Int) -> Unit,
) {
    val accentColor = when (item.accent) {
        RecommendationAccent.YELLOW -> BtnPrimary
        RecommendationAccent.GREEN -> SurfaceGreen
        RecommendationAccent.PINK -> SurfacePink
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accentColor),
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

        if (item.showSetGoalButton && item.suggestedGoal != null) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfacePink)
                    .clickable { onSetGoalClick(item.suggestedGoal) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Set Goal",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun EditGoalDialog(
    currentGoal: Int,
    initialText: String,
    onTextChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgSecondary,
        title = {
            Text(
                text = "Edit Daily Goal",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Set a new daily step target.",
                    color = TextGrey,
                    fontSize = 14.sp,
                )
                StepTrackerTextField(
                    value = initialText,
                    onValueChange = onTextChange,
                    label = "Daily goal (steps)",
                    placeholder = "e.g. 10000",
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}
