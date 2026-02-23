package com.example.steptracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.SurfaceGreen
import com.example.steptracker.ui.theme.TextAsh
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

@Composable
fun StatCard(
    value: String,
    unit: String,
    label: String,
    modifier: Modifier = Modifier,
    trendValue: String? = null,
    trendLabel: String? = null,
) {
    Surface(
        modifier = modifier,
        color = BgSecondary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Value + unit row
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = TextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }

            // Label
            Text(
                text = label,
                color = TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )

            // Optional trend section
            if (trendValue != null && trendLabel != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Green vertical bar indicator
                    Spacer(
                        modifier = Modifier
                            .width(4.dp)
                            .height(32.dp)
                            .background(
                                color = SurfaceGreen,
                                shape = RoundedCornerShape(50),
                            ),
                    )
                    Column {
                        Text(
                            text = "+$trendValue",
                            color = SurfaceGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp,
                        )
                        Text(
                            text = buildAnnotatedString {
                                append("Sprint ")
                                withStyle(SpanStyle(color = TextAsh)) {
                                    append("+$trendLabel%")
                                }
                            },
                            color = TextAsh,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 16.sp,
                        )
                    }
                }
            }
        }
    }
}
