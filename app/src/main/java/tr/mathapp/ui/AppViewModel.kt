package tr.mathapp.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDate
import tr.mathapp.data.Badge
import tr.mathapp.data.Progress
import tr.mathapp.data.ProgressStore
import tr.mathapp.model.BloomLevel
import tr.mathapp.model.Question
import tr.mathapp.model.QuestionGenerator
import tr.mathapp.model.Topic

sealed interface Screen {
    data object Home : Screen
    data object Stats : Screen
    data class TopicLevels(val topic: Topic) : Screen
    data object Quiz : Screen
    data object Result : Screen
}

class QuizSession(val topic: Topic, val level: BloomLevel, val questions: List<Question>) {
    var index by mutableIntStateOf(0)
    var correct by mutableIntStateOf(0)
    /** null while the current question is unanswered; otherwise whether it was answered correctly. */
    var lastAnswerCorrect by mutableStateOf<Boolean?>(null)
}

data class QuizResult(
    val topic: Topic,
    val level: BloomLevel,
    val correct: Int,
    val total: Int,
    val stars: Int,
    val points: Int,
    val newBadges: List<Badge>,
)

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val store = ProgressStore(app)
    private val generator = QuestionGenerator()

    var progress by mutableStateOf(store.load())
        private set
    var session by mutableStateOf<QuizSession?>(null)
        private set
    var lastResult by mutableStateOf<QuizResult?>(null)
        private set

    val backStack = mutableStateListOf<Screen>(Screen.Home)
    val current: Screen get() = backStack.last()

    fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    fun switchTab(screen: Screen) {
        backStack.clear()
        backStack.add(screen)
    }

    fun back() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    fun startQuiz(topic: Topic, level: BloomLevel) {
        session = QuizSession(topic, level, generator.quiz(topic, level))
        while (current == Screen.Quiz || current == Screen.Result) back()
        navigate(Screen.Quiz)
    }

    fun answer(correct: Boolean) {
        val s = session ?: return
        if (s.lastAnswerCorrect != null) return
        s.lastAnswerCorrect = correct
        if (correct) s.correct++
    }

    fun next() {
        val s = session ?: return
        if (s.index < s.questions.lastIndex) {
            s.index++
            s.lastAnswerCorrect = null
        } else {
            finishQuiz(s)
        }
    }

    private fun finishQuiz(s: QuizSession) {
        val total = s.questions.size
        val stars = when {
            s.correct == total -> 3
            s.correct >= total - 1 -> 2
            s.correct * 2 > total -> 1
            else -> 0
        }
        val points = s.correct * 10 + if (s.correct == total) 20 else 0
        val today = LocalDate.now().toEpochDay()
        val old = progress
        val streak = when (old.lastPlayDay) {
            today -> maxOf(old.streak, 1)
            today - 1 -> old.streak + 1
            else -> 1
        }
        val key = s.topic to s.level
        var updated = old.copy(
            stars = old.stars + (key to maxOf(old.stars[key] ?: 0, stars)),
            levelCorrect = old.levelCorrect + (s.level to (old.levelCorrect[s.level] ?: 0) + s.correct),
            levelAnswered = old.levelAnswered + (s.level to (old.levelAnswered[s.level] ?: 0) + total),
            points = old.points + points,
            quizzes = old.quizzes + 1,
            streak = streak,
            lastPlayDay = today,
        )
        val newBadges = Badge.entries.filter { it !in updated.badges && it.earned(updated) }
        updated = updated.copy(badges = updated.badges + newBadges)
        progress = updated
        store.save(updated)
        lastResult = QuizResult(s.topic, s.level, s.correct, total, stars, points, newBadges)
        session = null
        backStack[backStack.lastIndex] = Screen.Result
    }

    fun setName(name: String) {
        progress = progress.copy(name = name.trim().take(20))
        store.save(progress)
    }

    fun resetProgress() {
        progress = Progress(name = progress.name)
        store.save(progress)
    }
}
