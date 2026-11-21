package tr.mathapp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Correct = Color(0xFF2E7D32)
val CorrectBg = Color(0xFFE8F5E9)
val Wrong = Color(0xFFC62828)
val WrongBg = Color(0xFFFFEBEE)
val StarGold = Color(0xFFFFB300)
val Muted = Color(0xFF6B6B7B)

private val Colors = lightColorScheme(
    primary = Color(0xFF4F5BD5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3E6FF),
    onPrimaryContainer = Color(0xFF1A2178),
    secondary = Color(0xFFFF8F00),
    onSecondary = Color.White,
    background = Color(0xFFFFF9EF),
    onBackground = Color(0xFF1F1B16),
    surface = Color.White,
    onSurface = Color(0xFF1F1B16),
    surfaceVariant = Color(0xFFF3EEE6),
    onSurfaceVariant = Color(0xFF4A4458),
)

@Composable
fun MathTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, content = content)
}
