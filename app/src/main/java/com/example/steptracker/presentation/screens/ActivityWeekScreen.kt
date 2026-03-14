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
import com.example.steptracker.presentation.viewmodel.ActivityViewModel
import com.example.steptracker.presentation.viewmodel.ActivityWeekUiState
import com.example.steptracker.presentation.viewmodel.WeeklyBreakdownItem
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

private val BestDayGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF4D03F), Color(0xFFE5C13B)),
)

@Composable
fun ActivityWeekScreen(
    onHomeClick: () -> Unit = {},
    onDayClick: () -> Unit = {},
    onMonthClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: ActivityViewModel = viewModel(),
) {
    val uiState by viewModel.weekUiState.collectAsState()

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

            item { WeekPeriodTabSelector(onDayClick = onDayClick, onMonthClick = onMonthClick) }

            item { WeekStatsRow(uiState) }

            item { WeekStepHistoryCard(uiState.weeklySteps) }

            item { WeekBestDayCard(uiState.bestDaySteps, uiState.bestDayDate) }

            item { WeekStreakCardsRow(uiState.currentStreak, uiState.longestStreak) }

            item { WeeklyDailyBreakdownCard(uiState.weeklyBreakdown) }
        }
    }
}

// ---------------------------------------------------------------------------
// Period tab selector — Week is active
// ---------------------------------------------------------------------------

@Composable
private fun WeekPeriodTabSelector(onDayClick: () -> Unit, onMonthClick: () -> Unit = {}) {
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
            WeekPeriodTab(
                label = "Day",
                isSelected = false,
                onClick = onDayClick,
                modifier = Modifier.weight(1f),
            )
            WeekPeriodTab(
                label = "Week",
                isSelected = true,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            WeekPeriodTab(
                label = "Month",
                isSelected = false,
                onClick = onMonthClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun WeekPeriodTab(
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
            .then(
                if (!isSelected) Modifier.background(Color.Transparent)
                else Modifier
            )
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
// Stats row — Total Steps + Daily Avg (week values)
// ---------------------------------------------------------------------------

@Composable
private fun WeekStatsRow(uiState: ActivityWeekUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        WeekStatInfoCard(
            icon = Icons.Filled.DirectionsWalk,
            label = "Total Steps",
            value = "%,d".format(uiState.totalSteps),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        WeekStatInfoCard(
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
private fun WeekStatInfoCard(
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
// Step History bar chart — 7 bars Mon–Sun, y-axis 0k–10k
// ---------------------------------------------------------------------------

private val weekXLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val weekYLabels = listOf("10k", "7.5k", "5k", "2.5k", "0k")
private val weekYValues = listOf(10_000f, 7_500f, 5_000f, 2_500f, 0f)
private const val weekMaxSteps = 10_000f

@Composable
private fun WeekStepHistoryCard(weeklySteps: List<Int>) {
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
                    text = "7 Days",
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
                drawWeekStepHistoryChart(
                    weeklySteps = weeklySteps,
                    textMeasurer = textMeasurer,
                    axisStyle = axisLabelStyle,
                )
            }
        }
    }
}

private fun DrawScope.drawWeekStepHistoryChart(
    weeklySteps: List<Int>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    axisStyle: TextStyle,
) {
    val xLabelHeight = 28f
    val yLabelWidth = 52f
    val topPadding = 8f

    val chartLeft = yLabelWidth
    val chartRight = size.width
    val chartBottom = size.height - xLabelHeight - 4f
    val chartTop = topPadding
    val chartHeight = chartBottom - chartTop
    val chartWidth = chartRight - chartLeft

    // Y-axis labels
    weekYLabels.forEachIndexed { index, label ->
        val fraction = weekYValues[index] / weekMaxSteps
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
    val barCount = weeklySteps.size.coerceAtLeast(1)
    val groupWidth = chartWidth / barCount
    val barWidth = groupWidth * 0.55f
    val cornerRadius = CornerRadius(6f, 6f)

    weeklySteps.forEachIndexed { index, steps ->
        val fraction = steps.toFloat() / weekMaxSteps
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

    // X-axis labels
    weekXLabels.forEachIndexed { index, label ->
        val centerX = chartLeft + groupWidth * index + groupWidth / 2f
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
// Best Day card — shows date string (e.g. "Tue, 2/10")
// ---------------------------------------------------------------------------

@Composable
private fun WeekBestDayCard(bestDaySteps: Int, bestDayDate: String) {
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
private fun WeekStreakCardsRow(currentStreak: Int, longestStreak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        WeekStreakCard(
            icon = Icons.Filled.LocalFireDepartment,
            iconTint = Color(0xFFFF6B35),
            label = "Current Streak",
            value = currentStreak.toString(),
            subtitle = "days over 8,000 steps",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        WeekStreakCard(
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
private fun WeekStreakCard(
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
private fun WeeklyDailyBreakdownCard(breakdown: List<WeeklyBreakdownItem>) {
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
                    WeeklyBreakdownRow(item = item, maxSteps = maxSteps)
                }
            }
        }
    }
}

@Composable
private fun WeeklyBreakdownRow(item: WeeklyBreakdownItem, maxSteps: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Day + date labels
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

        // Progress bar + optional goal badge
        Box(
            modifier = Modifier
                .weight(1f)
                .height(32.dp),
        ) {
            val fraction = item.steps.toFloat() / maxSteps

            // Track background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BorderPrimary),
            )
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = fraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (item.goalAchieved) BtnPrimary else SurfaceSecondary),
            )
            // Step count
            Text(
                text = "%,d".format(item.steps),
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center),
            )

            // Goal-achieved badge (green circle with checkmark)
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
