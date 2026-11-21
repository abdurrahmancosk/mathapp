package tr.mathapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tr.mathapp.data.Badge
import tr.mathapp.data.Progress
import tr.mathapp.model.BloomLevel
import tr.mathapp.model.Topic
import tr.mathapp.ui.AppViewModel
import tr.mathapp.ui.Muted
import tr.mathapp.ui.Wrong

@Composable
fun StatsScreen(vm: AppViewModel) {
    val p = vm.progress
    var editName by remember { mutableStateOf(false) }
    var confirmReset by remember { mutableStateOf(false) }

    if (editName) NameDialog(p.name, onDismiss = { editName = false }) { vm.setName(it); editName = false }
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("İlerleme sıfırlansın mı?") },
            text = { Text("Tüm yıldızlar, puanlar ve rozetler silinecek. Bu işlem geri alınamaz.") },
            confirmButton = {
                TextButton(onClick = { vm.resetProgress(); confirmReset = false }) { Text("Sıfırla", color = Wrong) }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Vazgeç") } },
        )
    }

    Scaffold(bottomBar = { MainBottomBar(vm) }, containerColor = MaterialTheme.colorScheme.background) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "📈 İlerlemem",
                        fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { editName = true }) { Icon(Icons.Filled.Edit, contentDescription = "Adını değiştir") }
                }
                if (p.name.isNotBlank()) Text("Öğrenci: ${p.name}", color = Muted)
            }
            item { Overview(p) }
            item { Section("Bloom basamaklarına göre başarı") { BloomBars(p) } }
            item { Section("Konulara göre yıldızlar") { TopicRows(p) } }
            item { Section("Rozetler (${p.badges.size}/${Badge.entries.size})") { Badges(p) } }
            item {
                TextButton(onClick = { confirmReset = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("İlerlemeyi sıfırla", color = Wrong)
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun Overview(p: Progress) {
    val accuracy = if (p.totalAnswered > 0) p.totalCorrect * 100 / p.totalAnswered else 0
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OverviewTile("🏅", "${p.points}", "Puan", Modifier.weight(1f))
        OverviewTile("✅", "${p.totalCorrect}/${p.totalAnswered}", "Doğru", Modifier.weight(1f))
        OverviewTile("🎯", "%$accuracy", "Başarı", Modifier.weight(1f))
        OverviewTile("🔥", "${p.streak}", "Gün", Modifier.weight(1f))
    }
}

@Composable
private fun OverviewTile(emoji: String, value: String, label: String, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier,
    ) {
        Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 22.sp)
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(label, fontSize = 12.sp, color = Muted)
        }
    }
}

@Composable
private fun BloomBars(p: Progress) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        BloomLevel.entries.forEach { level ->
            val answered = p.levelAnswered[level] ?: 0
            val correct = p.levelCorrect[level] ?: 0
            val ratio = if (answered > 0) correct / answered.toFloat() else 0f
            val color = Color(level.color)
            Column {
                Row {
                    Text("${level.emoji} ${level.title}", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(
                        if (answered == 0) "henüz yok" else "%${(ratio * 100).toInt()} ($correct/$answered)",
                        color = Muted, fontSize = 13.sp,
                    )
                }
                LinearProgressIndicator(
                    progress = { ratio },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.15f),
                    drawStopIndicator = {},
                )
            }
        }
    }
}

@Composable
private fun TopicRows(p: Progress) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Topic.entries.forEach { topic ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${topic.emoji} ${topic.title}", modifier = Modifier.width(130.dp), fontWeight = FontWeight.SemiBold)
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    BloomLevel.entries.forEach { level ->
                        val s = p.stars(topic, level)
                        Text(
                            if (s > 0) "$s★" else "·",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = if (s > 0) Color.White else Muted,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .then(
                                    if (s > 0) Modifier.background(Color(level.color)) else Modifier.background(Color(0xFFEEEEEE)),
                                )
                                .padding(vertical = 3.dp),
                        )
                    }
                }
            }
        }
        Text("Kutular soldan sağa: Hatırla → Anla → Uygula → Analiz Et → Değerlendir → Oluştur", fontSize = 11.sp, color = Muted)
    }
}

@Composable
private fun Badges(p: Progress) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Badge.entries.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { badge ->
                    val earned = badge in p.badges
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (earned) Color(0xFFFFF3C4) else Color(0xFFF2F2F2))
                            .padding(8.dp)
                            .alpha(if (earned) 1f else 0.45f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(if (earned) badge.emoji else "🔒", fontSize = 28.sp)
                        Text(badge.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text(badge.description, fontSize = 10.sp, color = Muted, textAlign = TextAlign.Center, lineHeight = 13.sp)
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun NameDialog(current: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adın ne?") },
        text = {
            OutlinedTextField(value = text, onValueChange = { text = it.take(20) }, singleLine = true, label = { Text("Ad") })
        },
        confirmButton = { TextButton(onClick = { onSave(text) }) { Text("Kaydet") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Vazgeç") } },
    )
}
