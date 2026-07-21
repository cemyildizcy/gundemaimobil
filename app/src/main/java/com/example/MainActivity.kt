package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Instantiate ViewModel with Application context safely using a custom Factory
            val viewModel: MainViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(application) as T
                }
            })

            val preferences by viewModel.preferences.collectAsState()

            MyApplicationTheme(darkTheme = preferences.isDarkTheme) {
                if (!preferences.isOnboarded) {
                    LandingScreen(
                        viewModel = viewModel,
                        onOnboardingComplete = {
                            // Onboarding completed, state will automatically refresh and open MainAppScreen
                        }
                    )
                } else {
                    MainAppScreen(
                        viewModel = viewModel,
                        onLogout = {
                            // On logout, state will automatically refresh and open LandingScreen
                        }
                    )
                }
            }
        }
    }
}
