package com.example.mathapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.mathapp.data.model.*
import com.example.mathapp.ui.theme.*
import com.example.mathapp.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onActivitySelected: (Int) -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val selectedTopic by viewModel.selectedTopic.collectAsState()
    val selectedBloomLevel by viewModel.selectedBloomLevel.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header with greeting
            Text(
                text = "🎓 Matematik Öğren!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Stats card
            StatsCard(stats = stats)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Filter chips - Topics
            Text(
                text = "Konular",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            LazyColumn {
                items(viewModel.getAllTopics()) { topic ->
                    FilterChip(
                        selected = selectedTopic == topic,
                        onClick = {
                            if (selectedTopic == topic) {
                                viewModel.filterByTopic(null)
                            } else {
                                viewModel.filterByTopic(topic)
                            }
                        },
                        label = { Text(viewModel.getTopicDescription(topic)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Filter chips - Bloom Levels
            Text(
                text = "Bloom Seviyeleri",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            LazyColumn {
                items(viewModel.getAllBloomLevels()) { level ->
                    FilterChip(
                        selected = selectedBloomLevel == level,
                        onClick = {
                            if (selectedBloomLevel == level) {
                                viewModel.filterByBloomLevel(null)
                            } else {
                                viewModel.filterByBloomLevel(level)
                            }
                        },
                        label = { Text(viewModel.getBloomLevelDescription(level)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = "Etkinlikler",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(activities) { activity ->
            ActivityCard(
                activity = activity,
                bloomLevelDescription = viewModel.getBloomLevelDescription(activity.bloomLevel),
                topicDescription = viewModel.getTopicDescription(activity.topic),
                onClick = { onActivitySelected(activity.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatsCard(stats: UserStats?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Senin İstatistiklerin",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Puan", value = "${stats?.totalScore ?: 0}", emoji = "⭐")
                StatItem(label = "Cevap", value = "${stats?.totalQuestionsAnswered ?: 0}", emoji = "📝")
                StatItem(label = "Doğru", value = "${stats?.correctAnswers ?: 0}", emoji = "✅")
            }

            if (stats != null) {
                val accuracy =
                    if (stats.totalQuestionsAnswered > 0)
                        (stats.correctAnswers.toFloat() / stats.totalQuestionsAnswered * 100).toInt()
                    else 0

                LinearProgressIndicator(
                    progress = { accuracy / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    color = LightGreen
                )
                Text(
                    text = "Doğruluk: $accuracy%",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 24.sp)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ActivityCard(
    activity: Activity,
    bloomLevelDescription: String,
    topicDescription: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = getBloomLevelColor(activity.bloomLevel).copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = activity.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Start",
                    modifier = Modifier.size(24.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistantChip(
                    onClick = {},
                    label = { Text(bloomLevelDescription, fontSize = 10.sp) },
                    modifier = Modifier.size(minWidth = 80.dp, minHeight = 28.dp)
                )
                AssistantChip(
                    onClick = {},
                    label = { Text(topicDescription, fontSize = 10.sp) },
                    modifier = Modifier.size(minWidth = 80.dp, minHeight = 28.dp)
                )
                Text(
                    text = "⭐ ${activity.difficulty}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(LightYellow.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

fun getBloomLevelColor(level: BloomLevel): Color = when (level) {
    BloomLevel.REMEMBER -> LightRed
    BloomLevel.UNDERSTAND -> LightOrange
    BloomLevel.APPLY -> LightYellow
    BloomLevel.ANALYZE -> LightGreen
    BloomLevel.EVALUATE -> LightBlue
    BloomLevel.CREATE -> LightPurple
}
