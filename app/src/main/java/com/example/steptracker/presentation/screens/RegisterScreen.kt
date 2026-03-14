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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.steptracker.R
import com.example.steptracker.presentation.components.GoogleSignUpButton
import com.example.steptracker.presentation.components.OrDivider
import com.example.steptracker.presentation.components.StepTrackerTextField
import com.example.steptracker.presentation.viewmodel.RegisterViewModel
import com.example.steptracker.ui.theme.BgPrimary
import com.example.steptracker.ui.theme.BtnPrimary
import com.example.steptracker.ui.theme.BtnTextPrimary
import com.example.steptracker.ui.theme.TextGrey
import com.example.steptracker.ui.theme.TextPrimary
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    onSignInClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Account",
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
                text = "Start tracking your fitness journey",
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

            StepTrackerTextField(
                label = "First Name",
                value = uiState.firstName,
                onValueChange = viewModel::onFirstNameChange,
                placeholder = "Enter your first name",
                errorMessage = uiState.firstNameError,
                onFocusLost = viewModel::validateFirstName
            )
            Spacer(modifier = Modifier.height(20.dp))

            StepTrackerTextField(
                label = "Last Name",
                value = uiState.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = "Enter your last name",
                errorMessage = uiState.lastNameError,
                onFocusLost = viewModel::validateLastName
            )
            Spacer(modifier = Modifier.height(20.dp))

            StepTrackerTextField(
                label = "Email Address",
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "Enter your email",
                errorMessage = uiState.emailError,
                onFocusLost = viewModel::validateEmail,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(20.dp))

            StepTrackerTextField(
                label = "Password",
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Create a password",
                errorMessage = uiState.passwordError,
                onFocusLost = viewModel::validatePassword,
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            },
                            contentDescription = if (uiState.isPasswordVisible) "Hide password" else "Show password",
                            tint = TextGrey
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            StepTrackerTextField(
                label = "Confirm Password",
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                placeholder = "Re-enter your password",
                errorMessage = uiState.confirmPasswordError,
                onFocusLost = viewModel::validateConfirmPassword,
                visualTransformation = if (uiState.isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = viewModel::onToggleConfirmPasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.isConfirmPasswordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            },
                            contentDescription = if (uiState.isConfirmPasswordVisible) "Hide password" else "Show password",
                            tint = TextGrey
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = viewModel::onRegisterClick,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnPrimary,
                    contentColor = BtnTextPrimary
                ),
                modifier = Modifier
                    .width(280.dp)
                    .height(56.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = BtnTextPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Create Account",
                        style = TextStyle(
                            color = BtnTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        )
                    )
                }
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.errorMessage!!,
                    style = TextStyle(
                        color = Color(0xFFFF5252),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OrDivider()

            Spacer(modifier = Modifier.height(20.dp))

            GoogleSignUpButton(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val credentialManager = CredentialManager.create(context)
                            val googleSignInOption = GetSignInWithGoogleOption.Builder(
                                context.getString(R.string.default_web_client_id)
                            ).build()
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleSignInOption)
                                .build()
                            val result = credentialManager.getCredential(context, request)
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(result.credential.data)
                            viewModel.onGoogleSignInResult(googleIdTokenCredential.idToken)
                        } catch (e: GetCredentialCancellationException) {
                            // User dismissed the picker — no action needed
                        } catch (e: GetCredentialException) {
                            viewModel.onGoogleSignInError(e.message)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = TextGrey,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    ) {
                        append("Already have an account? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = BtnPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("Sign In")
                    }
                },
                modifier = Modifier.clickable { onSignInClick() },
                textAlign = TextAlign.Center
            )
        }
    }
}
