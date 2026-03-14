package com.example.steptracker.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.steptracker.presentation.components.BottomNavBar
import com.example.steptracker.presentation.components.NavTab
import com.example.steptracker.presentation.viewmodel.ActivityDayUiState
import com.example.steptracker.presentation.viewmodel.ActivityViewModel
import com.example.steptracker.presentation.viewmodel.HourlyBreakdownItem
import com.example.steptracker.ui.theme.BgPrimary
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.BorderPrimary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.BtnTextPrimary
import com.example.steptracker.ui.theme.IconBlack10
import com.example.steptracker.ui.theme.SurfaceSecondary
import com.example.steptracker.ui.theme.TextAsh
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary
import androidx.compose.foundation.Canvas

private val BestDayGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF4D03F), Color(0xFFE5C13B)),
)

@Composable
fun ActivityDayScreen(
    onHomeClick: () -> Unit = {},
    onWeekClick: () -> Unit = {},
    onMonthClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: ActivityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

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

            item { PeriodTabSelector(onWeekClick = onWeekClick, onMonthClick = onMonthClick) }

            item { StatsRow(uiState) }

            item { StepHistoryCard(uiState.hourlySteps) }

            item { BestDayCard(uiState.bestDaySteps, uiState.bestDayTime) }

            item { StreakCardsRow(uiState.currentStreak, uiState.longestStreak) }

            item { DailyBreakdownCard(uiState.dailyBreakdown) }
        }
    }
}

// ---------------------------------------------------------------------------
// Period tab selector
// ---------------------------------------------------------------------------

@Composable
private fun PeriodTabSelector(onWeekClick: () -> Unit = {}, onMonthClick: () -> Unit = {}) {
    Surface(
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PeriodTab(label = "Day", isSelected = true, onClick = {}, modifier = Modifier.weight(1f))
            PeriodTab(label = "Week", isSelected = false, onClick = onWeekClick, modifier = Modifier.weight(1f))
            PeriodTab(label = "Month", isSelected = false, onClick = onMonthClick, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PeriodTab(
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
// Stats row (Total Steps + Daily Avg)
// ---------------------------------------------------------------------------

@Composable
private fun StatsRow(uiState: ActivityDayUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StatInfoCard(
            icon = Icons.Filled.DirectionsWalk,
            label = "Total Steps",
            value = "%,d".format(uiState.totalSteps),
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        StatInfoCard(
            icon = Icons.Filled.CalendarToday,
            label = "Daily Avg",
            value = "%,d".format(uiState.dailyAvg),
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@Composable
private fun StatInfoCard(
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
// Step History bar chart
// ---------------------------------------------------------------------------

private val xAxisLabels = listOf("2:00", "6:00", "10:00", "14:00", "18:00", "22:00")

@Composable
private fun StepHistoryCard(hourlySteps: List<Int>) {
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
                    text = "Last 24h",
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
                drawStepHistoryChart(
                    hourlySteps = hourlySteps,
                    textMeasurer = textMeasurer,
                    axisStyle = axisLabelStyle,
                )
            }
        }
    }
}

private fun DrawScope.drawStepHistoryChart(
    hourlySteps: List<Int>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    axisStyle: TextStyle,
) {
    val yLabels = listOf("1.4k", "1.05k", "0.7k", "0.35k", "0k")
    val yValues = listOf(1400f, 1050f, 700f, 350f, 0f)
    val maxSteps = 1400f

    val xLabelHeight = 28f
    val yLabelWidth = 56f
    val topPadding = 8f

    val chartLeft = yLabelWidth
    val chartRight = size.width
    val chartBottom = size.height - xLabelHeight - 4f
    val chartTop = topPadding
    val chartHeight = chartBottom - chartTop
    val chartWidth = chartRight - chartLeft

    // Draw y-axis labels and horizontal guide lines
    yLabels.forEachIndexed { index, label ->
        val fraction = yValues[index] / maxSteps
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

    // Draw bars
    val barCount = hourlySteps.size.coerceAtLeast(1)
    val groupWidth = chartWidth / barCount
    val barWidth = groupWidth * 0.55f
    val cornerRadius = CornerRadius(6f, 6f)

    hourlySteps.forEachIndexed { index, steps ->
        val fraction = steps.toFloat() / maxSteps
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

    // Draw x-axis labels — 6 evenly spaced over 12 bars (every 2 bars = every 4h)
    val xStep = chartWidth / (xAxisLabels.size - 1)
    xAxisLabels.forEachIndexed { index, label ->
        val x = chartLeft + xStep * index
        val measured = textMeasurer.measure(label, axisStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            style = axisStyle,
            topLeft = Offset(
                x = x - measured.size.width / 2f,
                y = chartBottom + 6f,
            ),
        )
    }
}

// ---------------------------------------------------------------------------
// Best Day card
// ---------------------------------------------------------------------------

@Composable
private fun BestDayCard(bestDaySteps: Int, bestDayTime: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BestDayGradient)
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
                    text = bestDayTime,
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
private fun StreakCardsRow(currentStreak: Int, longestStreak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        StreakCard(
            icon = Icons.Filled.LocalFireDepartment,
            iconTint = Color(0xFFFF6B35),
            label = "Current Streak",
            value = currentStreak.toString(),
            subtitle = "days over 8,000 steps",
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        StreakCard(
            icon = Icons.Filled.MilitaryTech,
            iconTint = BtnPrimary,
            label = "Longest Streak",
            value = longestStreak.toString(),
            subtitle = "consecutive days",
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@Composable
private fun StreakCard(
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
// Daily Breakdown horizontal bar chart
// ---------------------------------------------------------------------------

@Composable
private fun DailyBreakdownCard(breakdown: List<HourlyBreakdownItem>) {
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
                    DailyBreakdownRow(item = item, maxSteps = maxSteps)
                }
            }
        }
    }
}

@Composable
private fun DailyBreakdownRow(item: HourlyBreakdownItem, maxSteps: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Hour + time labels
        Column(
            modifier = Modifier.width(48.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = item.hourLabel,
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp,
            )
            Text(
                text = item.timeLabel,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp,
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BorderPrimary),
            contentAlignment = Alignment.Center,
        ) {
            // Fill
            val fraction = item.steps.toFloat() / maxSteps
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = fraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceSecondary)
                    .align(Alignment.CenterStart),
            )
            // Step count overlay
            Text(
                text = "%,d".format(item.steps),
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
