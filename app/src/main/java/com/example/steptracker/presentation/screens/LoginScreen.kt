package com.example.steptracker.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steptracker.presentation.components.GoogleSignUpButton
import com.example.steptracker.presentation.components.OrDivider
import com.example.steptracker.presentation.components.StepTrackerTextField
import com.example.steptracker.presentation.viewmodel.LoginViewModel
import com.example.steptracker.ui.theme.BgPrimary
import com.example.steptracker.ui.theme.BorderPrimary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.BtnTextPrimary
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onCreateAccountClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Welcome Back",
                style = TextStyle(
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 48.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign in to continue your fitness journey",
                style = TextStyle(
                    color = TextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Email field
            StepTrackerTextField(
                label = "Email Address",
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "Enter your email",
                borderColor = BorderPrimary,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password field
            StepTrackerTextField(
                label = "Password",
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Enter your password",
                borderColor = BorderPrimary,
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                        Icon(
                            painter = painterResource(
                                id = if (uiState.isPasswordVisible) {
                                    android.R.drawable.ic_menu_view
                                } else {
                                    android.R.drawable.ic_secure
                                }
                            ),
                            contentDescription = if (uiState.isPasswordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                            tint = TextGrey
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password
            Text(
                text = "Forgot Password?",
                style = TextStyle(
                    color = BtnPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.End
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onForgotPasswordClick() }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Sign In button
            Button(
                onClick = viewModel::onLoginClick,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnPrimary,
                    contentColor = BtnTextPrimary
                ),
                modifier = Modifier
                    .width(280.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "Sign In",
                    style = TextStyle(
                        color = BtnTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OrDivider()

            Spacer(modifier = Modifier.height(20.dp))

            GoogleSignUpButton(
                onClick = {
                    viewModel.onGoogleSignInClick()
                    onGoogleSignInClick()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Footer
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = TextGrey,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    ) {
                        append("Don't have an account? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = BtnPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("Create Account")
                    }
                },
                modifier = Modifier.clickable { onCreateAccountClick() },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
