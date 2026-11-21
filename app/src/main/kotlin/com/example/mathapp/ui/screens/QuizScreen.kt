package com.example.mathapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathapp.data.model.Question
import com.example.mathapp.ui.theme.LightBlue
import com.example.mathapp.ui.theme.LightGreen
import com.example.mathapp.ui.theme.LightRed
import com.example.mathapp.ui.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit
) {
    val currentActivity by viewModel.currentActivity.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val score by viewModel.score.collectAsState()
    val correctAnswers by viewModel.correctAnswers.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()

    if (quizCompleted) {
        QuizCompletedScreen(
            activity = currentActivity,
            score = score,
            correctAnswers = correctAnswers,
            totalQuestions = questions.size,
            accuracy = viewModel.getAccuracy(),
            onRestart = { viewModel.restartQuiz() },
            onBack = onBackClick
        )
    } else {
        val currentQuestion = viewModel.getCurrentQuestion()

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            TopAppBar(
                title = {
                    Text(currentActivity?.title ?: "Quiz", fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = LightBlue.copy(alpha = 0.1f)
                )
            )

            if (currentQuestion != null && questions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Progress bar
                        LinearProgressIndicator(
                            progress = { (currentQuestionIndex + 1) / questions.size.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = LightGreen
                        )
                        Text(
                            text = "Soru ${currentQuestionIndex + 1}/${questions.size}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = currentQuestion.question,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Cevap Seç:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(
                        listOf(
                            currentQuestion.option1,
                            currentQuestion.option2,
                            currentQuestion.option3
                        )
                    ) { option ->
                        AnswerButton(
                            answer = option,
                            isCorrect = option == currentQuestion.correctAnswer,
                            onClick = {
                                viewModel.answerQuestion(option == currentQuestion.correctAnswer)
                            }
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = LightGreen.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💡 Açıklama",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = currentQuestion.explanation,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.previousQuestion() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                enabled = currentQuestionIndex > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightBlue,
                                    disabledContainerColor = LightBlue.copy(alpha = 0.5f)
                                )
                            ) {
                                Text("← Önceki")
                            }

                            Button(
                                onClick = { viewModel.nextQuestion() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                enabled = currentQuestionIndex < questions.size - 1,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightBlue,
                                    disabledContainerColor = LightBlue.copy(alpha = 0.5f)
                                )
                            ) {
                                Text("Sonraki →")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerButton(
    answer: String,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    var showResult by remember { mutableStateOf(false) }

    Button(
        onClick = {
            showResult = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (showResult) {
                if (isCorrect) LightGreen else LightRed
            } else {
                LightBlue
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        AnimatedContent(
            targetState = showResult,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { state ->
            if (state) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCorrect) "✅ Doğru!" else "❌ Yanlış",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = answer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun QuizCompletedScreen(
    activity: com.example.mathapp.data.model.Activity?,
    score: Int,
    correctAnswers: Int,
    totalQuestions: Int,
    accuracy: Float,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉 Tebrikler!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = LightGreen.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Etkinlik: ${activity?.title ?: "Quiz"}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Puan: $score",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightGreen
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅ Doğru", fontSize = 14.sp, color = Color.Gray)
                        Text("$correctAnswers/$totalQuestions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊 Doğruluk", fontSize = 14.sp, color = Color.Gray)
                        Text("${accuracy.toInt()}%", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                LinearProgressIndicator(
                    progress = { accuracy / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = LightGreen
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightBlue)
            ) {
                Text("Geri")
            }

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
            ) {
                Text("Tekrar")
            }
        }
    }
}
