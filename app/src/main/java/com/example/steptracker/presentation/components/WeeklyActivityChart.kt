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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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

private val yLabels = listOf(0, 3000, 6000, 9000, 12000)
private val xLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@Composable
fun WeeklyActivityChart(
    weeklySteps: List<Int>,
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
                    text = "Weekly Activity",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "7 Days \u25BE",
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
                drawWeeklyChart(
                    weeklySteps = weeklySteps,
                    textMeasurer = textMeasurer,
                    labelStyle = labelStyle,
                )
            }
        }
    }
}

private fun DrawScope.drawWeeklyChart(
    weeklySteps: List<Int>,
    textMeasurer: TextMeasurer,
    labelStyle: TextStyle,
) {
    val yLabelWidth = 44f
    val xLabelHeight = 20f
    val topPadding = 8f
    val chartLeft = yLabelWidth + 8f
    val chartRight = size.width
    val chartBottom = size.height - xLabelHeight - 4f
    val chartTop = topPadding
    val chartWidth = chartRight - chartLeft
    val chartHeight = chartBottom - chartTop
    val maxSteps = 12000f

    // Draw Y-axis labels and horizontal grid lines
    yLabels.forEach { yVal ->
        val yFraction = yVal.toFloat() / maxSteps
        val y = chartBottom - yFraction * chartHeight
        val measured = textMeasurer.measure(yVal.toString(), labelStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = yVal.toString(),
            style = labelStyle,
            topLeft = Offset(
                x = yLabelWidth - measured.size.width - 4f,
                y = y - measured.size.height / 2f,
            ),
        )
        // Subtle grid line
        drawLine(
            color = Color(0xFF2A2A2A),
            start = Offset(chartLeft, y),
            end = Offset(chartRight, y),
            strokeWidth = 1f,
        )
    }

    // Compute point positions
    val count = weeklySteps.size.coerceAtLeast(1)
    val spacing = chartWidth / (count - 1).coerceAtLeast(1).toFloat()
    val points = weeklySteps.mapIndexed { index, steps ->
        val x = chartLeft + index * spacing
        val y = chartBottom - (steps.toFloat() / maxSteps) * chartHeight
        Offset(x, y)
    }

    // Draw X-axis labels
    xLabels.forEachIndexed { index, label ->
        if (index < points.size) {
            val measured = textMeasurer.measure(label, labelStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                style = labelStyle,
                topLeft = Offset(
                    x = points[index].x - measured.size.width / 2f,
                    y = chartBottom + 4f,
                ),
            )
        }
    }

    // Build smooth path using cubic bezier
    val linePath = Path()
    val fillPath = Path()
    points.forEachIndexed { i, point ->
        if (i == 0) {
            linePath.moveTo(point.x, point.y)
            fillPath.moveTo(point.x, chartBottom)
            fillPath.lineTo(point.x, point.y)
        } else {
            val prev = points[i - 1]
            val cpX = (prev.x + point.x) / 2f
            linePath.cubicTo(cpX, prev.y, cpX, point.y, point.x, point.y)
            fillPath.cubicTo(cpX, prev.y, cpX, point.y, point.x, point.y)
        }
    }
    // Close fill path at the bottom
    points.lastOrNull()?.let { last ->
        fillPath.lineTo(last.x, chartBottom)
        fillPath.close()
    }

    // Draw gradient fill
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                BtnPrimary.copy(alpha = 0.35f),
                BtnPrimary.copy(alpha = 0.0f),
            ),
            startY = chartTop,
            endY = chartBottom,
        ),
    )

    // Draw line on top
    drawPath(
        path = linePath,
        color = BtnPrimary,
        style = Stroke(
            width = 2.5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}
