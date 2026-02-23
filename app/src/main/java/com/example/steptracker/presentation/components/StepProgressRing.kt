package com.example.steptracker.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

@Composable
fun StepProgressRing(
    stepCount: Int,
    stepGoal: Int,
    modifier: Modifier = Modifier,
    ringSize: Dp = 224.dp,
    strokeWidth: Dp = 16.dp,
) {
    val progress = (stepCount.toFloat() / stepGoal).coerceIn(0f, 1f)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BgSecondary,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(ringSize),
            ) {
                Canvas(modifier = Modifier.size(ringSize)) {
                    val strokePx = strokeWidth.toPx()
                    val diameter = size.minDimension - strokePx
                    val topLeft = Offset(strokePx / 2, strokePx / 2)
                    val arcSize = Size(diameter, diameter)
                    val startAngle = 135f
                    val sweepAngle = 270f

                    // Dashed background track
                    drawArc(
                        color = Color(0xFF2A2A2A),
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

                    // Foreground progress arc
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

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%,d".format(stepCount),
                        color = TextPrimary,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 48.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Steps today",
                        color = TextGrey,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }
    }
}
