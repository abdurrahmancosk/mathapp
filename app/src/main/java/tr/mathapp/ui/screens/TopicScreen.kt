package tr.mathapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tr.mathapp.model.BloomLevel
import tr.mathapp.model.Topic
import tr.mathapp.ui.AppViewModel
import tr.mathapp.ui.Muted

@Composable
fun TopicScreen(vm: AppViewModel, topic: Topic) {
    val p = vm.progress
    Scaffold(
        topBar = { BackTopBar("${topic.emoji} ${topic.title}", Color(topic.color), onBack = vm::back) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text(topic.summary, color = Muted)
                Text(
                    "Basamakları sırayla çık! Bir basamaktan en az 1 yıldız (5 sorudan 3 doğru) alınca sonraki basamak açılır.",
                    fontSize = 13.sp, color = Muted, modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                )
            }
            items(BloomLevel.entries.reversed()) { level ->
                val unlocked = p.isUnlocked(topic, level)
                LevelCard(
                    level = level,
                    stars = p.stars(topic, level),
                    unlocked = unlocked,
                    onClick = { vm.startQuiz(topic, level) },
                )
            }
        }
    }
}

@Composable
private fun LevelCard(level: BloomLevel, stars: Int, unlocked: Boolean, onClick: () -> Unit) {
    val color = Color(level.color)
    Card(
        onClick = onClick,
        enabled = unlocked,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White, disabledContainerColor = Color(0xFFF1F1F1)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (unlocked) 2.dp else 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level.ordinal * 6).dp, end = ((BloomLevel.entries.size - 1 - level.ordinal) * 6).dp),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${level.ordinal + 1}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (unlocked) color else Color.Gray)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f).alpha(if (unlocked) 1f else 0.6f)) {
                Text("${level.emoji} ${level.title}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = if (unlocked) color else Color.Gray)
                Text(level.description, fontSize = 12.sp, color = Muted)
            }
            Spacer(Modifier.width(8.dp))
            if (unlocked) {
                StarRow(stars, size = 18.sp)
            } else {
                Icon(Icons.Filled.Lock, contentDescription = "Kilitli", tint = Color.Gray, modifier = Modifier.size(22.dp))
            }
        }
    }
}
