package tr.mathapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tr.mathapp.model.BuildQuestion
import tr.mathapp.model.ChoiceQuestion
import tr.mathapp.model.Question
import tr.mathapp.ui.AppViewModel
import tr.mathapp.ui.Correct
import tr.mathapp.ui.CorrectBg
import tr.mathapp.ui.QuizSession
import tr.mathapp.ui.Wrong
import tr.mathapp.ui.WrongBg

@Composable
fun QuizScreen(vm: AppViewModel) {
    val s = vm.session ?: return
    val color = Color(s.level.color)
    var confirmExit by remember { mutableStateOf(false) }
    BackHandler { confirmExit = true }

    if (confirmExit) {
        AlertDialog(
            onDismissRequest = { confirmExit = false },
            title = { Text("Etkinlikten çıkılsın mı?") },
            text = { Text("Bu etkinlikteki cevapların kaydedilmeyecek.") },
            confirmButton = { TextButton(onClick = { confirmExit = false; vm.back() }) { Text("Çık") } },
            dismissButton = { TextButton(onClick = { confirmExit = false }) { Text("Devam et") } },
        )
    }

    Scaffold(
        topBar = { BackTopBar("${s.topic.emoji} ${s.topic.title} · ${s.level.title}", color, onBack = { confirmExit = true }) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Soru ${s.index + 1} / ${s.questions.size}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(12.dp))
                LinearProgressIndicator(
                    progress = { (s.index + if (s.lastAnswerCorrect != null) 1 else 0) / s.questions.size.toFloat() },
                    modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.15f),
                    drawStopIndicator = {},
                )
            }
            Spacer(Modifier.height(16.dp))
            val q = s.questions[s.index]
            QuestionCard(q)
            Spacer(Modifier.height(16.dp))
            when (q) {
                is ChoiceQuestion -> ChoiceAnswers(vm, s, q)
                is BuildQuestion -> BuildAnswer(vm, s, q, color)
            }
            AnimatedVisibility(visible = s.lastAnswerCorrect != null) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Feedback(q, s.lastAnswerCorrect == true)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = vm::next,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = color),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            if (s.index == s.questions.lastIndex) "Sonucu Gör 🎉" else "Devam Et ➜",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionCard(q: Question) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            val short = q.prompt.length <= 16
            Text(
                q.prompt,
                fontSize = if (short) 34.sp else 20.sp,
                fontWeight = if (short) FontWeight.Bold else FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = if (short) 42.sp else 28.sp,
                modifier = Modifier.fillMaxWidth(),
            )
            q.visual?.let { v ->
                Spacer(Modifier.height(14.dp))
                val table = v.contains(':')
                Text(
                    v,
                    fontSize = when {
                        v.length <= 4 -> 64.sp
                        table -> 20.sp
                        else -> 28.sp
                    },
                    lineHeight = if (table) 32.sp else 40.sp,
                    textAlign = if (table) TextAlign.Start else TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                )
            }
        }
    }
}

@Composable
private fun ChoiceAnswers(vm: AppViewModel, s: QuizSession, q: ChoiceQuestion) {
    var selected by rememberSaveable(s, s.index) { mutableStateOf<String?>(null) }
    val answered = s.lastAnswerCorrect != null
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        q.options.forEach { option ->
            val isAnswer = option == q.answer
            val (bg, fg) = when {
                answered && isAnswer -> CorrectBg to Correct
                answered && option == selected -> WrongBg to Wrong
                else -> Color.White to MaterialTheme.colorScheme.onSurface
            }
            OutlinedButton(
                onClick = {
                    if (!answered) {
                        selected = option
                        vm.answer(isAnswer)
                    }
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, if (answered && (isAnswer || option == selected)) fg else Color(0xFFDDDDDD)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = bg, contentColor = fg),
            ) {
                val mark = when {
                    answered && isAnswer -> "✅ "
                    answered && option == selected -> "❌ "
                    else -> ""
                }
                Text(mark + option, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun BuildAnswer(vm: AppViewModel, s: QuizSession, q: BuildQuestion, color: Color) {
    var a by rememberSaveable(s, s.index) { mutableIntStateOf(q.start.first) }
    var b by rememberSaveable(s, s.index) { mutableIntStateOf(q.start.second) }
    val answered = s.lastAnswerCorrect != null
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Stepper(q.labelA, a, q.rangeA, enabled = !answered) { a = it }
        Stepper(q.labelB, b, q.rangeB, enabled = !answered) { b = it }
        Text(
            q.render(a, b),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(color.copy(alpha = 0.1f))
                .padding(14.dp),
        )
        if (!answered) {
            Button(
                onClick = { vm.answer(q.check(a, b)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(16.dp),
            ) { Text("Kontrol Et ✔", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun Stepper(label: String, value: Int, range: IntRange, enabled: Boolean, onChange: (Int) -> Unit) {
    val big = range.last - range.first > 20
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (big) StepButton("−10", enabled && value > range.first) { onChange((value - 10).coerceIn(range)) }
                StepButton("−1", enabled && value > range.first) { onChange(value - 1) }
                Text(
                    "$value",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(64.dp),
                )
                StepButton("+1", enabled && value < range.last) { onChange(value + 1) }
                if (big) StepButton("+10", enabled && value < range.last) { onChange((value + 10).coerceIn(range)) }
            }
        }
    }
}

@Composable
private fun StepButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 8.dp),
    ) { Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
}

@Composable
private fun Feedback(q: Question, correct: Boolean) {
    val praise = remember(q) { PRAISE.random() }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (correct) CorrectBg else WrongBg),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                if (correct) praise else "Olsun, birlikte bakalım! 💪",
                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (correct) Correct else Wrong,
            )
            if (!correct) {
                val right = when (q) {
                    is ChoiceQuestion -> "Doğru cevap: ${q.answer}"
                    is BuildQuestion -> q.exampleSolution()?.let { (x, y) -> "Örnek bir doğru cevap:\n${q.render(x, y)}" } ?: ""
                }
                Text(right, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
            }
            Text("💡 ${q.explanation}", fontSize = 15.sp, modifier = Modifier.padding(top = 8.dp), lineHeight = 22.sp)
        }
    }
}

private val PRAISE = listOf("Harika! 🎉", "Süpersin! ⭐", "Aferin! 👏", "Çok iyi! 🌟", "Doğru! ✅")
