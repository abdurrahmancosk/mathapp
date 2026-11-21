package tr.mathapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/** Analog clock: short black hand (akrep) for hours, long red hand (yelkovan) for minutes. */
@Composable
fun ClockFace(hour: Int, minute: Int, modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    val numberStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
    Canvas(
        modifier
            .size(200.dp)
            .semantics { contentDescription = "Analog saat" },
    ) {
        val radius = size.minDimension / 2
        fun point(angleDeg: Double, length: Float) = Offset(
            center.x + length * cos(Math.toRadians(angleDeg - 90)).toFloat(),
            center.y + length * sin(Math.toRadians(angleDeg - 90)).toFloat(),
        )

        drawCircle(Color.White, radius)
        drawCircle(Color(0xFF4F5BD5), radius - 3.dp.toPx(), style = Stroke(width = 6.dp.toPx()))
        for (i in 0 until 60) {
            val major = i % 5 == 0
            drawLine(
                Color(0xFF999999),
                point(i * 6.0, radius * if (major) 0.86f else 0.9f),
                point(i * 6.0, radius * 0.94f),
                strokeWidth = (if (major) 3.dp else 1.5.dp).toPx(),
            )
        }
        for (n in 1..12) {
            val layout = measurer.measure("$n", numberStyle)
            val p = point(n * 30.0, radius * 0.7f)
            drawText(layout, topLeft = Offset(p.x - layout.size.width / 2f, p.y - layout.size.height / 2f))
        }
        drawLine(
            Color(0xFF1F1B16), center, point((hour % 12) * 30.0 + minute * 0.5, radius * 0.45f),
            strokeWidth = 9.dp.toPx(), cap = StrokeCap.Round,
        )
        drawLine(
            Color(0xFFE53935), center, point(minute * 6.0, radius * 0.75f),
            strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round,
        )
        drawCircle(Color(0xFF1F1B16), 7.dp.toPx(), center)
    }
}
