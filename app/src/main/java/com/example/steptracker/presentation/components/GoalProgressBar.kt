package com.example.steptracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.steptracker.ui.theme.BorderPrimary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.SurfaceGreen
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

@Composable
fun GoalProgressBar(
    label: String,
    current: Float,
    target: Float,
    unit: String,
    modifier: Modifier = Modifier,
    isGreen: Boolean = false,
) {
    val progress = (current / target).coerceIn(0f, 1f)
    val barColor: Color = if (isGreen) SurfaceGreen else BtnPrimary
    val valueText = buildValueText(current, target, unit)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                color = TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
            Text(
                text = valueText,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(BorderPrimary),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(barColor),
            )
        }
    }
}

private fun buildValueText(current: Float, target: Float, unit: String): String {
    val currentStr = if (current == current.toLong().toFloat()) {
        "%,.0f".format(current)
    } else {
        current.toString()
    }
    val targetStr = if (target == target.toLong().toFloat()) {
        "%,.0f".format(target)
    } else {
        target.toString()
    }
    return "$currentStr / $targetStr $unit"
}
