package tr.mathapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tr.mathapp.model.BloomLevel
import tr.mathapp.ui.AppViewModel
import tr.mathapp.ui.Muted

@Composable
fun ResultScreen(vm: AppViewModel) {
    val r = vm.lastResult ?: return
    val color = Color(r.level.color)
    val nextLevel = BloomLevel.entries.getOrNull(r.level.ordinal + 1)
    val canGoNext = nextLevel != null && vm.progress.isUnlocked(r.topic, nextLevel)

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                when (r.stars) {
                    3 -> "🏆"
                    2 -> "🎉"
                    1 -> "👍"
                    else -> "💪"
                },
                fontSize = 80.sp,
            )
            Text(
                when (r.stars) {
                    3 -> "Mükemmel!"
                    2 -> "Çok güzel!"
                    1 -> "Basamağı geçtin!"
                    else -> "Tekrar deneyelim!"
                },
                fontSize = 30.sp, fontWeight = FontWeight.Bold, color = color,
            )
            Text("${r.topic.emoji} ${r.topic.title} · ${r.level.emoji} ${r.level.title}", color = Muted)
            Spacer(Modifier.height(16.dp))
            StarRow(r.stars, size = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text("${r.total} sorudan ${r.correct} doğru", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text("+${r.points} puan", fontSize = 18.sp, color = color, fontWeight = FontWeight.Bold)
            if (r.stars == 0) {
                Text(
                    "Sonraki basamağı açmak için 5 sorudan en az 3'ünü doğru yapmalısın.",
                    textAlign = TextAlign.Center, color = Muted, modifier = Modifier.padding(top = 8.dp),
                )
            }

            if (r.newBadges.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text("Yeni rozet kazandın! 🎖️", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                r.newBadges.forEach { badge ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3C4)),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(badge.emoji, fontSize = 36.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(badge.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(badge.description, color = Muted, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                if (canGoNext && nextLevel != null) {
                    Button(
                        onClick = { vm.startQuiz(r.topic, nextLevel) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(nextLevel.color)),
                        shape = RoundedCornerShape(16.dp),
                    ) { Text("Sonraki basamak: ${nextLevel.emoji} ${nextLevel.title}", fontSize = 17.sp, fontWeight = FontWeight.Bold) }
                }
                OutlinedButton(
                    onClick = { vm.startQuiz(r.topic, r.level) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                ) { Text("🔄 Yeni sorularla tekrar dene", fontSize = 16.sp) }
                OutlinedButton(
                    onClick = vm::back,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                ) { Text("⬅ ${r.topic.title} basamaklarına dön", fontSize = 16.sp) }
            }
        }
    }
}
