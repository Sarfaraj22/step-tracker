package com.example.steptracker.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.steptracker.R
import com.example.steptracker.ui.theme.BgSecondary
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

@Composable
fun OrDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BgSecondary,
            thickness = 1.dp
        )
        Text(
            text = "or",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = TextStyle(
                color = TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = BgSecondary,
            thickness = 1.dp
        )
    }
}

@Composable
fun GoogleSignUpButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BgSecondary,
            contentColor = TextPrimary
        ),
        modifier = modifier
            .width(280.dp)
            .height(56.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Continue with Google",
            style = TextStyle(
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
