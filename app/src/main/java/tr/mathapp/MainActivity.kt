package tr.mathapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import tr.mathapp.ui.AppViewModel
import tr.mathapp.ui.MathTheme
import tr.mathapp.ui.Screen
import tr.mathapp.ui.screens.HomeScreen
import tr.mathapp.ui.screens.QuizScreen
import tr.mathapp.ui.screens.ResultScreen
import tr.mathapp.ui.screens.StatsScreen
import tr.mathapp.ui.screens.TopicScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathTheme { App() }
        }
    }
}

@Composable
private fun App(vm: AppViewModel = viewModel()) {
    BackHandler(enabled = vm.backStack.size > 1) { vm.back() }
    when (val screen = vm.current) {
        Screen.Home -> HomeScreen(vm)
        Screen.Stats -> StatsScreen(vm)
        is Screen.TopicLevels -> TopicScreen(vm, screen.topic)
        Screen.Quiz -> QuizScreen(vm)
        Screen.Result -> ResultScreen(vm)
    }
}
