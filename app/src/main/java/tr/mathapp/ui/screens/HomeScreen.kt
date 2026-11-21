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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tr.mathapp.data.Progress
import tr.mathapp.model.BloomLevel
import tr.mathapp.model.Topic
import tr.mathapp.ui.AppViewModel
import tr.mathapp.ui.Muted
import tr.mathapp.ui.Screen

@Composable
fun HomeScreen(vm: AppViewModel) {
    val p = vm.progress
    Scaffold(bottomBar = { MainBottomBar(vm) }, containerColor = MaterialTheme.colorScheme.background) { inner ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { Header(p) }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Konular", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }
            items(Topic.entries) { topic ->
                TopicCard(topic, p) { vm.navigate(Screen.TopicLevels(topic)) }
            }
        }
    }
}

@Composable
private fun Header(p: Progress) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF5C6BC0), Color(0xFF8E24AA))))
            .padding(20.dp),
    ) {
        Text(
            if (p.name.isBlank()) "Merhaba! 👋" else "Merhaba ${p.name}! 👋",
            color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold,
        )
        Text("Bugün matematik merdiveninde hangi basamağa çıkacaksın?", color = Color.White.copy(alpha = 0.9f))
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            HeaderStat("⭐", "${p.totalStars}", "Yıldız")
            HeaderStat("🏅", "${p.points}", "Puan")
            HeaderStat("🔥", "${p.streak}", "Gün serisi")
            HeaderStat("🎖️", "${p.badges.size}", "Rozet")
        }
    }
}

@Composable
private fun HeaderStat(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 22.sp)
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
    }
}

@Composable
private fun TopicCard(topic: Topic, p: Progress, onClick: () -> Unit) {
    val color = Color(topic.color)
    val max = BloomLevel.entries.size * Progress.MAX_STARS_PER_LEVEL
    val stars = p.topicStars(topic)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                topic.emoji,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(topic.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Text(topic.summary, fontSize = 12.sp, color = Muted, minLines = 3, maxLines = 3)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { stars / max.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.15f),
                drawStopIndicator = {},
            )
            Text("⭐ $stars / $max", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
