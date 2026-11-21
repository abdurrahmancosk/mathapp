package com.example.mathapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathapp.ui.theme.*
import com.example.mathapp.ui.viewmodel.HomeViewModel

@Composable
fun ProgressScreen(viewModel: HomeViewModel) {
    val stats by viewModel.stats.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "📈 İlerleme Raporu",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            if (stats != null) {
                ProgressOverviewCard(stats!!)
            } else {
                Text("Henüz veri yok. Etkinliklere başla!")
            }
        }

        item {
            Text(
                text = "Bloom Seviyeleri",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(viewModel.getAllBloomLevels()) { level ->
            BloomLevelProgressCard(
                level = level,
                description = viewModel.getBloomLevelDescription(level),
                color = getBloomLevelColor(level)
            )
        }

        item {
            Text(
                text = "Konular",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(viewModel.getAllTopics()) { topic ->
            TopicProgressCard(
                topic = topic,
                description = viewModel.getTopicDescription(topic)
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProgressOverviewCard(stats: com.example.mathapp.data.model.UserStats) {
    val accuracy = if (stats.totalQuestionsAnswered > 0)
        (stats.correctAnswers.toFloat() / stats.totalQuestionsAnswered * 100)
    else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = LightPurple.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Toplam Puan",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "⭐ ${stats.totalScore}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "Cevaplanan",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "📝 ${stats.totalQuestionsAnswered}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "Doğru Cevap",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "✅ ${stats.correctAnswers}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Doğruluk Oranı",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${accuracy.toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightGreen
                    )
                }
                LinearProgressIndicator(
                    progress = { accuracy / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = LightGreen,
                    trackColor = LightGreen.copy(alpha = 0.2f)
                )
            }

            if (stats.streakDays > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(LightOrange.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔥 ${stats.streakDays} Günlük Seri",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightOrange
                    )
                }
            }
        }
    }
}

@Composable
fun BloomLevelProgressCard(
    level: com.example.mathapp.data.model.BloomLevel,
    description: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bu seviyede ${level.ordinal + 1} etkinlik",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Button(
                onClick = {},
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = color)
            ) {
                Text("Devam Et", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun TopicProgressCard(
    topic: com.example.mathapp.data.model.MathTopic,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Puan: ${(1..100).random()} / 100",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "${(1..5).random()}⭐",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
