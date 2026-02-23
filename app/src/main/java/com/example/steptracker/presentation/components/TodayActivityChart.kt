package com.example.steptracker.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.TextAsh
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

private val barHourLabels = listOf("6", "9", "12", "15", "18", "21")

@Composable
fun TodayActivityChart(
    hourlySteps: List<Int>,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = TextAsh,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Today's Activity",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "Hourly \u25BE",
                    color = TextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 8.dp),
            ) {
                drawBarChart(
                    hourlySteps = hourlySteps,
                    textMeasurer = textMeasurer,
                    labelStyle = labelStyle,
                )
            }
        }
    }
}

private fun DrawScope.drawBarChart(
    hourlySteps: List<Int>,
    textMeasurer: TextMeasurer,
    labelStyle: TextStyle,
) {
    val xLabelHeight = 20f
    val topPadding = 8f
    val chartBottom = size.height - xLabelHeight - 4f
    val chartTop = topPadding
    val chartHeight = chartBottom - chartTop
    val barCount = hourlySteps.size.coerceAtLeast(1)
    val maxSteps = hourlySteps.maxOrNull()?.toFloat()?.coerceAtLeast(1f) ?: 1f

    val groupWidth = size.width / barCount
    val barWidth = groupWidth * 0.45f
    val cornerRadius = CornerRadius(6f, 6f)

    hourlySteps.forEachIndexed { index, steps ->
        val barFraction = steps.toFloat() / maxSteps
        val barHeight = barFraction * chartHeight
        val centerX = groupWidth * index + groupWidth / 2f
        val left = centerX - barWidth / 2f
        val top = chartBottom - barHeight

        drawRoundRect(
            color = BtnPrimary,
            topLeft = Offset(left, top),
            size = Size(barWidth, barHeight),
            cornerRadius = cornerRadius,
        )

        // X-axis label
        if (index < barHourLabels.size) {
            val label = barHourLabels[index]
            val measured = textMeasurer.measure(label, labelStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                style = labelStyle,
                topLeft = Offset(
                    x = centerX - measured.size.width / 2f,
                    y = chartBottom + 4f,
                ),
            )
        }
    }
}
