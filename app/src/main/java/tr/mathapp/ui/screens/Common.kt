package tr.mathapp.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import tr.mathapp.ui.AppViewModel
import tr.mathapp.ui.Screen
import tr.mathapp.ui.StarGold

@Composable
fun MainBottomBar(vm: AppViewModel) {
    NavigationBar {
        NavigationBarItem(
            selected = vm.current == Screen.Home,
            onClick = { vm.switchTab(Screen.Home) },
            icon = { Text("🏠", fontSize = 22.sp) },
            label = { Text("Konular") },
        )
        NavigationBarItem(
            selected = vm.current == Screen.Stats,
            onClick = { vm.switchTab(Screen.Stats) },
            icon = { Text("📈", fontSize = 22.sp) },
            label = { Text("İlerlemem") },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, color: Color, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {}) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = color,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White,
        ),
    )
}

@Composable
fun StarRow(stars: Int, max: Int = 3, size: TextUnit = 20.sp) {
    Row {
        repeat(max) { i ->
            Text(if (i < stars) "★" else "☆", color = StarGold, fontSize = size)
        }
    }
}
