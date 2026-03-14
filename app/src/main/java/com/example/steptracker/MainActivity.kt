package com.example.steptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.steptracker.presentation.navigation.StepTrackerApp
import com.example.steptracker.presentation.screens.ActivityDayScreen
import com.example.steptracker.presentation.screens.ActivityMonthScreen
import com.example.steptracker.presentation.screens.ActivityWeekScreen
import com.example.steptracker.presentation.screens.ForgotPasswordScreen
import com.example.steptracker.presentation.screens.GoalScreen
import com.example.steptracker.presentation.screens.HomeScreen
import com.example.steptracker.presentation.screens.LoginScreen
import com.example.steptracker.presentation.screens.ProfileScreen
import com.example.steptracker.ui.theme.StepTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepTrackerTheme {
                //ProfileScreen()
                //GoalScreen()
                //ActivityMonthScreen()
                //ActivityWeekScreen()
                //ActivityDayScreen()
                //HomeScreen()
                //LoginScreen()
                //ForgotPasswordScreen()
                StepTrackerApp()
            }
        }
    }
}