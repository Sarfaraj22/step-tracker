package com.example.steptracker.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steptracker.presentation.components.BottomNavBar
import com.example.steptracker.presentation.components.NavTab
import com.example.steptracker.presentation.viewmodel.ActivityMonthUiState
import com.example.steptracker.presentation.viewmodel.ActivityViewModel
import com.example.steptracker.presentation.viewmodel.MonthlyBreakdownItem
import com.example.steptracker.ui.theme.BgPrimary
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.BorderPrimary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.BtnTextPrimary
import com.example.steptracker.ui.theme.IconBlack10
import com.example.steptracker.ui.theme.SurfaceGreen
import com.example.steptracker.ui.theme.SurfaceSecondary
import com.example.steptracker.ui.theme.TextAsh
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

private val MonthBestDayGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF4D03F), Color(0xFFE5C13B)),
)

@Composable
fun ActivityMonthScreen(
    onHomeClick: () -> Unit = {},
    onDayClick: () -> Unit = {},
    onWeekClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: ActivityViewModel = viewModel(),
) {
    val uiState by viewModel.monthUiState.collectAsState()

    Scaffold(
        containerColor = BgPrimary,
        bottomBar = {
            BottomNavBar(
                selectedTab = NavTab.ACTIVITY,
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.HOME -> onHomeClick()
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Text(
                    text = "Activity",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                )
            }

            item {
                MonthPeriodTabSelector(
                    onDayClick = onDayClick,
                    onWeekClick = onWeekClick,
                )
            }

            item { MonthStatsRow(uiState) }

            item { MonthStepHistoryCard(uiState.monthlySteps) }

            item { MonthBestDayCard(uiState.bestDaySteps, uiState.bestDayDate) }

            item { MonthStreakCardsRow(uiState.currentStreak, uiState.longestStreak) }

            item { MonthlyDailyBreakdownCard(uiState.monthlyBreakdown) }
        }
    }
}

// ---------------------------------------------------------------------------
// Period tab selector — Month is active
// ---------------------------------------------------------------------------

