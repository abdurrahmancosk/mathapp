package com.example.mathapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mathapp.ui.screens.HomeScreen
import com.example.mathapp.ui.screens.ProgressScreen
import com.example.mathapp.ui.screens.QuizScreen
import com.example.mathapp.ui.screens.SettingsScreen
import com.example.mathapp.ui.theme.MathAppTheme
import com.example.mathapp.ui.viewmodel.HomeViewModel
import com.example.mathapp.ui.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathAppTheme {
                MathApp()
            }
        }
    }
}

@Composable
fun MathApp() {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedActivityId by remember { mutableStateOf<Int?>(null) }

    val homeViewModel: HomeViewModel = viewModel()
    val quizViewModel: QuizViewModel = viewModel()

    Scaffold(
        bottomBar = {
            if (currentScreen != "quiz") {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Anasayfa") },
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Progress") },
                        label = { Text("İlerleme") },
                        selected = currentScreen == "progress",
                        onClick = { currentScreen = "progress" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Ayarlar") },
                        selected = currentScreen == "settings",
                        onClick = { currentScreen = "settings" }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(
                    viewModel = homeViewModel,
                    onActivitySelected = { activityId ->
                        selectedActivityId = activityId
                        currentScreen = "quiz"
                        quizViewModel.loadActivity(activityId)
                    }
                )
                "quiz" -> QuizScreen(
                    viewModel = quizViewModel,
                    onBackClick = { currentScreen = "home" }
                )
                "progress" -> ProgressScreen(viewModel = homeViewModel)
                "settings" -> SettingsScreen()
            }
        }
    }
}