@Composable
private fun MonthPeriodTabSelector(
    onDayClick: () -> Unit,
    onWeekClick: () -> Unit,
) {
    Surface(
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MonthPeriodTab(
                label = "Day",
                isSelected = false,
                onClick = onDayClick,
                modifier = Modifier.weight(1f),
            )
            MonthPeriodTab(
                label = "Week",
                isSelected = false,
                onClick = onWeekClick,
                modifier = Modifier.weight(1f),
            )
            MonthPeriodTab(
                label = "Month",
                isSelected = true,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MonthPeriodTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (isSelected) BtnPrimary else BorderPrimary
    val textColor = if (isSelected) BtnTextPrimary else TextGrey

    Box(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

// ---------------------------------------------------------------------------
// Stats row — Total Steps + Daily Avg (monthly values)
// ---------------------------------------------------------------------------

@Composable
private fun MonthStatsRow(uiState: ActivityMonthUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MonthStatInfoCard(
            icon = Icons.Filled.DirectionsWalk,
            label = "Total Steps",
            value = "%,d".format(uiState.totalSteps),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        MonthStatInfoCard(
            icon = Icons.Filled.CalendarToday,
            label = "Daily Avg",
            value = "%,d".format(uiState.dailyAvg),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun MonthStatInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BtnPrimary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = label,
                    color = TextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Step History bar chart — 30 bars, y-axis 0k–12k, 8 x-axis labels
// ---------------------------------------------------------------------------

// Pairs of (barIndex, label) for the 8 x-axis labels shown in the Figma design
private val monthXLabelPositions = listOf(
    7 to "Tue",
    10 to "Fri",
    13 to "Tue",
    16 to "Fri",
    20 to "Tue",
    22 to "Sat",
    25 to "Wed",
    29 to "Sun",
)
private val monthYLabels = listOf("12k", "9k", "6k", "3k", "0k")
private val monthYValues = listOf(12_000f, 9_000f, 6_000f, 3_000f, 0f)
private const val monthMaxSteps = 12_000f

@Composable
private fun MonthStepHistoryCard(monthlySteps: List<Int>) {
    val textMeasurer = rememberTextMeasurer()
    val axisLabelStyle = TextStyle(color = TextAsh, fontSize = 12.sp, fontWeight = FontWeight.Normal)

    Surface(
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Step History",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "30 Days",
                    color = TextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
            ) {
                drawMonthStepHistoryChart(
                    monthlySteps = monthlySteps,
                    textMeasurer = textMeasurer,
                    axisStyle = axisLabelStyle,
                )
            }
        }
    }
}

private fun DrawScope.drawMonthStepHistoryChart(
    monthlySteps: List<Int>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    axisStyle: TextStyle,
) {
    val xLabelHeight = 28f
    val yLabelWidth = 44f
    val topPadding = 8f

    val chartLeft = yLabelWidth
    val chartRight = size.width
    val chartBottom = size.height - xLabelHeight - 4f
    val chartTop = topPadding
    val chartHeight = chartBottom - chartTop
    val chartWidth = chartRight - chartLeft

    // Y-axis labels
    monthYLabels.forEachIndexed { index, label ->
        val fraction = monthYValues[index] / monthMaxSteps
        val y = chartBottom - fraction * chartHeight
        val measured = textMeasurer.measure(label, axisStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = axisStyle,
            topLeft = Offset(
                x = yLabelWidth - measured.size.width - 6f,
                y = y - measured.size.height / 2f,
            ),
        )
    }

    // Bars
    val barCount = monthlySteps.size.coerceAtLeast(1)
    val groupWidth = chartWidth / barCount
    val barWidth = (groupWidth * 0.60f).coerceAtLeast(4f)
    val cornerRadius = CornerRadius(4f, 4f)

    monthlySteps.forEachIndexed { index, steps ->
        val fraction = steps.toFloat() / monthMaxSteps
        val barHeight = (fraction * chartHeight).coerceAtLeast(2f)
        val centerX = chartLeft + groupWidth * index + groupWidth / 2f
        val left = centerX - barWidth / 2f
        val top = chartBottom - barHeight

        drawRoundRect(
            color = BtnPrimary,
            topLeft = Offset(left, top),
            size = Size(barWidth, barHeight),
            cornerRadius = cornerRadius,
        )
    }

    // X-axis labels at specific bar positions
    monthXLabelPositions.forEach { (barIndex, label) ->
        val centerX = chartLeft + groupWidth * barIndex + groupWidth / 2f
        val measured = textMeasurer.measure(label, axisStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = axisStyle,
            topLeft = Offset(
                x = centerX - measured.size.width / 2f,
                y = chartBottom + 6f,
            ),
        )
    }
}

// ---------------------------------------------------------------------------
// Best Day card
// ---------------------------------------------------------------------------

@Composable
private fun MonthBestDayCard(bestDaySteps: Int, bestDayDate: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MonthBestDayGradient)
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = BtnTextPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = "Best Day",
                        color = BtnTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Text(
                    text = "%,d".format(bestDaySteps),
                    color = BtnTextPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp,
                )
                Text(
                    text = bestDayDate,
                    color = Color(0xB3000000),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(IconBlack10),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.TrendingUp,
                    contentDescription = null,
                    tint = BtnTextPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Streak cards row
// ---------------------------------------------------------------------------

@Composable
private fun MonthStreakCardsRow(currentStreak: Int, longestStreak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MonthStreakCard(
            icon = Icons.Filled.LocalFireDepartment,
            iconTint = Color(0xFFFF6B35),
            label = "Current Streak",
            value = currentStreak.toString(),
            subtitle = "days over 8,000 steps",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        MonthStreakCard(
            icon = Icons.Filled.MilitaryTech,
            iconTint = BtnPrimary,
            label = "Longest Streak",
            value = longestStreak.toString(),
            subtitle = "consecutive days",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun MonthStreakCard(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = label,
                    color = TextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp,
            )
            Text(
                text = subtitle,
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Daily Breakdown — day + date labels, goal-achieved badge, colored bars
// ---------------------------------------------------------------------------

@Composable
private fun MonthlyDailyBreakdownCard(breakdown: List<MonthlyBreakdownItem>) {
    val maxSteps = breakdown.maxOfOrNull { it.steps }?.coerceAtLeast(1) ?: 1

    Surface(
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Daily Breakdown",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                breakdown.forEach { item ->
                    MonthlyBreakdownRow(item = item, maxSteps = maxSteps)
                }
            }
        }
    }
}

@Composable
private fun MonthlyBreakdownRow(item: MonthlyBreakdownItem, maxSteps: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.width(48.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = item.dayLabel,
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp,
            )
            Text(
                text = item.dateLabel,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp,
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(32.dp),
        ) {
            val fraction = item.steps.toFloat() / maxSteps

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BorderPrimary),
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = fraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (item.goalAchieved) BtnPrimary else SurfaceSecondary),
            )
            Text(
                text = "%,d".format(item.steps),
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center),
            )

            if (item.goalAchieved) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(SurfaceGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Goal achieved",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }
    }
}
